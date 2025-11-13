package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.common.utils.NifflerFaker;
import guru.qa.niffler.database.UserDbClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.UserJson;
import guru.qa.niffler.model.test.TestData;
import guru.qa.niffler.service.UserClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;

import static guru.qa.niffler.jupiter.annotation.UserType.DEFAULT;
import static guru.qa.niffler.jupiter.annotation.UserType.RANDOM;

public class UserExtension implements BeforeEachCallback, ParameterResolver {
    public static final Namespace NAMESPACE = Namespace.create(UserExtension.class);
    private final UserClient userClient = new UserDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnn -> {
                    if (isAnnotationDataInvalid(userAnn)) {
                        throw new IllegalStateException("Дружеские связи должны отсутствовать, если user равен DEFAULT");
                    }
                    if (userAnn.user().equals(RANDOM)) {
                        String username = NifflerFaker.randomUserName();
                        String password = NifflerFaker.randomPassword();
                        UserJson createdUser = userClient.createUser(username, password);

                        var incomeInvitations = userClient.createIncomeInvitations(createdUser, userAnn.incomeInvitations());
                        var outcomeInvitations = userClient.createOutcomeInvitations(createdUser, userAnn.outcomeInvitations());
                        var friendShips = userClient.createFriendShip(createdUser, userAnn.friends());

                        TestData userData = new TestData(password,
                                incomeInvitations,
                                outcomeInvitations,
                                friendShips,
                                new ArrayList<>(),
                                new ArrayList<>());

                        createdUser.setTestData(userData);
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdUser);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        boolean isAnnotationValid = extensionContext.getRequiredTestMethod().isAnnotationPresent(User.class)
                && extensionContext.getRequiredTestMethod().getAnnotation(User.class).user().equals(RANDOM);
        if (!isAnnotationValid && parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)) {
            throw new ParameterResolutionException("Передача параметра UserJson возможна только в случае user = RANDOM");
        }
        return isAnnotationValid && parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }

    private boolean isAnnotationDataInvalid(User userAnn) {
        return userAnn.user().equals(DEFAULT) && (userAnn.incomeInvitations() != 0
                || userAnn.outcomeInvitations() != 0
                || userAnn.friends() != 0);
    }
}
