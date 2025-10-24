package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.database.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.entity.CategoryEntity;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Objects;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {
    private static final Namespace NAMESPACE = Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(ann -> {
                    if (isNotEmpty(ann.categories())) {
                        Category categoryA = ann.categories()[0];
                        CategoryJson createdCategory = createCategoryByAnnotation(categoryA, ann.user().getUsername());
                        context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (Objects.nonNull(category)) spendClient.deleteCategory(category);
    }

    private CategoryJson createCategoryByAnnotation(Category categoryAnn, String username) {
        CategoryJson category = categoryAnn.isArchived()
                ? CategoryJson.create(username, categoryAnn.name()).setArchived(true)
                : CategoryJson.create(username, categoryAnn.name());
        CategoryEntity createdCategory = spendClient.createCategory(category);
        return CategoryJson.fromEntity(createdCategory);
    }
}
