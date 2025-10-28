package guru.qa.niffler.database.dao;

import guru.qa.niffler.model.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

    SpendEntity save(SpendEntity spendEntity);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findAll();

    List<SpendEntity> findAllByUsername(String username);

    void deleteById(UUID id);
}
