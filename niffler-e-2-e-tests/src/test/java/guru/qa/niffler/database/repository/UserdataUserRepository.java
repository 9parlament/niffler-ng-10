package guru.qa.niffler.database.repository;

import guru.qa.niffler.model.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    void createFriendship(UserEntity requester, UserEntity addressee);

    void createIncomeInvitation(UserEntity requester, UserEntity addressee);

    void createOutcomeInvitation(UserEntity addressee, UserEntity requester);
}
