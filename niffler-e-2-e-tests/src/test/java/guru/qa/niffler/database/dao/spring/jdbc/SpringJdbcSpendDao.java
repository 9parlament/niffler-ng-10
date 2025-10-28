package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.SpendDao;
import guru.qa.niffler.database.dao.spring.jdbc.mapper.SpendRowMapper;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcSpendDao implements SpendDao {
    private final DataSource dataSource;

    @Override
    public SpendEntity save(SpendEntity spendEntity) {
        String insertSql = """
                INSERT INTO spend(username, spend_date, currency, amount, description, category_id) 
                VALUES (?, ?, ?, ?, ?, ?)""";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql);
                    statement.setString(1, spendEntity.getUsername());
                    statement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
                    statement.setString(3, spendEntity.getCurrency().name());
                    statement.setDouble(4, spendEntity.getAmount());
                    statement.setString(5, spendEntity.getDescription());
                    statement.setObject(6, spendEntity.getCategory().getId());
                    return statement;
                },
                keyHolder);
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return spendEntity.setId(generatedKey);
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                selectSql,
                SpendRowMapper.INSTANCE,
                id));
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                selectSql,
                SpendRowMapper.INSTANCE,
                username);
    }

    @Override
    public void deleteById(UUID id) {
        String deleteSql = "DELETE FROM spend WHERE id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(deleteSql);
    }
}
