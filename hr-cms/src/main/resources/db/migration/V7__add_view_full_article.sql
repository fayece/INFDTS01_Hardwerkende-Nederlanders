-- all information directly related to an article (except comments) ready to display the article.
CREATE VIEW full_articles AS
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
    aan.first_name as first_author_first_name,
    aan.prefix as first_author_prefix,
    aan.last_name as first_author_last_name

FROM articles a

         LEFT JOIN subjects s ON s.id = a.subject_id

         LEFT JOIN article_viewers av ON a.id = av.article_id

         LEFT JOIN public.comments c on a.id = c.article_id

         LEFT JOIN article_authors_named aan on a.id = aan.article_id

GROUP BY a.id, s.id, aan.first_name, aan.last_name, aan.prefix;