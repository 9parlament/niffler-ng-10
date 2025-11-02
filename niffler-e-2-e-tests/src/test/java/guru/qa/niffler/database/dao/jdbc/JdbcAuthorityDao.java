package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.AuthorityDao;
import guru.qa.niffler.model.entity.AuthorityEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcAuthorityDao implements AuthorityDao {
    private final Connection connection;

    @Override
    public void saveAll(AuthorityEntity... authorities) {
        String insertSql = """
                INSERT INTO authority (user_id, authority)
                VALUES (?, ?);
                """;
        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (AuthorityEntity authority : authorities) {
                statement.setObject(1, authority.getAuthUser().getId());
                statement.setString(2, authority.getAuthority());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении прав доступа пользователя", e);
        }
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        String deleteSql = "DELETE FROM authority WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setObject(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении прав доступа пользователя", e);
        }
    }
}
