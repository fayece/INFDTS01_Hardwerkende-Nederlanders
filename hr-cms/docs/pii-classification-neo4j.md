# PII Classificaties voor Neo4j

Dit document identificeert alle eigenschappen en relaties die in Neo4j worden opgeslagen.
De gegevens zijn geclassificeerd op basis van PII-niveau.
De GDPR-compliancevereisten voor elke eigenschap en relatie worden ook gedocumenteerd.

## Gerelateerde Documenten
- [PII Classificaties voor PostgreSQL](pii-classification-postgresql.md)
- [PII Classificaties voor Redis]()

## Classificatieniveaus

| Niveau         | Beschrijving                                                                                                                         |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **public**     | Geen PII. Veilig om bloot te stellen aan andere gebruikers.                                                                          |
| **PII**        | Kan worden gekoppeld aan een echt persoon.                                                                                           |
| **PII_strict** | Zeer gevoelig. Contactkanalen, inloggegevens of gegevens die gerichte misbruik (scams, phishing, credential attacks) mogelijk maken. |

> Er zijn momenteel geen Neo4j-eigenschappen die als PII_strict worden geclassificeerd.
> Het niveau staat enkel vermeld voor consistentie met de andere classificatiedocumenten.

---

## Neo4j Datamodel

```
(:Comment) -[:REPLIED_TO]-> (:Comment)
(:Comment) -[:AUTHORED_BY]-> (:User)
```

---

## Eigenschappen en Relaties

### Node `:User`
De `:User` nodes zijn kopiën van de `users` tabel in PostgreSQL.
Deze bestaat om ervoor te zorgen dat gebruikersinformatie beschikbaar is in Neo4j voor commentaargerelateerde queries, zonder dat er een dure join tussen Neo4j en PostgreSQL nodig is.
Enkel de volgende eigenschappen worden opgeslagen in Neo4j:

| Eigenschap  | Type          | PII Niveau | Reden                                                |
|-------------|---------------|------------|------------------------------------------------------|
| `id`        | String (UUID) | public     | Unieke identifier, geen directe PII                  |
| `firstName` | String        | PII        | Gelinkt aan een specifiek persoon via `:AUTHORED_BY` |
| `prefix`    | String        | PII        | Gelinkt aan een specifiek persoon via `:AUTHORED_BY` |
| `lastName`  | String        | PII        | Gelinkt aan een specifiek persoon via `:AUTHORED_BY` |

> **Opmerking**
> De database slaat de volledige naam van de gebruiker op in Neo4j.
> In de frontend wordt de username laten zien in plaats van de volledige naam, om privacy te beschermen.
> Wegens het feit dat usernames later geïmplementeerd zijn dan de Neo4j database, staat de username niet in Neo4j.
> Gezien de tijdlijn van implementatie en de huidige gegevens, zal de username op dit moment niet worden opgeslagen in Neo4j, maar enkel in MongoDB.

### Node: `:Comment`
De `:Comment` nodes bevatten alle informatie over een comment.
De inhoud van een comment wordt geschreven door een gebruiker.
Dit betekent dat gebruikers alles in hun comments kunnen zetten die ze willen, inclusief data die geregistreerd staat als PII of zelfs PII_strict, maar is niet automatisch PII of PII_strict.
De volgende eigenschappen worden standaard opgeslagen in Neo4j:

| Eigenschap    | Type          | PII Niveau | Reden                                                                                                               |
|---------------|---------------|------------|---------------------------------------------------------------------------------------------------------------------|
| `id`          | String (UUID) | public     | Unieke identifier om stabiele referenties naar comments mogelijk te maken, geen directe PII                         |
| `commentBody` | String        | PII        | Gelinkt aan een specifiek persoon via `:AUTHORED_BY`; vrije tekst, inhoud kan PII bevatten                          |
| `articleId`   | String (UUID) | public     | Verwijst naar een artikel, geen directe PII                                                                         |
| `creatorId`   | String (UUID) | PII        | Linkt de comment aan een specifieke gebruiker in PostgreSQL, waardoor het kan worden gekoppeld aan een echt persoon |
| `mediaId`     | String (UUID) | public     | Verwijst naar media, geen directe PII                                                                               |
| `createdAt`   | DateTime      | PII        | Timestamp van een specifieke actie van een persoon.                                                                 |
| `deletedAt`   | DateTime      | PII        | Timestamp van het verwijderen; aanwezigheid kan worden gekoppeld aan het gedrag van een specifieke persoon.         |

---

## Relaties

| Relatie          | PII Niveau | Reden                                                                                                     |
|------------------|------------|-----------------------------------------------------------------------------------------------------------|
| `[:REPLIED_TO]`  | public     | Structurele geneste relatie tussen comments, geen directe PII                                             |
| `[:AUTHORED_BY]` | PII        | Verbindt een comment aan een specifieke gebruiker, waardoor het kan worden gekoppeld aan een echt persoon |

---

## Samenvatting

