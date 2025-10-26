package guru.qa.niffler.database.dao.spring.jdbc;

import guru.qa.niffler.database.dao.AuthorityDao;
import guru.qa.niffler.model.entity.AuthorityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class SpringJdbcAuthorityDao implements AuthorityDao {
    private final DataSource dataSource;

    @Override
    public void saveAll(AuthorityEntity... authority) {
        String insertSql = "INSERT INTO authority(user_id, authority) VALUES (?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.batchUpdate(insertSql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setObject(2, authority[i].getAuthority());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                });
    }

    @Override
    public void deleteAllByUserId(UUID id) {
        String deleteSql = "DELETE FROM authority WHERE user_id = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(deleteSql, id);
    }
}
