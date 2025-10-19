package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.CategoryDao;
import guru.qa.niffler.model.entity.CategoryEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.database.DatabaseUtil.SPEND_DB_URL;

public class JdbcCategoryDao implements CategoryDao {

    public CategoryEntity save(CategoryEntity categoryEntity) {
        String insertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, categoryEntity.getName());
            statement.setString(2, categoryEntity.getUsername());
            statement.setBoolean(3, categoryEntity.isArchived());

            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                UUID generatedId = resultSet.getObject("id", UUID.class);
                return categoryEntity.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении категории", e);
        }
    }

    public Optional<CategoryEntity> findById(UUID id) {
        String selectSql = "SELECT * FROM category WHERE id = ?";
        CategoryEntity entity = new CategoryEntity();
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
            statement.setObject(1, UUID.class);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRowToCategory(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении категории по её id", e);
        }
    }

    @Override
    public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
        String selectSql = "SELECT * FROM category WHERE username = ? AND name = ?";
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql);
        ) {
            statement.setString(1, username);
            statement.setString(2, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRowToCategory(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении категории", e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        String selectSql = "SELECT * FROM category WHERE username = ?";
        List<CategoryEntity> categories;
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                categories = new ArrayList<>();
                while (resultSet.next()) {
                    CategoryEntity category = mapRowToCategory(resultSet);
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении категории", e);
        }
        return categories;
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM category WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(deleteSql)
        ) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении категории", e);
        }
    }

    private CategoryEntity mapRowToCategory(ResultSet resultSet) throws SQLException {
        return new CategoryEntity()
                .setId(resultSet.getObject("id", UUID.class))
                .setName(resultSet.getString("name"))
                .setUsername(resultSet.getString("username"))
                .setArchived(resultSet.getBoolean("archived"));
    }
}
