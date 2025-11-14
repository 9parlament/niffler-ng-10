package guru.qa.niffler.database;

import guru.qa.niffler.model.entity.AuthUserEntity;
import guru.qa.niffler.model.entity.AuthorityEntity;
import guru.qa.niffler.model.entity.CategoryEntity;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.SpendEntity;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static jakarta.persistence.PersistenceUnitTransactionType.JTA;
import static lombok.AccessLevel.PRIVATE;
import static org.hibernate.cfg.JdbcSettings.JAKARTA_JTA_DATASOURCE;
import static org.hibernate.cfg.MappingSettings.PHYSICAL_NAMING_STRATEGY;


@NoArgsConstructor(access = PRIVATE)
class SessionFactoryStore {
    private static final Map<Database, SessionFactory> SESSION_FACTORY_STORE = new ConcurrentHashMap<>();

    static SessionFactory getSessionFactory(Database database) {
        return SESSION_FACTORY_STORE.computeIfAbsent(
                database,
                SessionFactoryStore::createSessionFactory
        );
    }

    private static SessionFactory createSessionFactory(Database database) {
        var config = new HibernatePersistenceConfiguration(database.getDbName())
                .transactionType(JTA)
                .property(JAKARTA_JTA_DATASOURCE, ConnectionManager.getDataSource(database))
                .property(PHYSICAL_NAMING_STRATEGY, PhysicalNamingStrategySnakeCaseImpl.class);
        switch (database) {
            case AUTH -> config
                    .managedClass(AuthorityEntity.class)
                    .managedClass(AuthUserEntity.class);
            case USERDATA -> config
                    .managedClass(UserEntity.class)
                    .managedClass(FriendshipEntity.class);
            case SPEND -> config
                    .managedClass(CategoryEntity.class)
                    .managedClass(SpendEntity.class);
        }
        return config.createEntityManagerFactory();
    }
}
