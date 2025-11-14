package guru.qa.niffler.database.repository.hibernate;

import guru.qa.niffler.database.repository.AuthUserRepository;
import guru.qa.niffler.model.entity.AuthUserEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthUserHibernateRepository implements AuthUserRepository {
    private final SessionFactory sessionFactory;

    @Override
    public AuthUserEntity save(AuthUserEntity user) {
        sessionFactory.getCurrentSession()
                .persist(user);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        AuthUserEntity foundUser = sessionFactory.getCurrentSession()
                .find(AuthUserEntity.class, id);
        return Optional.ofNullable(foundUser);
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        AuthUserEntity foundUser = sessionFactory.openSession()
                .createSelectionQuery("from AuthUserEntity where username = ?1", AuthUserEntity.class)
                .setParameter(1, username)
                .getSingleResultOrNull();
        return Optional.ofNullable(foundUser);
    }

    @Override
    public void delete(AuthUserEntity user) {
        sessionFactory.getCurrentSession()
                .remove(user);
    }
}
