package nl.hardwerkendenederlanders.hrcms.database;

import nl.hardwerkendenederlanders.hrcms.models.Subject;

public interface SubjectRepository {
    public Subject[] GetAll();

    public void AddSubject(Subject subject);
}
