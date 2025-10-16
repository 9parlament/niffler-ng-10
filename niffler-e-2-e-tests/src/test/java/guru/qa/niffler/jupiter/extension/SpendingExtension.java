package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendApiClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final Namespace NAMESPACE = Namespace.create(SpendingExtension.class);
    private final SpendApiClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(ann -> {
                            if (isNotEmpty(ann.spending())) {
                                Spending spending = ann.spending()[0];
                                CategoryJson category = CategoryJson.create(ann.user().getUsername(), spending.category());
                                var reqBody = SpendJson.create(category, spending.amount(), spending.description(), ann.user().getUsername());
                                SpendJson createdS = spendClient.createSpend(reqBody);
                                context.getStore(NAMESPACE).put(context.getUniqueId(), createdS);
                            }
                        }
                );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SpendJson storedSpending = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);
        if (Objects.nonNull(storedSpending))
            spendClient.removeSpend(List.of(storedSpending.getId().toString()), storedSpending.getUsername());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }
}
