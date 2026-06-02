package nl.hardwerkendenederlanders.hrcms.database.interfaces;

public interface DatabaseMutableRepository<T> extends DatabaseRepository<T> {

    void update(T entity);
}
