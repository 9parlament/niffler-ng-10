package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.database.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.CategoryJson;
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

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {
    private static final Namespace NAMESPACE = Namespace.create(CategoryExtension.class);
    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(ann -> {
                    if (isNotEmpty(ann.categories())) {
                        if (ann.user().equals(RANDOM)) {
                            UserJson testUser = context.getStore(UserExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);
                            List<CategoryJson> categories = Arrays.stream(ann.categories())
                                    .map(categoryAnn -> createCategoryByAnnotation(categoryAnn, testUser.getUsername()))
                                    .toList();
                            testUser.getTestData().categories().addAll(categories);
                        } else {
                            CategoryJson[] categories = Arrays.stream(ann.categories())
                                    .map(categoryAnn -> createCategoryByAnnotation(categoryAnn, DEFAULT_USER.getUsername()))
                                    .toArray(CategoryJson[]::new);
                            context.getStore(NAMESPACE).put(context.getUniqueId(), categories);
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (!extensionContext.getRequiredTestMethod().isAnnotationPresent(User.class)) {
            return false;
        }

        boolean supportsParameter = parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class)
                || parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);

        if (supportsParameter && extensionContext.getRequiredTestMethod().getAnnotation(User.class).user().equals(RANDOM)) {
            throw new ParameterResolutionException("Передача параметра CategoryJson возможна только в случае user = DEFAULT");
        }
        return supportsParameter;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class)
                ? extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson[].class)[0]
                : extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson[].class);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson[] categories = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson[].class);
        if (Objects.nonNull(categories)) {
            Arrays.stream(categories).forEach(spendClient::deleteCategory);
        }
    }

    private CategoryJson createCategoryByAnnotation(Category categoryAnn, String username) {
        CategoryJson category = categoryAnn.isArchived()
                ? CategoryJson.create(username, categoryAnn.name()).setArchived(true)
                : CategoryJson.create(username, categoryAnn.name());
        return spendClient.createCategory(category);
    }
}
