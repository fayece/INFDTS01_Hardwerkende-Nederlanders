package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.Subject;

public interface SubjectService {
    public Subject[] GetAll();

    public void AddSubject(Subject subject);
}
