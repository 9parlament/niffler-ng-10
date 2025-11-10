package guru.qa.niffler.database.repository.spring.jdbc;

import guru.qa.niffler.database.dao.spring.jdbc.mapper.SpendRowMapper;
import guru.qa.niffler.database.repository.SpendRepository;
import guru.qa.niffler.database.repository.spring.jdbc.extractor.CategoryRowExtractor;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpendSpringJdbcRepository implements SpendRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        String spendInsertSql = """
                INSERT INTO spend(username, spend_date, currency, amount, description, category_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        String categoryInsertSql = """
                INSERT INTO category(username, name, archived)
                VALUES (?, ?, ?)
                """;
        CategoryEntity categoryEntity = spendEntity.getCategory();

        if (Objects.isNull(categoryEntity.getId())) {
            KeyHolder categoryKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                        PreparedStatement statement = con.prepareStatement(categoryInsertSql, Statement.RETURN_GENERATED_KEYS);
                        statement.setString(1, categoryEntity.getName());
                        statement.setString(2, categoryEntity.getUsername());
                        statement.setBoolean(3, categoryEntity.isArchived());
                        return statement;
                    },
                    categoryKeyHolder);
            UUID generatedCategoryKey = (UUID) categoryKeyHolder.getKeys().get("id");
            categoryEntity.setId(generatedCategoryKey);
        }

        KeyHolder spendKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(spendInsertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, spendEntity.getUsername());
                    statement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
                    statement.setString(3, spendEntity.getCurrency().name());
                    statement.setDouble(4, spendEntity.getAmount());
                    statement.setString(5, spendEntity.getDescription());
                    statement.setObject(6, spendEntity.getCategory().getId());
                    return statement;
                },
                spendKeyHolder);
        UUID generatedSpendKey = (UUID) spendKeyHolder.getKeys().get("id");
        return spendEntity.setId(generatedSpendKey);
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
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(updateSql);
            statement.setString(1, spendEntity.getUsername());
            statement.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            statement.setString(3, spendEntity.getCurrency().name());
            statement.setDouble(4, spendEntity.getAmount());
            statement.setString(5, spendEntity.getDescription());
            statement.setObject(6, spendEntity.getCategory().getId());
            statement.setObject(7, spendEntity.getId());
            return statement;
        });
        return spendEntity;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        String insertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, categoryEntity.getName());
                    statement.setString(2, categoryEntity.getUsername());
                    statement.setBoolean(3, categoryEntity.isArchived());
                    return statement;
                },
                keyHolder);
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return categoryEntity.setId(generatedKey);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        String selectSql = """
                SELECT spend.*,
                       category.id AS category_table_id,
                       category.name AS category_table_name,
                       category.username AS category_table_username,
                       category.archived AS category_table_archived
                FROM spend
                JOIN category ON spend.category_id = category.id
                WHERE spend.category_id = ?;
                """;
        CategoryEntity foundCategory = jdbcTemplate.query(
                selectSql,
                CategoryRowExtractor.INSTANCE,
                id);
        return Optional.ofNullable(foundCategory);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        String selectSql = """
                SELECT spend.*,
                       category.id AS category_table_id,
                       category.name AS category_table_name,
                       category.username AS category_table_username,
                       category.archived AS category_table_archived
                FROM spend
                JOIN category ON spend.category_id = category.id
                WHERE category.username = ? AND category.name = ?;
                """;
        CategoryEntity foundCategory = jdbcTemplate.query(
                selectSql,
                CategoryRowExtractor.INSTANCE,
                username,
                categoryName);
        return Optional.ofNullable(foundCategory);
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
        return jdbcTemplate.query(
                        selectSql,
                        SpendRowMapper.INSTANCE,
                        id)
                .stream().findFirst();
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
        return jdbcTemplate.query(
                selectSql,
                SpendRowMapper.INSTANCE,
                username,
                description);
    }

    @Override
    public void delete(SpendEntity spendEntity) {
        jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spendEntity.getId());
    }

    @Override
    public void deleteCategory(CategoryEntity categoryEntity) {
        categoryEntity.getSpends().forEach(this::delete);
        jdbcTemplate.update("DELETE FROM category WHERE id = ?", categoryEntity.getId());
    }
}
