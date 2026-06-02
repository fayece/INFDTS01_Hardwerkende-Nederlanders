DROP VIEW full_articles;
DROP TABLE comments;

CREATE VIEW full_articles AS

WITH most_recent_authors AS(
    SELECT *
    FROM article_authors_named AS aan
    WHERE aan.created_at IN (SELECT min(created_at) FROM article_authors GROUP BY article_id)
)

SELECT
    a.id as article_id,
    a.title,
    a.text_content,
    a.updated_at,
    a.created_at,
    a.publication_status,
    s.subject_name,
    count(distinct av.id) as view_count,
    0 as comment_count,
    mrs.first_name as first_author_first_name,
    mrs.prefix as first_author_prefix,
    mrs.last_name as first_author_last_name

FROM articles a

         LEFT JOIN subjects s ON s.id = a.subject_id

         LEFT JOIN article_viewers av ON a.id = av.article_id

         LEFT JOIN most_recent_authors mrs on a.id = mrs.article_id

GROUP BY a.id, s.id, mrs.first_name, mrs.last_name, mrs.prefix;
