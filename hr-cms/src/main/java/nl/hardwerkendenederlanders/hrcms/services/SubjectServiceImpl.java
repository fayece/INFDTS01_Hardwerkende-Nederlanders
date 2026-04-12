package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.SubjectRepository;
import nl.hardwerkendenederlanders.hrcms.models.Subject;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository){
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject[] GetAll() {
        return subjectRepository.GetAll();
    }

    @Override
    public void AddSubject(Subject subject) {
        subjectRepository.AddSubject(subject);
    }
}
