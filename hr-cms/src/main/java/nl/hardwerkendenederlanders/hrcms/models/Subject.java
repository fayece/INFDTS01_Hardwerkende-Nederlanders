package nl.hardwerkendenederlanders.hrcms.models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Subject {
    private final UUID id;
    private final String subjectName;

    public Subject (UUID id, String subjectName){
        this.id = id;
        this.subjectName = subjectName;
    }

    public Subject(String subjectName){
        this(UUID.randomUUID(), subjectName);
    }
}
