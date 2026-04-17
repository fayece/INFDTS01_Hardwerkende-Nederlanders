START TRANSACTION;

ALTER TABLE articles
ADD CONSTRAINT title_at_least_one_character CHECK(publication_status = 'DRAFT' OR length(title) > 0);

ALTER TABLE articles
ADD CONSTRAINT text_content_at_least_one_character CHECK(publication_status = 'DRAFT' OR length(text_content) > 0);

ALTER TABLE articles
ADD CONSTRAINT valid_publication_status CHECK(publication_status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'));

COMMIT;