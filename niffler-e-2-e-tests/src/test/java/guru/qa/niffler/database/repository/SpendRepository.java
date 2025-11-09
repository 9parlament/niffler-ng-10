package guru.qa.niffler.database.repository;

import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity create(SpendEntity spendEntity);

    SpendEntity update(SpendEntity spendEntity);

    CategoryEntity createCategory(CategoryEntity categoryEntity);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findAllByUsernameAndSpendDescription(String username, String description);

    void delete(SpendEntity spendEntity);

    void deleteCategory(CategoryEntity categoryEntity);
}
