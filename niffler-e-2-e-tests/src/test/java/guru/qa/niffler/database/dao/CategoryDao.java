package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.CategoryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity save(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);
}