| PII Niveau | Nodes en Relaties | Eigenschappen                                                                               |
|------------|-------------------|---------------------------------------------------------------------------------------------|
| PII        | `:Comment`        | `commentBody`, `creatorId`, `createdAt`, `deletedAt`                                        |
| PII        | `[:AUTHORED_BY]`  | Relatie tussen `:Comment` en `:User` die een comment verbindt aan een specifieke gebruiker. |
| public     | `:User`           | `id`, `firstName`, `prefix`, `lastName`                                                     |
| public     | `:Comment`        | `id`, `articleId`, `mediaId`                                                                |
| public     | `[:REPLIED_TO]`   | Geneste relatie tussen comments, geen directe PII                                           |

---

## GDPR Compliance Vereisten

### Rechtmatigheid (Art. 6)

| Data Categorie       | Velden                                  | Rechtmatige Basis       | Toelichting                                                                                        |
|----------------------|-----------------------------------------|-------------------------|----------------------------------------------------------------------------------------------------|
| Commentinhoud        | `commentBody`. `createdAt`, `deletedAt` | Contract (Art. 6(1)(b)) | Volstrekt noodzakelijk voor het uitvoeren van het comment systeem.                                 |
| Comment auteurschap  | `creatorId`, `[:AUTHORED_BY]`           | Contract (Art. 6(1)(b)) | Verbindt comment aan een specifieke gebruiker, noodzakelijk voor het functioneren van het systeem. |
| Gebruikersinformatie | `id`, `firstName`, `prefix`, `lastName` | Contract (Art. 6(1)(b)) | Nodig voor het functioneren van het comment systeem, maar bevat geen directe PII.                  |

### Retentie

> Dit zijn initiële aanbevelingen op basis van de huidige gegevens en gebruikspatronen.
> Deze aanbevelingen zijn nog niet definitief, verwerkt in beleid, of gecommuniceerd in een officiële capaciteit.
> Formele retentieperiodes moeten worden vastgesteld in overleg met juridische en compliance teams, en duidelijk worden gecommuniceerd aan gebruikers.

| Data Categorie   | Retentieperiode                             | Notities                                                                                                                                              |
|------------------|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `:Comment` nodes | Zolang het account actief is, plus 30 dagen | PII-gegevens moeten worden verwijderd of geanonimiseerd op moment van accountverwijdering, met een bufferperiode van 30 dagen voor niet-PII gegevens. |
| `:User` nodes    | Zolang het account actief is                | Node moet verwijderd worden wanneer de gelinkte PostgreSQL row wordt verwijderd.                                                                      |

### Rechten van betrokkenen

| Recht                           | Betrekking tot                                                                | Notities                                                                                                                                                                                               |
|---------------------------------|-------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Toegang (Art. 15)               | `:Comment` nodes en `:User` node                                              | Alle comment inhoud moet worden verstrekt in elke aanvraag                                                                                                                                             |
| Rectificatie (Art. 16)          | `:User` node eigenschappen (`firstName`, `prefix`, `lastName`                 | Naam informatie moet binnen 5 minuten worden geüpdate via een reconciliation job. Comment content kan zelf niet worden aangepast, dus daar is geen rectificatieproces voor nodig.                      |
| Verwijdering (Art. 17)          | `commentBody`, `creatorId`, `:User` node eigenschap, `[:AUTHORED_BY]` relatie | Zie Issues hieronder voor details.                                                                                                                                                                     |
| Restrictie (Art. 18)            | Alle PII gegevens op `:Comment` en `:User` nodes                              | Er moet een mechanisme zijn om gegevens niet te processen zonder deze te verwijderen.                                                                                                                  |
| Dataoverdraagbaarheid (Art. 20) | `commentBody`. `createdAt` per `:Comment` node                                | Gegevens moeten worden geëxporteerd in een gestructureerd, machine-leesbaar formaat (zoals JSON) dat gemakkelijk kan worden overgedragen aan een andere controller, indien verzocht door de gebruiker. |

---

## Issues
### Huidige soft-delete voldoet niet aan het recht op verwijdering
De huidige implementatie van comment verwijdering in Neo4j is een soft-delete, waarbij de `deletedAt` timestamp wordt ingesteld en de `commentBody` in de applicatielaag wordt gemaskeerd, terwijl de originele `commentBody` aanwezig blijft in Neo4j.
Deze aanpak voldoet niet aan het recht op verwijdering (Art. 17).

Om te voldoen aan de GDPR vereisten, moet de implementatie worden aangepast op een van de volgende manieren:
- `commentBody` volledig overschrijven met een placeholder tekst bij verwijdering, zodat de originele inhoud niet langer aanwezig is in Neo4j.
- De `:Comment` node volledig verwijderen uit Neo4j bij verwijdering. Let hierbij op dat er een tombstone node voor in de plaats moet komen om referentiële integriteit te behouden voor bestaande relaties.

Dit moet worden opgelost voordat een workflow voor gebruikersverzoeken tot verwijdering kan worden geïmplementeerd.
