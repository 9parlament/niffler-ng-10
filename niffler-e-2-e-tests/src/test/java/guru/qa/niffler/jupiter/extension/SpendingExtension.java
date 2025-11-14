package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.database.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.api.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static guru.qa.niffler.jupiter.annotation.UserType.RANDOM;
import static guru.qa.niffler.model.User.DEFAULT_USER;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final Namespace NAMESPACE = Namespace.create(SpendingExtension.class);
    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(ann -> {
                    if (isNotEmpty(ann.spending())) {
                        if (ann.user().equals(RANDOM)) {
                            UserJson testUser = context.getStore(UserExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);
                            List<SpendJson> spends = Arrays.stream(ann.spending())
                                    .map(spendingAnn -> createSpendByAnnotation(spendingAnn, testUser.getUsername()).spend())
                                    .toList();
                            testUser.getTestData().spends().addAll(spends);
                        } else {
                            SpendWithCategory[] spendsWithCategory = Arrays.stream(ann.spending())
                                    .map(spendingAnn -> createSpendByAnnotation(spendingAnn, DEFAULT_USER.getUsername()))
                                    .toArray(SpendWithCategory[]::new);
                            context.getStore(NAMESPACE).put(context.getUniqueId(), spendsWithCategory);
                        }
                    }
                });
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SpendWithCategory[] storedSpend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendWithCategory[].class);
        if (Objects.nonNull(storedSpend)) {
            Arrays.stream(storedSpend).forEach(spendWithCategory -> {
                spendClient.deleteSpend(spendWithCategory.spend());
                spendClient.deleteCategory(spendWithCategory.category());
            });
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (!extensionContext.getRequiredTestMethod().isAnnotationPresent(User.class)) {
            return false;
        }
        boolean supportsParameter = parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class)
                || parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
        if (supportsParameter && extensionContext.getRequiredTestMethod().getAnnotation(User.class).user().equals(RANDOM)) {
            throw new ParameterResolutionException("Передача параметра SpendJson возможна только в случае user = DEFAULT");
        }
        return supportsParameter;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        var spendsWithCategory = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendWithCategory[].class);
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class)
                ? spendsWithCategory[0].spend()
                : Arrays.stream(spendsWithCategory).map(SpendWithCategory::spend).toArray(SpendJson[]::new);
    }

    private SpendWithCategory createSpendByAnnotation(Spending spendingAnn, String username) {
        CategoryJson category = CategoryJson.create(username, spendingAnn.category());
        SpendJson spend = SpendJson.create(category, spendingAnn.amount(), spendingAnn.currency(), spendingAnn.description(), username);
        SpendJson createdSpend = spendClient.createSpend(spend);
        return new SpendWithCategory(createdSpend, createdSpend.getCategory());
    }

    private record SpendWithCategory(SpendJson spend, CategoryJson category) {}
}
