package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.AuthUserDao;
import guru.qa.niffler.model.entity.AuthUserEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcAuthUserDao implements AuthUserDao {
    private final Connection connection;

    @Override
    public AuthUserEntity save(AuthUserEntity userEntity) {
        String insertSql = """
                INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, userEntity.getUsername());
            statement.setString(2, userEntity.getPassword());
            statement.setBoolean(3, userEntity.getEnabled());
            statement.setBoolean(4, userEntity.getAccountNotExpired());
            statement.setBoolean(5, userEntity.getAccountNonLocked());
            statement.setBoolean(6, userEntity.getCredentialsNonExpired());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                return userEntity.setId(resultSet.getObject("id", UUID.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM \"user\" WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя");
        }
    }
}
