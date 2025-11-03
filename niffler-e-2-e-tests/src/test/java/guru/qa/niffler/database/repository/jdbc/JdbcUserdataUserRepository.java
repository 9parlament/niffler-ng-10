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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JdbcUserdataUserRepository implements UserdataUserRepository {
    private final Connection connection;

    @Override
    public UserEntity create(UserEntity user) {
        String userInsertSql = """
                INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        String friendshipInsertSql = """
                INSERT INTO friendship(requester_id, addressee_id, status)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement userStatement = connection.prepareStatement(userInsertSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement friendshipStatement = connection.prepareStatement(friendshipInsertSql)
        ) {
            Stream.of(
                            Stream.of(user),
                            user.getRequests().stream().map(FriendshipEntity::getAddressee),
                            user.getAddressees().stream().map(FriendshipEntity::getRequester)
                    )
                    .flatMap(Function.identity())
                    .collect(Collectors.toSet())
                    .forEach(userEntity -> {
                                try {
                                    userStatement.setString(1, userEntity.getUsername());
                                    userStatement.setString(2, userEntity.getCurrency().name());
                                    userStatement.setString(3, userEntity.getFirstname());
                                    userStatement.setString(4, userEntity.getSurname());
                                    userStatement.setBytes(5, userEntity.getPhoto());
                                    userStatement.setBytes(6, userEntity.getPhotoSmall());
                                    userStatement.setString(7, userEntity.getFullname());
                                    userStatement.executeUpdate();
                                    userStatement.clearParameters();

                                    try (ResultSet resultSet = userStatement.getGeneratedKeys()) {
                                        resultSet.next();
                                        UUID generatedId = resultSet.getObject("id", UUID.class);
                                        userEntity.setId(generatedId);
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException("Ошибка при сохранении пользователя", e);
                                }
                            }
                    );
            Stream.concat(
                            user.getRequests().stream(),
                            user.getAddressees().stream()
                    )
                    .collect(Collectors.toSet())
                    .forEach(friendshipEntity -> {
                                try {
                                    friendshipStatement.setObject(1, friendshipEntity.getRequester().getId());
                                    friendshipStatement.setObject(2, friendshipEntity.getAddressee().getId());
                                    friendshipStatement.setString(3, friendshipEntity.getStatus().name());
                                    friendshipStatement.executeUpdate();
                                    friendshipStatement.clearParameters();
                                } catch (SQLException e) {
                                    throw new RuntimeException("Ошибка при сохранении отношения", e);
                                }
                            }
                    );
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
        return user;
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
                                .setFullname(resultSet.getString("full_name"))
                                .setSurname(resultSet.getString("surname"))
                                .setFirstname(resultSet.getString("firstname"))
                                .setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")))
                                .setPhoto(resultSet.getBytes("photo"))
                                .setPhotoSmall(resultSet.getBytes("photo_small"));
                        userStore.put(userId, user);
                    }

                    if (userId.equals(id)) {
                        FriendshipEntity userFriendship = new FriendshipEntity()
                                .setFriendShipId(new FriendshipId(
                                        resultSet.getObject("requester_id", UUID.class),
                                        resultSet.getObject("addressee_id", UUID.class)))
                                .setStatus(FriendshipState.valueOf(
                                        resultSet.getString("status")));
                        userFriendshipStore.add(userFriendship);
                    }
                }

                userFriendshipStore.forEach(friendshipEntity -> {
                    UUID requesterId = friendshipEntity.getFriendShipId().requesterId();
                    UserEntity requester = userStore.get(requesterId);
                    friendshipEntity.setRequester(requester);
                    requester.getRequests().add(friendshipEntity);

                    UUID addresseeId = friendshipEntity.getFriendShipId().addresseeId();
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
