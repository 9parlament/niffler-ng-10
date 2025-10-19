package guru.qa.niffler.database;

import guru.qa.niffler.database.dao.CategoryDao;
import guru.qa.niffler.database.dao.SpendDao;
import guru.qa.niffler.database.dao.jdbc.JdbcCategoryDao;
import guru.qa.niffler.database.dao.jdbc.JdbcSpendDao;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;

import java.util.Objects;

public class SpendDbClient {
    private final SpendDao spendDao = new JdbcSpendDao();
    private final CategoryDao categoryDao = new JdbcCategoryDao();

    public SpendEntity createSpend(SpendJson spend) {
        final SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (Objects.isNull(spendEntity.getCategory().getId())) {
            CategoryEntity category = categoryDao.save(spendEntity.getCategory());
            spendEntity.getCategory().setId(category.getId());
        }
        return spendDao.save(spendEntity);
    }

    public CategoryEntity createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return categoryDao.save(categoryEntity);
    }
}
