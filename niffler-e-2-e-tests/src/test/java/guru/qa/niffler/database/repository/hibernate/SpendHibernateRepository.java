package guru.qa.niffler.database.repository.hibernate;

import guru.qa.niffler.database.repository.SpendRepository;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SpendHibernateRepository implements SpendRepository {
    private final SessionFactory sessionFactory;

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        sessionFactory.getCurrentSession()
                .persist(spendEntity);
        return spendEntity;
    }

    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        return sessionFactory.getCurrentSession()
                .merge(spendEntity);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        sessionFactory.getCurrentSession().persist(categoryEntity);
        return categoryEntity;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        CategoryEntity categoryEntity = session
                .find(CategoryEntity.class, id);
        return Optional.ofNullable(categoryEntity);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        CategoryEntity foundCategory = sessionFactory.openSession()
                .createSelectionQuery("from CategoryEntity c where c.username = ?1 and c.name = ?2", CategoryEntity.class)
                .setParameter(1, username)
                .setParameter(2, categoryName)
                .getSingleResultOrNull();
        return Optional.ofNullable(foundCategory);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        SpendEntity spendEntity = sessionFactory.getCurrentSession()
                .find(SpendEntity.class, id);
        return Optional.ofNullable(spendEntity);
    }

    @Override
    public List<SpendEntity> findAllByUsernameAndSpendDescription(String username, String description) {
        return sessionFactory.openSession()
                .createSelectionQuery("from SpendEntity s where s.username = ?1 and s.description = ?2", SpendEntity.class)
                .setParameter(1, username)
                .setParameter(2, description)
                .getResultList();
    }

    @Override
    public void delete(SpendEntity spendEntity) {
        sessionFactory.getCurrentSession()
                .remove(spendEntity);
    }

    @Override
    public void deleteCategory(CategoryEntity categoryEntity) {
        sessionFactory.getCurrentSession()
                .remove(categoryEntity);
    }
}
