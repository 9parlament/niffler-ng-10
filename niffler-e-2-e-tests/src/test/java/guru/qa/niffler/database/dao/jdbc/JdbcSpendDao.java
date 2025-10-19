package guru.qa.niffler.database.dao.jdbc;

import guru.qa.niffler.database.dao.SpendDao;
import guru.qa.niffler.model.entity.SpendEntity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static guru.qa.niffler.database.DatabaseUtil.SPEND_DB_URL;

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
}
