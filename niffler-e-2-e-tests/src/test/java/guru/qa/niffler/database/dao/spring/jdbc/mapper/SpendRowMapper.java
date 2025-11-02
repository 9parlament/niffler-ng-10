package guru.qa.niffler.database.dao.spring.jdbc.mapper;

import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.valueOf;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SpendRowMapper implements RowMapper<SpendEntity> {
    public static SpendRowMapper INSTANCE = new SpendRowMapper();

    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity category = new CategoryEntity()
                .setId(rs.getObject("category_table_id", UUID.class))
                .setName(rs.getString("category_table_name"))
                .setUsername(rs.getString("category_table_username"))
                .setArchived(rs.getBoolean("category_table_archived"));

        return new SpendEntity()
                .setId(rs.getObject("id", UUID.class))
                .setUsername(rs.getString("username"))
                .setSpendDate(rs.getDate("spend_date"))
                .setCurrency(valueOf(rs.getString("currency")))
                .setAmount(rs.getDouble("amount"))
                .setDescription(rs.getString("description"))
                .setCategory(category);
    }
}
