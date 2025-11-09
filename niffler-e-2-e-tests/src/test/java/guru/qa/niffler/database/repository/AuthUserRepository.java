package guru.qa.niffler.database.repository;

import guru.qa.niffler.model.entity.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity save(AuthUserEntity user);

    Optional<AuthUserEntity> findById(UUID id);

    Optional<AuthUserEntity> findByUsername(String username);

    void delete(AuthUserEntity user);
}
