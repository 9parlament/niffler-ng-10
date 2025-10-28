package guru.qa.niffler.database.dao.spring.jdbc.mapper;

import guru.qa.niffler.model.entity.CategoryEntity;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CategoryRowMapper implements RowMapper<CategoryEntity> {
    public static CategoryRowMapper INSTANCE = new CategoryRowMapper();

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CategoryEntity()
                .setId(rs.getObject("id", UUID.class))
                .setName(rs.getString("name"))
                .setUsername(rs.getString("username"))
                .setArchived(rs.getBoolean("archived"));
    }
}
