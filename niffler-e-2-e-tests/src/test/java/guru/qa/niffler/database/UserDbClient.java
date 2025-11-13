package guru.qa.niffler.database;

import guru.qa.niffler.database.repository.AuthUserRepository;
import guru.qa.niffler.database.repository.UserdataUserRepository;
import guru.qa.niffler.database.repository.hibernate.AuthUserHibernateRepository;
import guru.qa.niffler.database.repository.hibernate.UserdataUserHibernateRepository;
import guru.qa.niffler.model.api.UserJson;
import guru.qa.niffler.model.entity.AuthUserEntity;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.UserEntity;
import guru.qa.niffler.service.UserClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.qa.niffler.common.utils.NifflerFaker.randomPassword;
import static guru.qa.niffler.common.utils.NifflerFaker.randomUserName;
import static guru.qa.niffler.database.Database.AUTH;
import static guru.qa.niffler.database.Database.USERDATA;
import static guru.qa.niffler.database.SessionFactoryStore.getSessionFactory;
import static guru.qa.niffler.database.TransactionManager.executeInXaTransaction;
import static guru.qa.niffler.model.entity.AuthorityEntity.readAuthority;
import static guru.qa.niffler.model.entity.AuthorityEntity.writeAuthority;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

public class UserDbClient implements UserClient {
    private final AuthUserRepository authUserRepository;
    private final UserdataUserRepository userdataUserRepository;

    public UserDbClient() {
        authUserRepository = new AuthUserHibernateRepository(getSessionFactory(AUTH));
        userdataUserRepository = new UserdataUserHibernateRepository(getSessionFactory(USERDATA));
    }

    @Override
    public UserJson createUser(String username, String password) {
        String encodedPassword = createDelegatingPasswordEncoder().encode(password);
        AuthUserEntity authUser = AuthUserEntity.create(username, encodedPassword);
        authUser.getAuthorities().addAll(List.of(readAuthority(authUser), writeAuthority(authUser)));

        UserEntity user = UserEntity.create(username);

        UserEntity createdUser = executeInXaTransaction(
                () -> {
                    authUserRepository.save(authUser);
                    return userdataUserRepository.create(user);
                });
        return UserJson.from(createdUser);
    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson addressee, int count) {
        List<UserData> requesters = Stream.generate(this::createRandomUser)
                .limit(count)
                .toList();
        UserEntity addresseeEntity = UserEntity.from(addressee);
        executeInXaTransaction(() -> {
            requesters.forEach(requester -> {
                        authUserRepository.save(requester.userAuthPart());
                        userdataUserRepository.create(requester.userUserdataPart());
                        userdataUserRepository.createInvitation(requester.userUserdataPart(), addresseeEntity);
                    }
            );
            return null;
        });
        return addresseeEntity.getAddressees().stream().map(FriendshipEntity::getRequester).map(UserJson::from).collect(Collectors.toList());
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson requester, int count) {
        List<UserData> addressees = Stream.generate(this::createRandomUser)
                .limit(count)
                .toList();
        UserEntity requesterEntity = UserEntity.from(requester);
        executeInXaTransaction(() -> {
            addressees.forEach(addressee -> {
                        authUserRepository.save(addressee.userAuthPart());
                        userdataUserRepository.create(addressee.userUserdataPart());
                        userdataUserRepository.createInvitation(requesterEntity, addressee.userUserdataPart());
                    }
            );
            return null;
        });
        return requesterEntity.getRequests().stream().map(FriendshipEntity::getAddressee).map(UserJson::from).collect(Collectors.toList());
    }

    @Override
    public List<UserJson> createFriendShip(UserJson requester, int count) {
        List<UserData> addressees = Stream.generate(this::createRandomUser)
                .limit(count)
                .toList();
        UserEntity requesterEntity = UserEntity.from(requester);
        executeInXaTransaction(() -> {
            addressees.forEach(addressee -> {
                        authUserRepository.save(addressee.userAuthPart());
                        userdataUserRepository.create(addressee.userUserdataPart());
                        userdataUserRepository.createFriendship(requesterEntity, addressee.userUserdataPart());
                    }
            );
            return null;
        });
        return requesterEntity.getRequests().stream().map(FriendshipEntity::getAddressee).map(UserJson::from).collect(Collectors.toList());
    }

    private UserData createRandomUser() {
        String username = randomUserName();
        String password = createDelegatingPasswordEncoder().encode(randomPassword());
        AuthUserEntity authUser = AuthUserEntity.create(username, password);
        authUser.getAuthorities().addAll(List.of(readAuthority(authUser), writeAuthority(authUser)));
        UserEntity user = UserEntity.create(username);
        return new UserData(authUser, user);
    }

    private record UserData(AuthUserEntity userAuthPart, UserEntity userUserdataPart) {
    }
}
