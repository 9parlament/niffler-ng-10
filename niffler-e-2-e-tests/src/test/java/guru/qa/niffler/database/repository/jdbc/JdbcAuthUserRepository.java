package guru.qa.niffler.database.repository.jdbc;

import guru.qa.niffler.database.repository.AuthUserRepository;
import guru.qa.niffler.model.entity.AuthUserEntity;
import guru.qa.niffler.model.entity.AuthorityEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcAuthUserRepository implements AuthUserRepository {
    private final Connection connection;

    @Override
    public AuthUserEntity save(AuthUserEntity user) {
        String userInsertSql = """
                INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        String authorityInsertSql = """
                INSERT INTO authority (user_id, authority)
                VALUES (?, ?);
                """;
        try (PreparedStatement userStmnt = connection.prepareStatement(userInsertSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityStmnt = connection.prepareStatement(authorityInsertSql)
        ) {
            userStmnt.setString(1, user.getUsername());
            userStmnt.setString(2, user.getPassword());
            userStmnt.setBoolean(3, user.getEnabled());
            userStmnt.setBoolean(4, user.getAccountNotExpired());
            userStmnt.setBoolean(5, user.getAccountNonLocked());
            userStmnt.setBoolean(6, user.getCredentialsNonExpired());
            userStmnt.executeUpdate();
            try (ResultSet resultSet = userStmnt.getGeneratedKeys()) {
                resultSet.next();
                user.setId(resultSet.getObject("id", UUID.class));
            }

            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityStmnt.setObject(1, authority.getAuthUser().getId());
                authorityStmnt.setString(2, authority.getAuthority());
                authorityStmnt.addBatch();
                authorityStmnt.clearParameters();
            }
            authorityStmnt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String selectSql = """
                SELECT u.*,
                a.id AS authority_id,
                a.authority AS authority_authority
                FROM "user" u
                JOIN authority a ON u.id = a.user_id
                WHERE u.id = ?;
                """;
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                AuthUserEntity authUser = null;
                while (resultSet.next()) {
                    authUser = mapRowToUser(resultSet, authUser);
                }
                return Optional.ofNullable(authUser);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении данных пользователя", e);
        }
    }

    @SneakyThrows
    private AuthUserEntity mapRowToUser(ResultSet resultSet, AuthUserEntity user) {
        if (Objects.isNull(user)) {
            user = new AuthUserEntity()
                    .setId(resultSet.getObject("id", UUID.class))
                    .setUsername(resultSet.getString("username"))
                    .setPassword(resultSet.getString("password"))
                    .setEnabled(resultSet.getBoolean("enabled"))
                    .setAccountNotExpired(resultSet.getBoolean("account_non_expired"))
                    .setAccountNonLocked(resultSet.getBoolean("account_non_locked"))
                    .setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"))
                    .setAuthorities(new ArrayList<>());
        }
        AuthorityEntity authority = new AuthorityEntity()
                .setId(resultSet.getObject("authority_id", UUID.class))
                .setAuthority(resultSet.getString("authority_authority"))
                .setAuthUser(user);
        user.getAuthorities().add(authority);
        return user;
    }
}
