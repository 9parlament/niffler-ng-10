package guru.qa.niffler.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static guru.qa.niffler.config.Configuration.CFG;

@RequiredArgsConstructor
public enum Database {
    SPEND("niffler-spend"),
    USERDATA("niffler-userdata"),
    AUTH("niffler-auth"),
    CURRENCY("niffler-currency");

    @Getter private final String dbName;

    public String connectionUrl() {
        String jdbcUrlPattern = "%s%s?user=%s&password=%s";
        return jdbcUrlPattern.formatted(CFG.dbJdbcUrl(), dbName, CFG.dbUsername(), CFG.dbPassword());
    }
}
