package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity save(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);

    Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName);

    List<CategoryEntity> findAll();

    List<CategoryEntity> findAllByUsername(String username);

    void deleteById(UUID id);
}
