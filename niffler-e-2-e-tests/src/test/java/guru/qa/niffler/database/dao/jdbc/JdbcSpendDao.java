package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.SpendDao;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;

import java.sql.Connection;
import java.sql.Date;
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
import static guru.qa.niffler.model.CurrencyValues.valueOf;

public class JdbcSpendDao implements SpendDao {

    public SpendEntity save(SpendEntity spendEntity) {
        String insertSql = """
                INSERT INTO spend(username, spend_date, currency, amount, description, category_id) 
                VALUES (?, ?, ?, ?, ?, ?)""";
        UUID generatedId = null;
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, spendEntity.getUsername());
            statement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            statement.setString(3, spendEntity.getCurrency().name());
            statement.setDouble(4, spendEntity.getAmount());
            statement.setString(5, spendEntity.getDescription());
            statement.setObject(6, spendEntity.getCategory().getId());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) generatedId = resultSet.getObject("id", UUID.class);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении траты", e);
        }
        return spendEntity.setId(generatedId);
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
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)
        ) {
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
    public List<SpendEntity> findAllByUsername(String username) {
        String selectSql = """
                SELECT spend.*,
                       category.id AS category_table_id,
                       category.name AS category_table_name,
                       category.username AS category_table_username,
                       category.archived AS category_table_archived
                FROM spend
                JOIN category ON spend.category_id = category.id
                WHERE spend.username = ?;
                """;
        List<SpendEntity> spends;
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                spends = new ArrayList<>();
                while (resultSet.next()) {
                    SpendEntity spend = mapRowToSpend(resultSet);
                    spends.add(spend);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка трат", e);
        }
        return spends;
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM spend WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(SPEND_DB_URL);
             PreparedStatement statement = connection.prepareStatement(deleteSql)
        ) {
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении траты", e);
        }
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
