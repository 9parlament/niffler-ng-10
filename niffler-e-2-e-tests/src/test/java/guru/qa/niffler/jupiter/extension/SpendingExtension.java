package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.database.SpendDbClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.entity.SpendEntity;
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

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    public static final Namespace NAMESPACE = Namespace.create(SpendingExtension.class);
    private final SpendDbClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(ann -> {
                            if (isNotEmpty(ann.spending())) {
                                Spending spendingA = ann.spending()[0];
                                SpendWithCategory createdSpend = createSpendByAnnotation(spendingA, ann.user().getUsername());
                                context.getStore(NAMESPACE).put(context.getUniqueId(), createdSpend);
                            }
                        }
                );
    }

    @Override
    public void afterEach(ExtensionContext context) {
        SpendWithCategory storedSpend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendWithCategory.class);
        if (Objects.nonNull(storedSpend)) {
            spendClient.deleteSpend(storedSpend.spend());
            spendClient.deleteCategory(storedSpend.category());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendWithCategory.class).spend();
    }

    private SpendWithCategory createSpendByAnnotation(Spending spendingAnn, String username) {
        CategoryJson category = CategoryJson.create(username, spendingAnn.category());
        SpendJson spend = SpendJson.create(category, spendingAnn.amount(), spendingAnn.currency(), spendingAnn.description(),username);
        SpendEntity createdSpend = spendClient.createSpend(spend);
        return new SpendWithCategory(SpendJson.fromEntity(createdSpend), category);
    }

    private record SpendWithCategory(SpendJson spend, CategoryJson category) {}
}
