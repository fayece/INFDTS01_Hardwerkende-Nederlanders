package nl.hardwerkendenederlanders.hrcms.database;

import nl.hardwerkendenederlanders.hrcms.models.Subject;

public interface SubjectRepository {
    Subject[] findAll();

    void insert(Subject subject);
}
