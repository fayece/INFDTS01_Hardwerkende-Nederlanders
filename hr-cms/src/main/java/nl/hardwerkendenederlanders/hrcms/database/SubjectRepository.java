package nl.hardwerkendenederlanders.hrcms.database;

import nl.hardwerkendenederlanders.hrcms.models.Subject;

public interface SubjectRepository {
    Subject[] GetAll();

    void AddSubject(Subject subject);
}
