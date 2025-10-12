package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.service.AuthApiClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import static guru.qa.niffler.config.Configuration.CFG;
import static guru.qa.niffler.model.User.DEFAULT_USER;

//TODO: Упразднить экстеншн после переноса логики по созданию умолчательног пользователя в TestUserCreationExtension
public class DefaultUserCreationExtension implements SuiteExtension {

    @Override
    @SneakyThrows
    public void beforeSuite(ExtensionContext context) {
        new AuthApiClient().register(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword());
    }

    @Override
    @SneakyThrows
    public void afterSuite(ExtensionContext context) {
        String sql = """
                DELETE FROM authority WHERE user_id IN (SELECT id FROM "user" WHERE username = ?);
                DELETE FROM "user" WHERE username = ?;
                """;
        try (Connection connection = DriverManager.getConnection(CFG.dbJdbcUrl() + "niffler-auth", CFG.dbUsername(), CFG.dbPassword());
             PreparedStatement stmnt = connection.prepareStatement(sql)
        ) {
            stmnt.setString(1, DEFAULT_USER.getUsername());
            stmnt.setString(2, DEFAULT_USER.getUsername());
            stmnt.executeUpdate();
        }
    }
}
