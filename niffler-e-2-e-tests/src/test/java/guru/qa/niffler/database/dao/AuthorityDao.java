package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.AuthorityEntity;

import java.util.UUID;

public interface AuthorityDao {

    void saveAll(AuthorityEntity... authority);

    void deleteAllByUserId(UUID id);
}
