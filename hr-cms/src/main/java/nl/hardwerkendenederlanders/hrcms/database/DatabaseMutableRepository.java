package nl.hardwerkendenederlanders.hrcms.database;

public interface DatabaseMutableRepository<T> extends DatabaseRepository<T> {

    void update(T entity);
}
