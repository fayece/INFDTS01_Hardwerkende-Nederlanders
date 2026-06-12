# Database RBAC voor PostgreSQL

Dit document definieert de PostgreSQL login rollen (actoren) en groepen (privileges) die gebruikt worden om toegang te krijgen tot de hr-cms database.
Ook worden de rechten die op tabel- en schemaniveau worden toegewezen hier beschreven.

## Gerelateerde documenten
- [PII Classificatie voor PostgreSQL](pii-classification-postgresql.md)

[//]: # (TODO: link in pii-classification-postgresql.md naar dit document zodra pii in dev staat)

---

## Doelstellingen
- **Principle of Least Privilege**: Rollen krijgen alleen de minimale privileges die ze nodig hebben om hun taken uit te voeren.
  Bijvoorbeeld, de `cms_role_unauthenticated` heeft vrijwel geen toegang, terwijl `cms_role_administrator` bredere toegang heeft.
- **Separation of Duties**: Verschillende rollen zijn gescheiden op basis van hun verantwoordelijkheden.
  Bijvoorbeeld, `cms_role_content_manager` kan content beheren maar heeft geen toegang tot gebruikersbeheer, terwijl `cms_role_administrator` beide kan doen.

## Overwegingen
- **Applicatielaag vs Database-laag**: De rollen en privileges waren initieel ontworpen om op applicatieniveau te worden gehandhaafd.
  Dit ontwerp legt hierdoor tevens de basis voor de PostgreSQL database-rollen en -privileges.
- **Extra Rollen**: Naast de rollen die direct gebruikt en beheerd worden door de applicatie, zijn er extra rollen opgenomen voor operationele doeleinden.
  Deze extra rollen zijn niet gekoppeld aan in-app RBAC-rollen, maar zijn nodig voor taken zoals database migraties, back-ups, en moderatie van audit logs.
- **RLS**: Row-Level Security is niet opgenomen in dit initiële ontwerp, maar de "own row" notaties in de tabellen geven aan waar RLS in de toekomst kan worden toegepast.
  Voor nu vertrouwt het ontwerp op de applicatielaag om `WHERE` clauses te gebruiken om toegang tot rijen te beperken op basis van de geauthenticeerde gebruiker.
  Zie [Issues](#issues) voor meer details.

---

## Login Rollen

| Rol             | Doel                                                                                                                                                                                                                                                    |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `cms_app`       | De databaseverbinding van de applicatie. Verbindt met de standaard sessierol `cms_role_unauthenticated` en voert `SET ROLE` uit per verzoek op basis van de in-app RBAC rol van de geauthenticeerde gebruiker (Administrator / Content Manager / User). |
| `cms_flyway`    | Schema-eigenaar, alleen gebruikt door de Flyway migratie runner tijdens deploys. Wordt nooit gebruikt door de draaiende applicatie.                                                                                                                     |
| `cms_backup`    | Read-only account voor backup- en dump-jobs (zoals `pg_dump`). Kan alle schema's lezen, inclusief `pii` en `pii_strict`, maar kan nergens schrijven.                                                                                                    |
| `cms_moderator` | Dedicated account voor systeemmoderators/operators. Wordt gebruikt om het `integrity_logs` audit trail te bekijken. Gescheiden van de in-app RBAC hiërarchie en van `cms_app`.                                                                          |
| `cms_superuser` | Dedicated, gedocumenteerde superuser account voor DBA/emergency werk. Vervangt gebruik van de standaard PostgreSQL superuser. Moet spaarzaam worden gebruikt en worden geaudit.                                                                         |

## Groep Rollen

Momenteel zijn er vier groep rollen die worden gebruikt door `cms_app` voor in-app RBAC.
Deze rollen zijn hiërarchisch opgebouwd, waarbij elke hogere rol de privileges van de vorige rol erft, plus extra privileges die specifiek zijn voor die rol.
Deze privileges kunnen veranderen naarmate de applicatie-eisen evolueren, dus de tabellen hieronder beschrijven enkel de huidige staat van privileges per rol.
Als maintainer van deze rollen is het belangrijk deze tabellen bij te werken zodra er wijzigingen worden aangebracht in de privileges, zowel in de database als in de applicatielaag, om ervoor te zorgen dat dit document een accurate bron van waarheid blijft.

| Rol                        | Doel                                                                                                                                                     | Gekoppeld aan                                                       |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| `cms_role_unauthenticated` | Enkel `INSERT` op `integrity_logs` voor niet-geautenticeerde sessies.                                                                                    | Standaard sessierol voor `cms_app`                                  |
| `cms_role_user`            | Voegt eigen profiel toegang toe aan `pii`/`pii_strict`, en engagement acties (viewen, media uploads).                                                    | In-app RBAC rol "User"                                              |
| `cms_role_content_manager` | Voegt volledige artikel/commentaar/media/subject management toe, en leesrechten op auteur namen.                                                         | In-app RBAC rol "Content Manager"                                   |
| `cms_role_administrator`   | Voegt accountbeheer toe (create/update/delete gebruikers, beheren rollen & permissies), inclusief `pii`/`pii_strict` schrijfrechten voor elke gebruiker. | In-app RBAC rol "Administrator"                                     |
| `cms_role_moderator`       | Alleen toegang tot `integrity_logs`.                                                                                                                     | Alleen voor `cms_moderator` login rol, niet gebruikt door `cms_app` |

> ## `Integrity logs` design
> `integrity logs` wordt behandeld als een write-mostly audit trail, niet als applicatiedata:
> - Elke sessie, inclusief de standaard `cms_role_unauthenticated` van `cms_app`, kan events in de audit trail toevoegen via `INSERT`.
> - Alleen `cms_role_moderator`, `cms_flyway`, `cms_backup`, en `cms_superuser` kunnen `SELECT` of `DELETE` uitvoeren op `integrity_logs`.
> - Geen enkele in-app RBAC rol (`cms_role_content_manager`, `cms_role_administrator`) heeft toegang tot `integrity_logs`, gezien het een operationele zorg is, gescheiden van applicatiebeheer.

---

## Privileges per schema en tabel

Elke cel is een combinatie van privileges op schema- en tabelniveau.

Legenda per positie:
- **S / I / U / D** (hoofdletter) = privilege is toegekend (`SELECT` / `INSERT` / `UPDATE` / `DELETE`)
- **s / i / u / d** (kleine letter) = privilege is toegekend op tabelniveau, maar beperkt op rijniveau. De specifieke beperkingen kunnen verschillen per tabel. Hieronder volgen de algemene richtlijnen en de uitzonderingen:
  - *Algemene richtlijn*: Beperkt tot de eigen rij van de geauthenticeerde gebruiker. Toegang wordt beperkt met een `WHERE` clause in de applicatielaag.
    Zie [Issues](#issues) voor details over de afwezigheid van Row-Level Security in dit ontwerp.
  - *`roles`, `permissions` en `role_permissions`*: Beperkt tot de rij/rijen die overeenkomen met de `role_id` van de geauthenticeerde gebruiker.
- **-** = privilege op deze positie niet toegekend

Tabellen met `*` zijn nog niet geïmplementeerd, maar zijn opgenomen in de tabellen voor volledigheid, op basis van de geplande PII schema splitsing. Zie [Issues](#issues).
> **Let op**: De tabellen zijn gemaakt met de intentie om de ideale privileges aan te tonen zodra de [Issues](#issues) zijn opgelost.

### `public`

| Rol                            | users  | roles  | permissions | role_permissions | articles | article_authors | article_viewers | media_items | subjects | integrity_logs |
|--------------------------------|--------|--------|-------------|------------------|----------|-----------------|-----------------|-------------|----------|----------------|
| `cms_role_unauthenticated`     | `----` | `----` | `----`      | `----`           | `----`   | `----`          | `----`          | `----`      | `----`   | `-I--`         |
| `cms_role_user`                | `S---` | `s---` | `s---`      | `s---`           | `S---`   | `S---`          | `Si--`          | `SI--`      | `S---`   | `-I--`         |
| `cms_role_content_manager`     | `S---` | `s---` | `s---`      | `s---`           | `SIUD`   | `SIUD`          | `Si--`          | `SI--`      | `SIU-`   | `-I--`         |
| `cms_role_administrator`       | `SI--` | `S---` | `s---`      | `s---`           | `SIUD`   | `SIUD`          | `Si--`          | `SI--`      | `SIU-`   | `-I--`         |
| `cms_role_moderator`           | `----` | `----` | `----`      | `----`           | `----`   | `----`          | `----`          | `----`      | `----`   | `S--D`         |
| `cms_flyway` / `cms_superuser` | `SIUD` | `SIUD` | `SIUD`      | `SIUD`           | `SIUD`   | `SIUD`          | `SIUD`          | `SIUD`      | `SIUD`   | `SIUD`         |
| `cms_backup`                   | `S---` | `S---` | `S---`      | `S---`           | `S---`   | `S---`          | `S---`          | `S---`      | `S---`   | `S---`         |

### `pii`

| Rol                            | users_pii | article_authors `*` | articles `*` | article_viewers `*` | integrity_logs `*` |
|--------------------------------|-----------|---------------------|--------------|---------------------|--------------------|
| `cms_role_unauthenticated`     | `----`    | `----`              | `----`       | `----`              | `-I--`             |
| `cms_role_user`                | `s-u-`    | `S---`              | `S---`       | `si--`              | `-I--`             |
| `cms_role_content_manager`     | `S-u-`    | `SIU-`              | `SIU-`       | `Si--`              | `-I--`             |
| `cms_role_administrator`       | `SIU-`    | `SIU-`              | `SIU-`       | `Si--`              | `-I--`             |
| `cms_role_moderator`           | `----`    | `----`              | `----`       | `----`              | `S--D`             |
| `cms_flyway` / `cms_superuser` | `SIUD`    | `SIUD`              | `SIUD`       | `SIUD`              | `SIUD`             |
| `cms_backup`                   | `S---`    | `S---`              | `S---`       | `S---`              | `S---`             |

> `cms_role_content_manager` en `cms_role_administrator` hebben geen `D` op `pii.article_authors` en `pii.articles`:
> rijen worden verwijderd via `ON DELETE CASCADE` vanuit respectievelijk `public.article_authors` en `public.articles`, geen directe `DELETE` toegang nodig.
>
> `cms_role_administrator` heeft om dezelfde reden geen `D` op `pii.users_pii`: rijen worden verwijderd via `ON DELETE CASCADE` vanuit `users`.
>
> `cms_role_moderator` heeft geen `I` of `U` op `pii.integrity_logs`: alleen `INSERT` via de applicatielaag, geen directe database `INSERT` of `UPDATE` toegang.
> Dit is enkel om de audit trail te kunnen bekijken en op te schonen.
> `DELETE` mag niet zomaar, en die acties moeten ook worden vastgelegd in de audit trail zelf. Zie [Issues](#issues).

### `pii_strict`

| Rol                            | users_pii_strict |
|--------------------------------|------------------|
| `cms_role_unauthenticated`     | `----`           |
| `cms_role_user`                | `--u-`           |
| `cms_role_content_manager`     | `--u-`           |
| `cms_role_administrator`       | `-IU-`           |
| `cms_role_moderator`           | `----`           |
| `cms_flyway` / `cms_superuser` | `SIUD`           |
| `cms_backup`                   | `S---`           |

> `cms_role_administrator` heeft geen `D` op `pii_strict.users_pii_strict`:
> rijen worden verwijderd via `ON DELETE CASCADE` vanuit `users`, geen directe `DELETE` toegang nodig.

## Issues
### Row-level security wordt niet afgedwongen door de database
De tabellen met de kleine letter notaties (**r / i / u / d**) vertrouwen op de applicatielaag om `WHERE` clauses te gebruiken om toegang te beperken tot de eigen rij van de geauthenticeerde gebruiker.
Dit betekent dat een fout in de applicatielaag (zoals het vergeten van een `WHERE` clause) ervoor kan zorgen dat gebruikers toegang krijgen tot gegevens van andere gebruikers, wat een beveiligingsrisico is.
Ook kan een kwaadwillige gebruiker `WHERE` clauses omzeilen als ze directe toegang tot de database hebben, wat nog een beveiligingsrisico is.
Row-Level Security (RLS) in PostgreSQL zou een robuustere oplossing bieden door deze beperkingen op database-niveau af te dwingen, maar is vanwege de scope niet in dit initiële ontwerp opgenomen.
RLS zal zo spoedig mogelijk worden geïmplementeerd in een toekomstige iteratie om deze beveiligingsrisico's te verminderen.

Hetzelfde geldt voor de hoofdletter-privileges (bijvoorbeeld volledige `S` op `articles`): de database staat toegang tot alle rijen toe, ongeacht `publication_status`, en vertrouwt op de applicatielaag om bijvoorbeeld `DRAFT`/`ARCHIVED` artikelen te filteren voor wie ze niet mag zien.

Daarnaast vertrouwt dit ontwerp op `SET ROLE`/`RESET ROLE` per request om `cms_app` van rol te laten wisselen op basis van de geauthenticeerde gebruiker.
Vergeet de applicatielaag dit (bijvoorbeeld bij hergebruik van een pooled connection), dan kan een request de privileges van een eerdere rol overerven.
Dit is dezelfde categorie risico als hierboven: de database kan dit niet zelf afdwingen en vertrouwt op de applicatielaag om de juiste rol/voorwaarden per request toe te passen.

### `pii.article_authors`, `pii.articles`, `pii.article_viewers` en `pii.integrity_logs` zijn nog niet geïmplementeerd
Het opsplitsen van de PII in `article_authors`, `articles`, `article_viewers` en `integrity_logs` is nog niet geïmplementeerd.
Dit betekent dat de huidige implementatie nog steeds brede toegang tot PII heeft binnen het `public` schema.
De tabellen hierboven zijn dan ook gebaseerd op het eindontwerp waarbij deze PII-gegevens zijn opgesplitst in aparte tabellen binnen het `pii` schema.
De daadwerkelijke privileges kunnen dan ook pas worden afgedwongen zodra deze PII-splitsing is voltooid.

### `cms_role_moderator` acties op `pii.integrity_logs` worden niet gelogd
Gezien de `cms_role_moderator` geen `INSERT` of `UPDATE` privileges heeft op `pii.integrity_logs`, kunnen moderators rijen verwijderen zonder dat deze acties vastgelegd worden in de audit trail.
Dit is een beveiligingsrisico, aangezien het mogelijk is dat een moderator belangrijke audit logs verwijdert zonder dat dit wordt gedetecteerd.
Een mogelijke oplossing is om een aparte audit trail te implementeren voor moderator acties, of om triggers te gebruiken via een superuser account (of een aparte `cms_role_audit` rol) die deze acties logt in een aparte tabel voordat ze worden uitgevoerd.
Hoe dan ook moet er hier een closed loop op worden gezet zodat alles vastgelegd wordt, en enkel verouderde logs verwijderd kunnen worden, niet zomaar alles.

### `cms_role_unauthenticated` heeft geen toegang om in te loggen
Volgens de tabellen hierboven heeft `cms_role_unauthenticated` `----` op `users`, `pii.users_pii` en `pii_strict.users_pii_strict`.
Iets in de login-flow moet echter de `password_hash` van de opgegeven username kunnen lezen om te verifiëren, en daarna `role_id`/`active` kunnen opzoeken voor `SET ROLE`.

Een naïeve oplossing is om `cms_role_unauthenticated` leesrechten te geven op deze tabellen, beperkt tot de rij die hoort bij de opgegeven username (`WHERE username = ?`).
Dit werkt, maar betekent dat iedereen met een database-verbinding die `cms_role_unauthenticated` gebruikt, de `password_hash`, `role_id` en `active` van elke gebruiker kan opvragen door simpelweg een username te gebruiken in de query.

Een beter alternatief is een PostgreSQL native functie (bijv. `verify_login(username, password)`) die zelf met verhoogde rechten deze tabellen leest, de wachtwoord-vergelijking intern uitvoert, en alleen `user_id`/`role_id`/`active` teruggeeft bij een geslaagde login.
`password_hash` verlaat de database dan ook nooit.
Of dit alternatief goed past hangt af van of het huidige hashing-algoritme (BCrypt, zie `AuthServiceImpl`) ondersteund kan worden binnen zo'n functie (bijvoorbeeld via `pgcrypto`).

Voor nu is de naïeve oplossing geïmplementeerd om de login-flow werkend te krijgen, maar dit is een belangrijk aandachtspunt voor een toekomstige iteratie.
