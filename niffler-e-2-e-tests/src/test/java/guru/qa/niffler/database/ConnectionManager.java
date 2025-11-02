package guru.qa.niffler.database;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionManager {
    private static final Map<Database, DataSource> DATASOURCE_STORE = new ConcurrentHashMap<>();
    private static final Map<Long, Map<Database, Connection>> THREADED_CONN_POOL = new ConcurrentHashMap<>();

    public static void closeAllConnections() {
        THREADED_CONN_POOL.values().stream()
                .flatMap(db -> db.values().stream())
                .forEach(ConnectionManager::closeSilently);
    }

    static Connection getConnection(Database database) {
        return THREADED_CONN_POOL.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> new HashMap<>(Map.of(database, createConnectionTo(database)))
        ).computeIfAbsent(
                database,
                key -> createConnectionTo(database)
        );
    }

    static DataSource getDataSource(Database database) {
        return DATASOURCE_STORE.computeIfAbsent(
                database,
                db -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    dsBean.setUniqueResourceName(database.getDbName());
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    dsBean.getXaProperties().put("URL", database.connectionUrl());
                    return dsBean;
                }
        );
    }

    @SneakyThrows
    private static Connection createConnectionTo(Database database) {
        DataSource dataSource = DATASOURCE_STORE.computeIfAbsent(
                database,
                db -> getDataSource(database)
        );
        return dataSource.getConnection();
    }

    private static void closeSilently(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
}
