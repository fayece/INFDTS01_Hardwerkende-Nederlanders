CREATE TABLE article_viewers (
     id                  UUID                    PRIMARY KEY                         DEFAULT gen_random_uuid(),
     article_id          UUID                    NOT NULL REFERENCES articles(id)    ON DELETE CASCADE,
     viewer_id           UUID                    NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
     UNIQUE (article_id, viewer_id)
);