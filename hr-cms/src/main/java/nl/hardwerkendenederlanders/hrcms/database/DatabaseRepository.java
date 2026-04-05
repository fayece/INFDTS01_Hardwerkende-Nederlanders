package nl.hardwerkendenederlanders.hrcms.database;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DatabaseRepository<T> {

    void insert(T entity);


    Optional<T> findById(UUID id);

    void delete(UUID id);

    List<T> findAllPaged(int page, int limit);
}
