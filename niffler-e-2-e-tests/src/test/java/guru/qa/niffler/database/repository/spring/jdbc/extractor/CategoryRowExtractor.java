package guru.qa.niffler.database.repository.spring.jdbc.extractor;

import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.valueOf;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CategoryRowExtractor implements ResultSetExtractor<CategoryEntity> {
    public static final CategoryRowExtractor INSTANCE = new CategoryRowExtractor();

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        CategoryEntity category = null;
        while (rs.next()) {
            if (Objects.isNull(category)) {
                category = new CategoryEntity()
                        .setId(rs.getObject("category_table_id", UUID.class))
                        .setName(rs.getString("category_table_name"))
                        .setUsername(rs.getString("category_table_username"))
                        .setArchived(rs.getBoolean("category_table_archived"));
            }
            SpendEntity spend = new SpendEntity()
                    .setId(rs.getObject("id", UUID.class))
                    .setUsername(rs.getString("username"))
                    .setSpendDate(rs.getDate("spend_date"))
                    .setCurrency(valueOf(rs.getString("currency")))
                    .setAmount(rs.getDouble("amount"))
                    .setDescription(rs.getString("description"))
                    .setCategory(category);

            category.getSpends().add(spend);
        }
        return category;
    }
}
