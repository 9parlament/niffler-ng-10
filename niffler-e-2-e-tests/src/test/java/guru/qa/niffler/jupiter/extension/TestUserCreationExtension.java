package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.user.UserFactory;
import guru.qa.niffler.data.user.User;
import guru.qa.niffler.data.user.UserType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.qa.niffler.config.Configuration.CFG;

public class TestUserCreationExtension implements SuiteExtension {
    static final Namespace NAMESPACE = Namespace.create(TestUserCreationExtension.class);

    @Override
    public void beforeSuite(ExtensionContext context) {
        context.getStore(NAMESPACE).put(this.getClass(), UserFactory.createUsersQueueByConfig());
    }

    //TODO: Скорректировать после реализации storage-классов и внедрения их в архитектуру проекта
    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void afterSuite(ExtensionContext context) {
        String sqlAuthDb = """
                DELETE FROM authority WHERE user_id IN (SELECT id FROM "user" WHERE username = ?);
                DELETE FROM "user" WHERE username = ?;
                """;
        String sqlUserDb = """
                DELETE FROM friendship WHERE addressee_id IN (SELECT id FROM "user" WHERE username = ?);
                DELETE FROM friendship WHERE  requester_id IN (SELECT id FROM "user" WHERE username = ?);
                DELETE FROM "user" WHERE username = ?;
                """;
        try (Connection connectionAuth = DriverManager.getConnection(CFG.dbJdbcUrl() + "niffler-auth", CFG.dbUsername(), CFG.dbPassword());
             Connection connectionUser = DriverManager.getConnection(CFG.dbJdbcUrl() + "niffler-userdata", CFG.dbUsername(), CFG.dbPassword());
             PreparedStatement stmntAuth = connectionAuth.prepareStatement(sqlAuthDb);
             PreparedStatement stmntUser = connectionUser.prepareStatement(sqlUserDb)
        ) {
            Map<UserType, Queue<User>> userStore = context.getStore(NAMESPACE).get(this.getClass(), HashMap.class);
            userStore.entrySet().stream().flatMap(entry -> entry.getValue().stream())
                    .flatMap(userN -> Stream.of(
                            Stream.of(userN),
                            userN.getFriends().stream(),
                            userN.getIncomeInvitations().stream(),
                            userN.getOutcomeInvitations().stream())
                    )
                    .flatMap(Function.identity())
                    .collect(Collectors.toSet())
                    .forEach(u -> {
                        try {
                            stmntAuth.setString(1, u.getUsername());
                            stmntAuth.setString(2, u.getUsername());
                            stmntAuth.executeUpdate();
                            stmntAuth.clearParameters();

                            stmntUser.setString(1, u.getUsername());
                            stmntUser.setString(2, u.getUsername());
                            stmntUser.setString(3, u.getUsername());
                            stmntUser.executeUpdate();
                            stmntUser.clearParameters();
                        } catch (SQLException e) {
                            throw new RuntimeException("Ошибка при удалении пользователя: ", e);
                        }
                    });
        }
    }
}
