package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.AuthUserEntity;

import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity save(AuthUserEntity userEntity);

    void deleteById(UUID id);
}
