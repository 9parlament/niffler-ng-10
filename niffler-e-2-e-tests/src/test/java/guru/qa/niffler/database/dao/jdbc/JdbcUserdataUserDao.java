package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.UserdataUserDao;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.entity.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.database.DatabaseUtil.USERDATA_DB_URL;

public class JdbcUserdataUserDao implements UserdataUserDao {

    @Override
    public UserEntity save(UserEntity user) {
        String insertSql = """
                INSERT INTO user (username, currency, firstname, surname, photo, photo_small, full_name)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = DriverManager.getConnection(USERDATA_DB_URL);
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setBytes(5, user.getPhoto());
            statement.setBytes(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());

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
        String selectSql = "SELECT * from user WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(USERDATA_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRowToUser(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении данных пользователя");
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        String selectSql = "SELECT * from user WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(USERDATA_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRowToUser(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении данных пользователя");
        }
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM user WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(USERDATA_DB_URL);
             PreparedStatement statement = connection.prepareStatement(deleteSql)
        ) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя");
        }
    }

    private UserEntity mapRowToUser(ResultSet resultSet) throws SQLException {
        return new UserEntity()
                .setId(resultSet.getObject("id", UUID.class))
                .setUsername(resultSet.getString("username"))
                .setFullname(resultSet.getString("full_name"))
                .setSurname(resultSet.getString("surname"))
                .setFirstname(resultSet.getString("firstname"))
                .setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")))
                .setPhoto(resultSet.getBytes("photo"))
                .setPhotoSmall(resultSet.getBytes("photo_small"));
    }
}
