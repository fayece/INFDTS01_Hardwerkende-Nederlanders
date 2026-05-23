# PII Field Classification - Redis

This document identifies all data cached in Redis, classifies it by PII level, and documents GDPR compliance requirements.

Redis is used exclusively as a cache. All data is transient, with the global TTL set to **10 minutes**, after which entries expire automatically.
Redis holds no authoritative data, meaning everything caches here is derived from other databases.

## Related Documents
- [pii-classification-postgresql.md](pii-classification-postgresql.md) - PostgreSQL classification document for all core domain data, including users and articles.
- [pii-classification-neo4j.md](pii-classification-neo4j.md) - Neo4j classification document for comment data and the `:User` projection.

## Classification Levels

| Level          | Description                                                                                                                                                 |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **public**     | No PII. Safe to expose broadly.                                                                                                                             |
| **PII**        | Could be linked to a real person.                                                                                                                           |
| **PII_strict** | Highly sensitive. Contact channels, credentials, or data that could enable targeted abuse (scams, phising, credential attacks). Must be tightly controlled. |

> No Redis cache entries currently reach PII_strict. The level is listed for consistency with the other classification documents.

---

## Cache Namespaces

### `publishedArticlesFull`
Populated by `ArticleServiceImpl.findNewPublished()`. Caches a `List<ArticleFullDetailsDto>`, which is the full published article list shown to readers.

| Field                   | PII Level | Reason                         |
|-------------------------|-----------|--------------------------------|
| `firstAuthor.firstName` | PII       | Author's first name            |
| `firstAuthor.prefix`    | PII       | Author's name prefix           |
| `firstAuthor.lastName`  | PII       | Author's last name             |
| All other fields        | public    | Operational and editorial data |

Evicted by: any call to `ensureArticleExists()` (article create or update).

---

### `fullArticle`
Populated by `ArticleServiceImpl.findArticleFullId()`. Caches a single `ArticleFullDetailsDto` keyed by article UUID.

| Field                   | PII Level | Reason                         |
|-------------------------|-----------|--------------------------------|
| `firstAuthor.firstName` | PII       | Author's first name            |
| `firstAuthor.prefix`    | PII       | Author's name prefix           |
| `firstAuthor.lastName`  | PII       | Author's last name             |
| All other fields        | public    | Operational and editorial data |

Evicted by: any call to `ensureArticleExists()` for the same article ID.

---

### `topCommentsArticle`
Populated by `CommentServiceImpl.getTopLevelComments()`. Caches a `PagedComments` object (list of `CommentViewDto`) keyed by article UUID.

| Field            | PII Level | Reason                                                                                |
|------------------|-----------|---------------------------------------------------------------------------------------|
| `commentBody`    | PII       | Free-text content authored by a specific person                                       |
| `authorName`     | PII       | Full name of the comment author (composed from `firstName`, `prefix`, and `lastName`) |
| `creatorId`      | PII       | UUID linking the comment to a specific user in PostgreSQL                             |
| `createdAt`      | PII       | Timestamp of a specific person's action                                               |
| All other fields | public    | Structural and operational data                                                       |

Evicted by: any call to `postComment()` for the same article ID.
> **Note**: Deleted comments are included in this cache. For deleted comments, `commentBody` is replaced with "This comment has been deleted" and `authorName` is "Hidden" at the Neo4j layer.
> This means the cached values already reflect the masked form, not the original content. This does not mean erasure is satisfied:
> the original `commentBody` remains stored in Neo4j. See the [Neo4j critical note](pii-classification-neo4j.md#critical-current-soft-delete-does-not-satisfy-right-to-erasure).

---

## Summary

| PII Level | Cache Namespace         | Fields                                                                |
|-----------|-------------------------|-----------------------------------------------------------------------|
| PII       | `publishedArticlesFull` | `firstAuthor.firstName`, `firstAuthor.prefix`, `firstAuthor.lastName` |
| PII       | `fullArticle`           | `firstAuthor.firstName`, `firstAuthor.prefix`, `firstAuthor.lastName` |
| PII       | `topCommentsArticle`    | `commentBody`, `authorName`, `creatorId`, `createdAt`                 |
| public    | All caches              | All other fields                                                      |

All cached PII is transient and derived from authoritative copies in other databases. Redis is not a system of record.

---

## GDPR Compliance Reference

### Lawful Basis (Art. 6)
| Data Category                  | Cache                                  | Lawful Basis            | Notes                                                            |
|--------------------------------|----------------------------------------|-------------------------|------------------------------------------------------------------|
| Author name in article caches  | `publishedArticlesFull`, `fullArticle` | Contract (Art. 6(1)(b)) | Derived from the same data as the PostgreSQL authoritative copy  |
| Comment content and authorship | `topCommentsArticle`                   | Contract (Art. 6(1)(b)) | Derived from Neo4j; same basis as the source data                |

### Retention
All cache entries expire automatically after **10 minutes** via the global TTL configured in `RedisCacheManagerConfiguration`.
No manual retention policy is required beyond ensuring cache eviction is triggered correctly on data changes (see critical note below).

### Data Subject Rights
| Right                   | Impact on Redis Cache                                                                                      |
|-------------------------|------------------------------------------------------------------------------------------------------------|
| Access (Art. 15)        | No action needed. Redis holds no data not already present in other databases.                              |
| Rectification (Art. 16) | Stale cache names will expire within 10 minutes; no immediate cache eviction is triggered by name updates. |
| Erasure (Art. 17)       | Cache entries containing the user's data must be evicted at erasure time. See critical note below.         |
| Portability (Art. 20)   | No action needed. Export should be performed from authoritative sources only.                              |

---

## Critical: Erasure Does Not Evict Cache Entries
When a user is deleted, no cache eviction is triggered for entries containing that user's personal data. As a result:
- `publishedArticlesFull` and `fullArticle` entries may continue to display the deleted user's name as article author for up to 10 minutes.
- `topCommentsArticle` entries may continue to display the deleted user's `authorName` and `creatorId` for up to 10 minutes.

For most erasure scenarios, the 10-minute window is acceptable given the short TTL.
However, if immediate erasure is required (e.g. the data subject explicitly requests it under Art. 17), the deletion flow must also evict all affected cache keys.

The current eviction annotations (`@CacheEvict`) are scoped to article and comment write operations. There is no user-scoped eviction.
A user deletion would need to evict:
- All `fullArticle` entries for articles the user authored.
- The `publishedArticlesFull` entry if the user is the `firstAuthor` of any cached article.
- All `topCommentsArticle` entries for articles where the user has comments.
