package guru.qa.niffler.database;

import guru.qa.niffler.database.repository.SpendRepository;
import guru.qa.niffler.database.repository.hibernate.SpendHibernateRepository;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import guru.qa.niffler.service.SpendClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static guru.qa.niffler.database.Database.SPEND;
import static guru.qa.niffler.database.SessionFactoryStore.getSessionFactory;
import static guru.qa.niffler.database.TransactionManager.executeInXaTransaction;


public class SpendDbClient implements SpendClient {
    private final SpendRepository spendRepository;

    public SpendDbClient() {
        spendRepository = new SpendHibernateRepository(getSessionFactory(SPEND));
    }

    @Override
    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        CategoryEntity categoryEntity = spendEntity.getCategory();
        return SpendJson.fromEntity(
                Objects.isNull(categoryEntity.getId())
                        ? executeInXaTransaction(() -> {
                            spendRepository.createCategory(categoryEntity);
                            return spendRepository.create(spendEntity);
                        }
                )
                        : executeInXaTransaction(() -> spendRepository.create(spendEntity)));
    }

    @Override
    public SpendJson editSpend(SpendJson spend) {
        executeInXaTransaction(() -> {
            spendRepository.findById(spend.getId());
            spendRepository.update(SpendEntity.fromJson(spend));
            return null;
        });
        return spend;
    }

    public CategoryJson createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        executeInXaTransaction(() -> spendRepository.createCategory(categoryEntity));
        return CategoryJson.fromEntity(categoryEntity);
    }

    @Override
    public CategoryJson getCategoryByName(String username, String name) {
        return spendRepository.findCategoryByUsernameAndCategoryName(username, name)
                .map(CategoryJson::fromEntity)
                .orElse(null);
    }


    @Override
    public void deleteSpend(SpendJson spend) {
        executeInXaTransaction(() -> {
            spendRepository.findById(spend.getId())
                    .ifPresent(spendRepository::delete);
            return null;
        });
    }

    @Override
    public List<SpendJson> getSpendsByDescription(String username, String description) {
        List<SpendEntity> foundSpends = spendRepository.findAllByUsernameAndSpendDescription(username, description);
        return foundSpends.stream().map(SpendJson::fromEntity).collect(Collectors.toList());
    }


    public void deleteCategory(CategoryJson category) {
        executeInXaTransaction(() -> {
            spendRepository.findCategoryById(category.getId())
                    .ifPresent(spendRepository::deleteCategory);
            return null;
        });
    }
}
