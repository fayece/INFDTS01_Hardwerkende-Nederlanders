-- Smaller version of the full article view. this improves performance
CREATE VIEW article_authors_named AS
SELECT aa.id, aa.article_id, aa.author_id, aa.created_at, u.first_name, u.prefix, u.last_name, u.role_id, r.role_name
FROM article_authors AS aa
JOIN users AS u on aa.author_id = u.id
LEFT JOIN roles AS r ON u.role_id = r.id
ORDER BY aa.created_at;