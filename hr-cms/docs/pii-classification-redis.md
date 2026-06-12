# PII Classificaties voor Redis

Dit document identificeert alle eigenschappen en relaties die in Redis worden beheerd.
De gegevens zijn geclassificeerd op basis van PII-niveau.
De GDPR-compliancevereisten voor elke cache namespace worden ook gedocumenteerd.

Redis werkt momenteel enkel als cache. De data is tijdelijk, met een globale Time To Live (TTL) van 10 minuten. Na deze periode vervallen de cache entries automatisch.
Redis bevat geen autoritatieve data, wat betekent dat alle gegevens afgeleid zijn van andere databases.

## Gerelateerde Documenten
- [PII Classificaties voor PostgreSQL](pii-classification-postgresql.md)
- [PII Classificaties voor Neo4j](pii-classification-neo4j.md)

## Classificatieniveaus

| Niveau         | Beschrijving                                                                                                                         |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **public**     | Geen PII. Veilig om bloot te stellen aan andere gebruikers.                                                                          |
| **PII**        | Kan direct of indirect gebruikt worden om een persoon te identificeren en beschrijven                                                |
| **PII_strict** | Zeer gevoelig. Contactkanalen, inloggegevens of gegevens die gerichte misbruik (scams, phishing, credential attacks) mogelijk maken. |

> Er zijn momenteel geen Redis cache entries die als PII_strict worden geclassificeerd.
> Het niveau staat enkel vermeld voor consistentie met de andere classificatiedocumenten.

---

## Cache Namespaces

### `publishedArticlesFull`
Gevuld door [ArticleServiceImpl.findNewPublished()](../src/main/java/nl/hardwerkendenederlanders/hrcms/services/ArticleServiceImpl.java).
Cachet een `List<ArticleFullDetailsDto>`: de volledige lijst van gepubliceerde artikelen die aan lezers wordt getoond.

| Veld                   | PII Niveau | Reden                                                                                                                                                                                                                            |
|------------------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `firstAuthor.username` | public     | Gebuikersnaam van de eerste auteur. Gezien deze eerst random gegenereerd wordt en de gebruiker een deze mag aanpassen (met een waarchuwing om geen persoonlijke informatie te gebruiken), wordt deze als public geclassificeerd. |
| Alle andere velden     | public     | Operationele en redactionele data                                                                                                                                                                                                |

Evicted door: elke aanroep van `ensureArticleExists()` (artikel aanmaken of bijwerken).

---

### `fullArticle`
Gevuld door [ArticleServiceImpl.findArticleFullId()](../src/main/java/nl/hardwerkendenederlanders/hrcms/services/ArticleServiceImpl.java).
Cachet een enkele `ArticleFullDetailsDto` op basis van een artikel UUID.

| Veld                   | PII Niveau | Reden                                                                                                                                                                                                                            |
|------------------------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `firstAuthor.username` | public     | Gebuikersnaam van de eerste auteur. Gezien deze eerst random gegenereerd wordt en de gebruiker een deze mag aanpassen (met een waarchuwing om geen persoonlijke informatie te gebruiken), wordt deze als public geclassificeerd. |
| Alle andere velden     | public     | Operationele en redactionele data                                                                                                                                                                                                |

Evicted door: elke aanroep van `ensureArticleExists()` voor hetzelfde artikel ID.

---

### `topCommentsArticle`
Gevuld door [CommentServiceImpl.getTopLevelComments()](../src/main/java/nl/hardwerkendenederlanders/hrcms/services/CommentServiceImpl.java).
Cachet een `PagedComments` object (lijst van `CommentViewDto`) op basis van een artikel UUID.

| Veld               | PII Niveau | Reden                                                                                                                                                                                                                              |
|--------------------|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `commentBody`      | PII        | Gelinkt aan een specifiek persoon via `:AUTHORED_BY`; vrije tekst, inhoud kan PII bevatten                                                                                                                                         |
| `username`         | public     | Gebruikersnaam van de comment auteur. Gezien deze eerst random gegenereerd wordt en de gebruiker een deze mag aanpassen (met een waarchuwing om geen persoonlijke informatie te gebruiken), wordt deze als public geclassificeerd. |
| `creatorId`        | PII        | UUID die de comment linkt aan een specifieke gebruiker in PostgreSQL                                                                                                                                                               |
| `createdAt`        | PII        | Timestamp van een specifieke actie van een persoon                                                                                                                                                                                 |
| Alle andere velden | public     | Structurele en operationele data                                                                                                                                                                                                   |

