package guru.qa.niffler.database.repository.jdbc;

import guru.qa.niffler.database.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipState;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.FriendshipEntity.FriendshipId;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcUserdataUserRepository implements UserdataUserRepository {
    private final Connection connection;

    @Override
    public UserEntity create(UserEntity user) {
        String insertSql = """
                INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setBytes(5, user.getPhoto());
            statement.setBytes(6, user.getPhotoSmall());
            statement.setString(7, user.getFullName());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                UUID generatedId = resultSet.getObject("id", UUID.class);
                return user.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        String selectSql = """
                SELECT u.*,
                       f.status,
                       f.requester_id,
                       f.addressee_id
                FROM "user" u
                JOIN friendship f ON u.id = f.requester_id OR u.id =  f.addressee_id
                WHERE ? IN (f.requester_id, f.addressee_id)
                """;
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setObject(1, id);
            Map<UUID, UserEntity> userStore = new HashMap<>();
            List<FriendshipEntity> userFriendshipStore = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID userId = resultSet.getObject("id", UUID.class);
                    if (!userStore.containsKey(userId)) {
                        UserEntity user = new UserEntity()
                                .setId(userId)
                                .setUsername(resultSet.getString("username"))
                                .setFullName(resultSet.getString("full_name"))
                                .setSurname(resultSet.getString("surname"))
                                .setFirstname(resultSet.getString("firstname"))
                                .setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")))
                                .setPhoto(resultSet.getBytes("photo"))
                                .setPhotoSmall(resultSet.getBytes("photo_small"));
                        userStore.put(userId, user);
                    }

                    if (userId.equals(id)) {
                        FriendshipEntity userFriendship = new FriendshipEntity()
                                .setFriendShipId(new FriendshipEntity.FriendshipId(
                                        resultSet.getObject("requester_id", UUID.class),
                                        resultSet.getObject("addressee_id", UUID.class)))
                                .setStatus(FriendshipState.valueOf(
                                        resultSet.getString("status")));
                        userFriendshipStore.add(userFriendship);
                    }
                }

                userFriendshipStore.forEach(friendshipEntity -> {
                    UUID requesterId = friendshipEntity.getFriendShipId().requester();
                    UserEntity requester = userStore.get(requesterId);
                    friendshipEntity.setRequester(requester);
                    requester.getRequests().add(friendshipEntity);

                    UUID addresseeId = friendshipEntity.getFriendShipId().addressee();
                    UserEntity addressee = userStore.get(addresseeId);
                    friendshipEntity.setAddressee(addressee);
                    addressee.getAddressees().add(friendshipEntity);
                });
            }
            return Objects.isNull(userStore.get(id))
                    ? Optional.empty()
                    : Optional.of(userStore.get(id));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении данных пользователя", e);
        }
    }

    @Override
    public void createFriendship(UserEntity requester, UserEntity addressee) {
        String insertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, FriendshipState.ACCEPTED.name());
            statement.addBatch();
            statement.clearParameters();

            statement.setObject(1, addressee.getId());
            statement.setObject(2, requester.getId());
            statement.setString(3, FriendshipState.ACCEPTED.name());
            statement.addBatch();

            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке создания отношения \"Дружба\"", e);
        }

        FriendshipEntity requesterFriendShip = FriendshipEntity.createFriendship(requester, addressee);
        FriendshipEntity addresseeFriendShip = FriendshipEntity.createFriendship(addressee, requester);
        requester.getRequests().add(requesterFriendShip);
        requester.getAddressees().add(addresseeFriendShip);
        addressee.getAddressees().add(addresseeFriendShip);
        addressee.getRequests().add(requesterFriendShip);
    }

    @Override
    public void createIncomeInvitation(UserEntity requester, UserEntity addressee) {
        String insertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, FriendshipState.PENDING.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке создания отношения \"Исходящее предложение дружбы\"", e);
        }

        FriendshipEntity invitation = FriendshipEntity.createInvitation(requester, addressee);
        requester.getRequests().add(invitation);
        addressee.getAddressees().add(invitation);
    }

    @Override
    public void createOutcomeInvitation(UserEntity addressee, UserEntity requester) {
        String insertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, FriendshipState.PENDING.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке создания отношения \"Входящее предложение дружбы\"", e);
        }

        FriendshipEntity invitation = FriendshipEntity.createInvitation(requester, addressee);
        requester.getRequests().add(invitation);
        addressee.getAddressees().add(invitation);
    }
}
