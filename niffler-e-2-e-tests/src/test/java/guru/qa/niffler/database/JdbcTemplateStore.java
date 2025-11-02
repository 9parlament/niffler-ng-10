package guru.qa.niffler.database;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JdbcTemplateStore {
    private static final Map<Database, JdbcTemplate> STORE = new ConcurrentHashMap<>();

    static JdbcTemplate getJdbcTemplate(Database database) {
        return STORE.computeIfAbsent(
                database,
                db -> new JdbcTemplate(ConnectionManager.getDataSource(db))
        );
    }
}
