START TRANSACTION;


CREATE TABLE subjects (
    id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
    subject_name        VARCHAR(100)            NOT NULL,
    CONSTRAINT subject_name_at_least_one_character CHECK( length(subject_name) > 0 )
);

ALTER TABLE articles
    ADD COLUMN subject_id UUID;

ALTER TABLE articles
    ADD CONSTRAINT fk_subject
    FOREIGN KEY (subject_id)
    REFERENCES subjects(id);

COMMIT;