Evicted door: elke aanroep van `postComment()` voor hetzelfde artikel ID.
> **Nog belangrijk**: Verwijderde comments worden opgenomen in deze cache. Voor verwijderde comments wordt `commentBody` vervangen door "This comment has been deleted" en `username` is "Hidden".
> Dit wordt gedaan in runtime in de Neo4j repository laag. Dit houdt in dat de cache entry ook direct de gemaskeerde waarden bevat, en niet de originele comment body en username.
> Dit betekent niet dat het systeem voldoet aan het recht op verwijdering: de originele `commentBody` blijft aanwezig in de database. Voor meer, zie de [Neo4j Issue](pii-classification-neo4j.md#huidige-soft-delete-voldoet-niet-aan-het-recht-op-verwijdering).

---

## Samenvatting

| PII Niveau | Cache Namespace      | Velden                                  |
|------------|----------------------|-----------------------------------------|
| PII        | `topCommentsArticle` | `commentBody`, `creatorId`, `createdAt` |
| public     | Alle caches          | Alle andere velden                      |

Alle gecachete PII-gegevens zijn kopiën van gegevens die door een andere database worden beheerd.
Redis is niet een opslagplaats om gegevens te bewaren.

---

## GDPR Compliance Vereisten

### Rechtmatigheid (Art. 6)
| Data Categorie   | Cache                | Rechtmatigheid          | Notities                                           |
|------------------|----------------------|-------------------------|----------------------------------------------------|
| Comment-gegevens | `topCommentsArticle` | Contract (Art. 6(1)(b)) | Afgeleid van Neo4j; dezelfde basis als de brondata |

### Retentie
Alle cache entries hebben een globale TTL van 10 minuten, waarna ze automatisch vervallen.
De TTL is ingesteld in [RedisCacheManagerConfiguration](../src/main/java/nl/hardwerkendenederlanders/hrcms/config/RedisCacheManagerConfiguration.java).
Er is geen handmatige retentie- of verwijderingsmechanisme nodig buiten de garantie dat cache eviction correct plaatsvindt bij relevante datawijzigingen (zie Issues).

### Rechten van betrokkenen

| Recht                           | Impact op Redis Cache                                                                                                                    |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| Toegang (Art. 15)               | Geen actie nodig. Redis bevat geen data die niet al in andere databases aanwezig is.                                                     |
| Rectificatie (Art. 16)          | Caches worden automatisch verwijderd binnen 10 minuten. Bij het updaten van een `username` wordt er niet direct een eviction getriggerd. |
| Verwijdering (Art. 17)          | Cache entries die de gegevens van de gebruiker bevbatten moeten worden geevicted op het moment van verwijdering. Zie Issues              |
| Dataoverdraagbaarheid (Art. 20) | Geen actie nodig. Ezports moeten enkel worden uitgevoerd vanuit autoritatieve bronnen.                                                   |

---

## Issues
### Verwijdering triggert niet een directe eviction van cache entries
Wanneer een gebruiker verwijderd wordt, worden de cache entries die de gegevens van die gebruiker bevatten niet onmiddellijk evicted.
Dit betekent het volgende:
- `publishedArticlesFull` en `fullArticle` entries kunnen nog steeds de `username` van de verwijderde gebruiker bevatten totdat ze automatisch vervallen.
  Al is de `username` als public geclassificeerd, is het nog steeds een goed idee om deze zo snel mogelijk te verwijderen.
  Dit is vooral belangrijk als de gebruiker een `username` had gekozen die persoonlijk identificeerbaar is (hoewel dit direct tegen de waarschuwing gaat).
- `topCommentsArticle` entries kunnen nog steeds de `commentBody`, `username`, `creatorId` en `createdAt` van de verwijderde gebruiker bevatten totdat ze automatisch vervallen.
  Dit is problematischer, omdat een aantal van deze velden als PII is geclassificeerd.

Voor de meeste scenarios is een 10 minuten TTL voldoende.
Echter in een geval van een verwijderingsverzoek op basis van Artikel 17, moet de delete flow ook een eviction van de relevante caches triggeren om te voldoen aan het recht op verwijdering.
Dit kan ten koste gaan van wat performance, maar is noodzakelijk voor compliance.

Momenteel zijn eviction annotaties (`@CacheEvict`) gescoped op artikelen en writeoperaties van comments. Er is geen user-gescoped eviction.
Een user deletion zou de volgende caches moeten evicten:
- Alle `fullArticle` entries voor artikelen die de gebruiker heeft geschreven of waaraan de gebruiker heeft bijgedragen.
- De `publishedArticlesFull` entry als de gebruiker de `firstAuthor` is van een van de gecachete artikelen.
- Alle `topCommentsArticle` entries voor artikelen waar de gebruiker een comment heeft geplaatst.
