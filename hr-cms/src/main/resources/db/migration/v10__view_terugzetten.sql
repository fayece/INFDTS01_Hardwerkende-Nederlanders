-- Smaller version of the full article view. this improves performance
DROP VIEW IF EXISTS full_articles;
DROP VIEW IF EXISTS article_authors_named;

CREATE VIEW article_authors_named AS
SELECT aa.id, aa.article_id, aa.author_id, aa.created_at, u.first_name, u.prefix, u.last_name, u.role_id, r.role_name
FROM article_authors AS aa
         JOIN users AS u on aa.author_id = u.id
         LEFT JOIN roles AS r ON u.role_id = r.id
ORDER BY aa.created_at;

-- all information directly related to an article (except comments) ready to display the article.
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
    count(distinct c.id) as comment_count,
    mrs.first_name as first_author_first_name,
    mrs.prefix as first_author_prefix,
    mrs.last_name as first_author_last_name

FROM articles a

         LEFT JOIN subjects s ON s.id = a.subject_id

         LEFT JOIN article_viewers av ON a.id = av.article_id

         LEFT JOIN public.comments c on a.id = c.article_id

         LEFT JOIN most_recent_authors mrs on a.id = mrs.article_id

GROUP BY a.id, s.id, mrs.first_name, mrs.last_name, mrs.prefix;