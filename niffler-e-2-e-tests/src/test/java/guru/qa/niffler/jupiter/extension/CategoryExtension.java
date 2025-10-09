package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
    private static final Namespace NAMESPACE = Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApi = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        Category category = AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class).get();
        CategoryJson createdCategory = spendApi.createCategory(CategoryJson.create(category.user().getUsername()));
        context.getStore(NAMESPACE).put(context.getUniqueId(), category.isArchived()
                ? spendApi.updateCategory(createdCategory.setArchived(true))
                : createdCategory
        );
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
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (!category.isArchived()) spendApi.updateCategory(category.setArchived(true));
    }
}
