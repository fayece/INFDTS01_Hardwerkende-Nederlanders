CREATE VIEW article_authors_named AS
SELECT aa.id, aa.article_id, aa.author_id, u.first_name, u.prefix, u.last_name, u.role_id, r.role_name, a.title
FROM article_authors AS aa
JOIN users AS u on aa.author_id = u.id
LEFT JOIN roles AS r ON u.role_id = r.id
LEFT JOIN articles AS a ON aa.article_id = a.id;