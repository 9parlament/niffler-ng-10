package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {

    UserEntity save(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    void deleteById(UUID id);
}
