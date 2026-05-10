package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.Subject;

public interface SubjectService {
    Subject[] findAll();

    void insert(Subject subject);
}
