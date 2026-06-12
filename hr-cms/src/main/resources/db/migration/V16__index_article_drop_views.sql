CREATE INDEX created_at_published ON articles USING btree (created_at DESC) WHERE publication_status = 'PUBLISHED';

DROP VIEW full_articles;
DROP VIEW article_authors_named;
