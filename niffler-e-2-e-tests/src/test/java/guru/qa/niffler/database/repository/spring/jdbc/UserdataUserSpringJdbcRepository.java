package guru.qa.niffler.database.repository.spring.jdbc;

import guru.qa.niffler.database.repository.UserdataUserRepository;
import guru.qa.niffler.database.repository.spring.jdbc.extractor.UserdataUserRowExtractor;
import guru.qa.niffler.model.FriendshipState;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserdataUserSpringJdbcRepository implements UserdataUserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public UserEntity create(UserEntity user) {
        String insertSql = """
                INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, user.getUsername());
                    statement.setString(2, user.getCurrency().name());
                    statement.setString(3, user.getFirstname());
                    statement.setString(4, user.getSurname());
                    statement.setBytes(5, user.getPhoto());
                    statement.setBytes(6, user.getPhotoSmall());
                    statement.setString(7, user.getFullName());
                    return statement;
                },
                keyHolder
        );
        UUID generatedId = (UUID) keyHolder.getKeys().get("id");
        return user.setId(generatedId);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        String insertSql = """
                SELECT
                    u.*,
                    f.requester_id,
                    f.addressee_id,
                    f.status
                FROM "user" u
                JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id
                WHERE ? IN (f.requester_id, f.addressee_id);
                """;
        Map<UUID, UserEntity> userStore = jdbcTemplate.query(
                insertSql,
                UserdataUserRowExtractor.INSTANCE,
                id
        );
        return Objects.nonNull(userStore)
                ? Optional.ofNullable(userStore.get(id))
                : Optional.empty();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        String insertSql = """
                SELECT
                    u.*,
                    f.requester_id,
                    f.addressee_id,
                    f.status
                FROM "user" u
                JOIN friendship f ON u.id = f.requester_id OR u.id = f.addressee_id
                WHERE ? IN (
                            SELECT username FROM "user" WHERE id IN (f.requester_id, f.addressee_id)
                );
                """;
        Map<UUID, UserEntity> userStore = jdbcTemplate.query(
                insertSql,
                UserdataUserRowExtractor.INSTANCE,
                username
        );
        return Objects.nonNull(userStore)
                ? userStore.values().stream().filter(u -> u.getUsername().equals(username)).findFirst()
                : Optional.empty();
    }

    @Override
    public void createFriendship(UserEntity requester, UserEntity addressee) {
        String insertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.batchUpdate(
                insertSql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (i == 0) {
                            ps.setObject(1, requester.getId());
                            ps.setObject(2, addressee.getId());
                            ps.setObject(3, FriendshipState.ACCEPTED.name());
                        } else {
                            ps.setObject(1, addressee.getId());
                            ps.setObject(2, requester.getId());
                            ps.setObject(3, FriendshipState.ACCEPTED.name());
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return 2;
                    }
                }
        );
        FriendshipEntity requesterFriendShip = FriendshipEntity.createFriendship(requester, addressee);
        FriendshipEntity addresseeFriendShip = FriendshipEntity.createFriendship(addressee, requester);
        requester.getRequests().add(requesterFriendShip);
        requester.getAddressees().add(addresseeFriendShip);
        addressee.getAddressees().add(addresseeFriendShip);
        addressee.getRequests().add(requesterFriendShip);
    }

    @Override
    public void createInvitation(UserEntity requester, UserEntity addressee) {
        String insertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(insertSql);
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, FriendshipState.PENDING.name());
            return statement;
        });
        FriendshipEntity invitation = FriendshipEntity.createInvitation(requester, addressee);
        requester.getRequests().add(invitation);
        addressee.getAddressees().add(invitation);
    }
}
