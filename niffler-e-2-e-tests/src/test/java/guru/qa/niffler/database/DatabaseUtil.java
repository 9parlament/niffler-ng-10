package guru.qa.niffler.database;

import lombok.NoArgsConstructor;

import static guru.qa.niffler.config.Configuration.CFG;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DatabaseUtil {
    public static final String SPEND_DB_URL = connectionUrl("niffler-spend");
    public static final String USERDATA_DB_URL = connectionUrl("niffler-userdata");
    public static final String AUTH_DB_URL = connectionUrl("niffler-auth");
    public static final String CURRENCY_DB_URL = connectionUrl("niffler-currency");

    private static String connectionUrl(String dbName) {
        final String jdbcUrlPattern = "%s%s?user=%s&password=%s";
        return jdbcUrlPattern.formatted(CFG.dbJdbcUrl(), dbName, CFG.dbUsername(), CFG.dbPassword());
    }
}
