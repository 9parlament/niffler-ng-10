package guru.qa.niffler.database;

import guru.qa.niffler.database.dao.jdbc.JdbcCategoryDao;
import guru.qa.niffler.database.dao.jdbc.JdbcSpendDao;
import guru.qa.niffler.model.api.CategoryJson;
import guru.qa.niffler.model.api.SpendJson;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;

import java.util.Objects;

import static guru.qa.niffler.database.TransactionManager.executeInTransaction;


public class SpendDbClient {

    public SpendEntity createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        CategoryEntity categoryEntity = spendEntity.getCategory();
        return Objects.isNull(categoryEntity.getId())
                ? executeInTransaction(connection -> {
                    new JdbcCategoryDao(connection).save(categoryEntity);
                    return new JdbcSpendDao(connection).save(spendEntity);
                },
                Database.SPEND)
                : executeInTransaction(connection -> {
                    return new JdbcSpendDao(connection).save(spendEntity);
                },
                Database.SPEND);
    }

    public CategoryEntity createCategory(CategoryJson category) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
        return executeInTransaction(connection -> {
                    return new JdbcCategoryDao(connection).save(categoryEntity);
                },
                Database.SPEND);
    }

    public void deleteSpend(SpendJson spend) {
        executeInTransaction(connection -> {
                    new JdbcSpendDao(connection).deleteById(spend.getId());
                },
                Database.SPEND);
    }

    public void deleteCategory(CategoryJson category) {
        if (Objects.isNull(category.getId())) {
            executeInTransaction(connection -> {
                        JdbcCategoryDao categoryDao = new JdbcCategoryDao(connection);
                        categoryDao.findByUsernameAndCategoryName(category.getUsername(), category.getName())
                                .ifPresent(entity -> categoryDao.deleteById(entity.getId()));
                    },
                    Database.SPEND);
        } else {
            executeInTransaction(connection -> {
                        new JdbcCategoryDao(connection).deleteById(category.getId());
                    },
                    Database.SPEND);
        }
    }
}
