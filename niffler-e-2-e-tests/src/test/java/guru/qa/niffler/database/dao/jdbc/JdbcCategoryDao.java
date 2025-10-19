package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.CategoryDao;
import guru.qa.niffler.model.entity.CategoryEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.database.DatabaseUtil.SPEND_DB_URL;

public class JdbcCategoryDao implements CategoryDao {

    public CategoryEntity save(CategoryEntity categoryEntity) {
        String insertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        UUID generatedId = null;
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, categoryEntity.getName());
            statement.setString(2, categoryEntity.getUsername());
            statement.setBoolean(3, categoryEntity.isArchived());

            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) generatedId = resultSet.getObject("id", UUID.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении категории", e);
        }
        return categoryEntity.setId(generatedId);
    }

    public Optional<CategoryEntity> findById(UUID id) {
        String selectSql = "SELECT * FROM category WHERE id = ?";
        CategoryEntity entity = new CategoryEntity();
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
            statement.setObject(1, UUID.class);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getObject("id", UUID.class));
                    entity.setName(resultSet.getString("name"));
                    entity.setUsername(resultSet.getString("username"));
                    entity.setArchived(resultSet.getBoolean("archived"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении траты по её id", e);
        }
        return Optional.of(entity);
    }
}
