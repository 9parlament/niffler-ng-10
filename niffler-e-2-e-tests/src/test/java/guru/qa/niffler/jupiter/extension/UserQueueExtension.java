package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.user.User;
import guru.qa.niffler.data.user.UserType;
import guru.qa.niffler.jupiter.annotation.UserT;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UserQueueExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        ParameterResolver {
    private static final Namespace NAMESPACE = Namespace.create(UserQueueExtension.class);

    @Override
    @SuppressWarnings("unchecked")
    public void beforeEach(ExtensionContext context) {
        Map<UserType, Queue<User>> userStore = context.getStore(TestUserCreationExtension.NAMESPACE)
                .get(TestUserCreationExtension.class, HashMap.class);

        Map<UserType, Queue<User>> queue = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserT.class))
                .map(p -> p.getAnnotation(UserT.class).value())
                .collect(Collectors.toMap(
                        uType -> uType,
                        uType -> {
                            Queue<User> userQueue = new ConcurrentLinkedQueue<>();
                            userQueue.add(pollUserByType(uType, userStore));
                            return userQueue;
                        },
                        (q1, q2) -> {
                            q1.addAll(q2);
                            return q1;
                        }
                ));

        context.getStore(NAMESPACE).put(context.getUniqueId(), queue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterEach(ExtensionContext context) {
        Map<UserType, Queue<User>> executedTestQueue = context.getStore(NAMESPACE).get(context.getUniqueId(), HashMap.class);
        Map<UserType, Queue<User>> userStore = context.getStore(TestUserCreationExtension.NAMESPACE).get(TestUserCreationExtension.class, HashMap.class);
        executedTestQueue.forEach((type, users) -> userStore.get(type).addAll(users));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(User.class)
                && parameterContext.getParameter().isAnnotationPresent(UserT.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Map<UserType, Queue<User>> executingTestQueue = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), HashMap.class);
        UserType userType = parameterContext.getParameter().getAnnotation(UserT.class).value();
        User user = executingTestQueue.get(userType).poll();
        executingTestQueue.get(userType).add(user);
        return user;
    }

    private User pollUserByType(UserType type, Map<UserType, Queue<User>> userStore) {
        Optional<User> user = Optional.empty();
        StopWatch stopWatch = StopWatch.createStarted();

        while (user.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
            user = Optional.ofNullable(userStore.get(type).poll());
        }

        if (user.isEmpty())
            throw new IllegalStateException("Пользователь с типом %s оказался недоступен в течение 30 сек.".formatted(type));

        Allure.getLifecycle().updateTestCase(test ->
                test.setStart(new Date().getTime()));

        return user.get();
    }
}
