package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.AuthUserEntity;

import java.util.List;
import java.util.UUID;

public interface AuthUserDao {

    AuthUserEntity save(AuthUserEntity userEntity);

    List<AuthUserEntity> findAll();

    void deleteById(UUID id);
}
