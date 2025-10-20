package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {

    UserEntity save(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void deleteById(UUID id);
}
