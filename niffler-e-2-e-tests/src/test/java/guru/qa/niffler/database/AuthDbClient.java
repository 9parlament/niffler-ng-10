package guru.qa.niffler.database;

import guru.qa.niffler.database.TransactionManager.XaConsumer;
import guru.qa.niffler.database.TransactionManager.XaFunction;
import guru.qa.niffler.database.dao.jdbc.JdbcAuthUserDao;
import guru.qa.niffler.database.dao.jdbc.JdbcAuthorityDao;
import guru.qa.niffler.database.dao.jdbc.JdbcUserdataUserDao;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.entity.AuthUserEntity;
import guru.qa.niffler.model.entity.UserEntity;
import guru.qa.niffler.model.test.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static guru.qa.niffler.database.TransactionManager.executeInXaTransaction;
import static guru.qa.niffler.model.entity.AuthorityEntity.readAuthority;
import static guru.qa.niffler.model.entity.AuthorityEntity.writeAuthority;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

public class AuthDbClient {

    public UserEntity createUser(User testUser) {
        AuthUserEntity authUser = AuthUserEntity.fromTestUser(testUser);
        String encodedPassword = encodeUserPassword(authUser);
        authUser.setPassword(encodedPassword);

        UserEntity user = new UserEntity().setUsername(testUser.getUsername()).setCurrency(CurrencyValues.RUB);

        return (UserEntity) executeInXaTransaction(
                new XaFunction<>(
                        connection -> {
                            AuthUserEntity authUserEntity = new JdbcAuthUserDao(connection).save(authUser);
                            JdbcAuthorityDao authorityDao = new JdbcAuthorityDao(connection);
                            authorityDao.saveAll(readAuthority(authUserEntity), writeAuthority(authUserEntity));
                            return authUserEntity;
                        },
                        Database.AUTH
                ),
                new XaFunction<>(
                        connection -> new JdbcUserdataUserDao(connection).save(user),
                        Database.USERDATA
                )
        );
    }

    public void deleteUserById(UUID id) {
        executeInXaTransaction(
                new XaConsumer(connection -> {
                            new JdbcAuthorityDao(connection).deleteAllByUserId(id);
                            new JdbcAuthUserDao(connection).deleteById(id);
                        },
                        Database.AUTH
                ),
                new XaConsumer(connection -> {
                            new JdbcUserdataUserDao(connection).deleteById(id);
                        },
                        Database.USERDATA
                )
        );
    }

    private String encodeUserPassword(AuthUserEntity user) {
        PasswordEncoder passwordEncoder = createDelegatingPasswordEncoder();
        return passwordEncoder.encode(user.getPassword());
    }
}
