package guru.qa.niffler.database.repository.hibernate;

import guru.qa.niffler.database.repository.UserdataUserRepository;
import guru.qa.niffler.model.entity.FriendshipEntity;
import guru.qa.niffler.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserdataUserHibernateRepository implements UserdataUserRepository {
    private final SessionFactory sessionFactory;

    @Override
    public UserEntity create(UserEntity user) {
        sessionFactory.getCurrentSession()
                .persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        UserEntity foundUser = sessionFactory.getCurrentSession()
                .find(UserEntity.class, id);
        return Optional.ofNullable(foundUser);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return sessionFactory.openSession()
                .createSelectionQuery("from UserEntity where username = ?1", UserEntity.class)
                .setParameter(1, username)
                .uniqueResultOptional();
    }

    @Override
    public void createFriendship(UserEntity requester, UserEntity addressee) {
        FriendshipEntity requesterFriendShip = FriendshipEntity.createFriendship(requester, addressee);
        FriendshipEntity addresseeFriendShip = FriendshipEntity.createFriendship(addressee, requester);
        Session session = sessionFactory.getCurrentSession();
        session.persist(requesterFriendShip);
        session.persist(addresseeFriendShip);

        requester.getRequests().add(requesterFriendShip);
        requester.getAddressees().add(addresseeFriendShip);
        addressee.getAddressees().add(addresseeFriendShip);
        addressee.getRequests().add(requesterFriendShip);
    }

    @Override
    public void createInvitation(UserEntity requester, UserEntity addressee) {
        FriendshipEntity invitation = FriendshipEntity.createInvitation(requester, addressee);
        sessionFactory.getCurrentSession()
                .persist(invitation);

        requester.getRequests().add(invitation);
        addressee.getAddressees().add(invitation);
    }
}
