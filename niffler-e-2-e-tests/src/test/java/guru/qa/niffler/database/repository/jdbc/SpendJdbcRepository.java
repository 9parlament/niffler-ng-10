package guru.qa.niffler.database.repository.jdbc;

import guru.qa.niffler.database.repository.SpendRepository;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.valueOf;

@RequiredArgsConstructor
public class SpendJdbcRepository implements SpendRepository {
    private final Connection connection;

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        String spendInsertSql = """
                INSERT INTO spend(username, spend_date, currency, amount, description, category_id)
                VALUES (?, ?, ?, ?, ?, ?)""";
        String categoryInsertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        try (PreparedStatement spendStatement = connection.prepareStatement(spendInsertSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement categoryStatement = connection.prepareStatement(categoryInsertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            CategoryEntity categoryEntity = spendEntity.getCategory();
            if (Objects.isNull(categoryEntity.getId())) {
                categoryStatement.setString(1, categoryEntity.getName());
                categoryStatement.setString(2, categoryEntity.getUsername());
                categoryStatement.setBoolean(3, categoryEntity.isArchived());
                categoryStatement.executeUpdate();
                try (ResultSet resultSet = categoryStatement.getGeneratedKeys()) {
                    resultSet.next();
                    UUID generatedId = resultSet.getObject("id", UUID.class);
                    categoryEntity.setId(generatedId);
                }
                spendEntity.setCategory(categoryEntity);
            }

            spendStatement.setString(1, spendEntity.getUsername());
            spendStatement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            spendStatement.setString(3, spendEntity.getCurrency().name());
            spendStatement.setDouble(4, spendEntity.getAmount());
            spendStatement.setString(5, spendEntity.getDescription());
            spendStatement.setObject(6, spendEntity.getCategory().getId());
            spendStatement.executeUpdate();
            try (ResultSet resultSet = spendStatement.getGeneratedKeys()) {
                resultSet.next();
                UUID generatedId = resultSet.getObject("id", UUID.class);
                return spendEntity.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении траты", e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        String updateSql = """
                UPDATE spend
                SET username = ?,
                    spend_date = ?,
                    currency = ?,
                    amount = ?,
                    description = ?,
                    category_id = ?
                WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, spendEntity.getUsername());
            statement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            statement.setString(3, spendEntity.getCurrency().name());
            statement.setDouble(4, spendEntity.getAmount());
            statement.setString(5, spendEntity.getDescription());
            statement.setObject(6, spendEntity.getCategory().getId());
            statement.setObject(7, spendEntity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении траты", e);
        }
        return spendEntity;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        String insertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
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

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        String selectSql = "SELECT * FROM category WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
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
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        String selectSql = "SELECT * FROM category WHERE username = ? AND name = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, username);
            statement.setString(2, categoryName);
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
    public Optional<SpendEntity> findById(UUID id) {
        String selectSql = """
                SELECT spend.*,
                       category.id AS category_table_id,
                       category.name AS category_table_name,
                       category.username AS category_table_username,
                       category.archived AS category_table_archived
                FROM spend
                JOIN category ON spend.category_id = category.id
                WHERE spend.id = ?;
                """;
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? Optional.of(mapRowToSpend(resultSet))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении траты", e);
        }
    }

    @Override
    public List<SpendEntity> findAllByUsernameAndSpendDescription(String username, String description) {
        String selectSql = """
                SELECT spend.*,
                       category.id AS category_table_id,
                       category.name AS category_table_name,
                       category.username AS category_table_username,
                       category.archived AS category_table_archived
                FROM spend
                JOIN category ON spend.category_id = category.id
                WHERE spend.username = ? AND spend.description = ?;
                """;
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, username);
            statement.setString(2, description);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<SpendEntity> spends = new ArrayList<>();
                while (resultSet.next()) {
                    SpendEntity spend = mapRowToSpend(resultSet);
                    spends.add(spend);
                }
                return spends;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении траты", e);
        }
    }

    @Override
    public void delete(SpendEntity spendEntity) {
        String deleteSql = "DELETE FROM spend WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setObject(1, spendEntity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении траты", e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity categoryEntity) {
        String spendDeleteSql = "DELETE FROM spend WHERE id = ?";
        String categoryDeleteSql = "DELETE FROM spend WHERE id = ?";
        try (PreparedStatement spendStatement = connection.prepareStatement(spendDeleteSql);
             PreparedStatement categoryStatement = connection.prepareStatement(categoryDeleteSql)
        ) {
            for (SpendEntity spend : categoryEntity.getSpends()) {
                spendStatement.setObject(1, spend.getId());
                spendStatement.addBatch();
                spendStatement.clearParameters();
            }
            spendStatement.executeBatch();

            categoryStatement.setObject(1, categoryEntity.getId());
            categoryStatement.executeUpdate();
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

    private SpendEntity mapRowToSpend(ResultSet resultSet) throws SQLException {
        CategoryEntity category = new CategoryEntity()
                .setId(resultSet.getObject("category_table_id", UUID.class))
                .setName(resultSet.getString("category_table_name"))
                .setUsername(resultSet.getString("category_table_username"))
                .setArchived(resultSet.getBoolean("category_table_archived"));

        return new SpendEntity()
                .setId(resultSet.getObject("id", UUID.class))
                .setUsername(resultSet.getString("username"))
                .setSpendDate(resultSet.getDate("spend_date"))
                .setCurrency(valueOf(resultSet.getString("currency")))
                .setAmount(resultSet.getDouble("amount"))
                .setDescription(resultSet.getString("description"))
                .setCategory(category);
    }
}
