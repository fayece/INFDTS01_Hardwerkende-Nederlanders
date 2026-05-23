# PII Field Classification - Neo4j

This document identifies all properties and relationships stored in Neo4j, classified by PII level, and documents GDPR compliance requirements.

## Related Documents
- [pii-classification-postgresql.md](pii-classification-postgresql.md) - PostgreSQL classification document for all core domain data, including users and articles.
- [pii-classification-redis.md](pii-classification-redis.md) - Redis classification document for cached, non-authoritative data.

## Classification Levels

| Level          | Description                                                                                                                                                 |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **public**     | No PII. Safe to expose broadly.                                                                                                                             |
| **PII**        | Could be linked to a real person.                                                                                                                           |
| **PII_strict** | Highly sensitive. Contact channels, credentials, or data that could enable targeted abuse (scams, phising, credential attacks). Must be tightly controlled. |

> No Neo4j properties currently reach PII_strict. The level is listed for consistency with the other classification documents.

---

## Graph Structure Overview

```
(:Comment) -[:REPLIED_TO]-> (:Comment)
(:Comment) -[:AUTHORED_BY]-> (:User)
```

---

## Node: `:User`

A name-only copy of the PostgreSQL `users` table. Exists to support graph traversal for comment authorship without requiring an extra call to PostgreSQL. 

| Property    | Type          | PII Level | Reason                               |
|-------------|---------------|-----------|--------------------------------------|
| `id`        | String (UUID) | public    | Mirrors `users.id`; surrogate key    |
| `firstName` | String        | PII       | Name data — identifies a real person |
| `prefix`    | String        | PII       | Part of a person's name              |
| `lastName`  | String        | PII       | Name data — identifies a real person |

> **Sync note:** The node is kept current with fallback logic:
> 
> When a user is created or edited in PostgreSQL, the application will do a best-effort attempt to update Neo4j within the same transaction. 
> If Neo4j is unavailable at that moment, the change will be picked up by `Neo4jUserReconciliationJob` within 5 minutes and synced then.
> Deletion takes effect immediately via an explicit call to `deleteById()`.

---

## Node: `:Comment`

Stores all comment data. Comment content is user-authored free text.
This means that users can write anything in their comments, including PII or even PII_strict data if misused.
However, since the data is authored by a user and linked to them via the `:AUTHORED_BY` relationship, we need to classify it as PII to reflect that it can be linked to a real person.

| Property      | Type                | PII Level | Reason                                                                           |
|---------------|---------------------|-----------|----------------------------------------------------------------------------------|
| `id`          | String (UUID)       | public    | Surrogate key to ensure stable references to comments                            |
| `commentBody` | String              | PII       | Free-text content authored by and linked to a specific person via `:AUTHORED_BY` |
| `articleId`   | String (UUID)       | public    | Reference to an article; not personal data in isolation                          |
| `creatorId`   | String (UUID)       | PII       | Links the comment to a specific user in PostgreSQL                               |
| `mediaId`     | String (UUID)       | public    | Reference to a media item; not personal data in isolation                        |
| `createdAt`   | DateTime            | PII       | Timestamp of a specific person's action                                          |
| `deletedAt`   | DateTime (nullable) | PII       | Timestamp of deletion; presence reveals a specific person deleted their comment  |

---

## Relationships

| Relationship     | PII Level | Reason                                                                                                     |
|------------------|-----------|------------------------------------------------------------------------------------------------------------|
| `[:REPLIED_TO]`  | public    | Structural threading between comments; no personal properties                                              |
| `[:AUTHORED_BY]` | PII       | Links a comment (with user-authored content) to a `:User` node; the relationship itself encodes authorship |

---

## Summary

| PII Level  | Node / Relationship | Properties                                           |
|------------|---------------------|------------------------------------------------------|
| **PII**    | `:User`             | `firstName`, `prefix`, `lastName`                    |
| **PII**    | `:Comment`          | `commentBody`, `creatorId`, `createdAt`, `deletedAt` |
| **PII**    | `[:AUTHORED_BY]`    | (relationship itself — encodes authorship)           |
| **public** | `:User`             | `id`                                                 |
| **public** | `:Comment`          | `id`, `articleId`, `mediaId`                         |
| **public** | `[:REPLIED_TO]`     | (relationship itself)                                |

Nodes with personal data: `:User` (name projection) and `:Comment` (user-authored content and authorship links).

---

## GDPR Compliance Reference

### Lawful Basis (Art. 6)

| Data Category        | Properties                                       | Lawful Basis            | Notes                                               |
|----------------------|--------------------------------------------------|-------------------------|-----------------------------------------------------|
| Comment content      | `commentBody`, `createdAt`, `deletedAt`          | Contract (Art. 6(1)(b)) | Posted as part of using the service                 |
| Comment authorship   | `creatorId`, `[:AUTHORED_BY]`                    | Contract (Art. 6(1)(b)) | Necessary to attribute comments to their author     |
| User name projection | `:User` node (`firstName`, `prefix`, `lastName`) | Contract (Art. 6(1)(b)) | Necessary to display author name alongside comments |

### Retention Periods

> These are recommended periods. Formal retention periods must be adopted in a Data Retention Policy document.

| Data Category    | Retention Period              | Notes                                                 |
|------------------|-------------------------------|-------------------------------------------------------|
| `:Comment` nodes | Duration of account + 30 days | PII properties must be erased on account deletion     |
| `:User` nodes    | Duration of account           | Delete node when PostgreSQL user is deleted or erased |

### Data Subject Rights

| Right                   | Applies to                                                                         | Notes                                                                                                                                                                                 |
|-------------------------|------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Access (Art. 15)        | All `:Comment` nodes authored by the user, `:User` node                            | Must include comment content in any data export                                                                                                                                       |
| Rectification (Art. 16) | `:User` node properties (`firstName`, `prefix`, `lastName`)                        | Name data must be updated within 5 minutes via the reconciliation job. Comments can currently not be edited after posting, so no rectification process for `commentBody` is required. |
| Erasure (Art. 17)       | `commentBody`, `creatorId`, `:User` node properties, `[:AUTHORED_BY]` relationship | See critical note below                                                                                                                                                               |
| Restriction (Art. 18)   | All PII properties on `:Comment` and `:User`                                       | Must be able to freeze processing without deleting                                                                                                                                    |
| Portability (Art. 20)   | `commentBody`, `createdAt` per comment                                             | Contract basis + automated processing; export in machine-readable format (e.g. JSON)                                                                                                  |

---

## Critical: Current Soft-Delete Does Not Satisfy Right to Erasure

The current `delete` implementation for comments is a soft delete that sets the `deletedAt` timestamp and masks the comment body at the application layer.
However, the original `commentBody` remains stored in Neo4j, which means that the right to erasure (Art. 17) cannot currently be satisfied by this approach alone.

To comply with GDPR requirements, the implementation must be updated to either:
- Overwrite `commentBody` in Neo4j at deletion time with the placeholder value, or
- Hard-delete the node and preserve the thread structure via a tombstone node with no personal properties.

This must be resolved before any GDPR erasure workflow is implemented.
