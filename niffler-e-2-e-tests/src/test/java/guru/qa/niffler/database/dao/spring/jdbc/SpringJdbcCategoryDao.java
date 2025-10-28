package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.CategoryDao;
import guru.qa.niffler.database.dao.spring.jdbc.mapper.CategoryRowMapper;
import guru.qa.niffler.model.entity.CategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcCategoryDao implements CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public CategoryEntity save(CategoryEntity category) {
        String insertSql = "INSERT INTO category(name, username, archived) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement statement = con.prepareStatement(insertSql);
                    statement.setString(1, category.getName());
                    statement.setString(2, category.getUsername());
                    statement.setBoolean(3, category.isArchived());
                    return statement;
                },
                keyHolder);
        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return category.setId(generatedKey);
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM category WHERE id = ?",
                CategoryRowMapper.INSTANCE,
                id));
    }

    @Override
    public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM category WHERE username = ? AND name = ?",
                CategoryRowMapper.INSTANCE,
                username, categoryName));
    }

    @Override
    public List<CategoryEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM category",
                CategoryRowMapper.INSTANCE);
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM category WHERE username = ?",
                CategoryRowMapper.INSTANCE,
                username);
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update("DELETE FROM category WHERE id = ?");
    }
}
