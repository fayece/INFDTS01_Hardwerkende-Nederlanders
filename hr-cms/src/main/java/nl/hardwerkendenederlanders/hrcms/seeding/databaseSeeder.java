package nl.hardwerkendenederlanders.hrcms.seeding;

import java.util.*;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class databaseSeeder {

    private final JdbcTemplate db;
    private final RoleRepository roleRepository;

    private final Random rand = new Random();

    private UUID createNumericUUID(int number) {
        return UUID.fromString(String.format("%32s", number)
                .replace(' ', '0')
                .replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{8})",
                        "$1-$2-$3-$4-$5"));
    }

    public void seed2() {

        System.out.println("Running Seeder");

        final int BATCH_SIZE = 10_000;
        int totalUsers = 100_000;
        int totalManagers = 50_000;
        int totalArticles = 200_000;
        int extraArticleAuthors = 100_000; // every article always has a random author. Field specifies extra authors
        int totalViews = 1_000_000;

        var allRoles = roleRepository.findAll();

        List<UUID> users = new ArrayList<>();
        List<UUID> contentManagers = new ArrayList<>();
        List<UUID> articles = new ArrayList<>();

        var userRole = allRoles.stream()
                .filter(role -> Objects.equals(role.getInternalName(), "USER"))
                .findFirst()
                .orElseThrow();

        var contentManagerRole = allRoles.stream()
                .filter(role -> Objects.equals(role.getInternalName(), "CONTENT_MANAGER"))
                .findFirst()
                .orElseThrow();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode("secret");

        var firstNames = Arrays.asList(
                "Berend",
                "Henk",
                "Frits",
                "Albert",
                "Joost",
                "Ben",
                "Sjaak",
                "Willem",
                "Roderik",
                "Stein",
                "Johan",
                "Annabell",
                "Emma",
                "Guusje",
                "Annemarie",
                "Maria",
                "Jan",
                "Piet",
                "Kees",
                "Dirk",
                "Arie",
                "Bram",
                "Cornelis",
                "Klaas",
                "Harm",
                "Gerrit",
                "Teun",
                "Leendert",
                "Hendrik",
                "Bastiaan",
                "Martinus",
                "Pieter",
                "Rutger",
                "Douwe",
                "Wouter",
                "Barend",
                "Huib",
                "Tjerk",
                "Siebe",
                "Koos",
                "Aart",
                "Floris",
                "Niek",
                "Maarten",
                "Joris",
                "Thijs",
                "Adriaan",
                "Boudewijn",
                "Evert",
                "Rik",
                "Tobias",
                "Roelof",
                "Sybren",
                "Wiebe",
                "Gerben",
                "Arend",
                "Jacobus",
                "Nicolaas",
                "Lambert",
                "Wim",
                "Theo",
                "Karel",
                "Anton",
                "Frans",
                "Geertruida",
                "Grietje",
                "Jannetje",
                "Trijntje",
                "Aaltje",
                "Neeltje",
                "Antje",
                "Maaike",
                "Marijke",
                "Liesbeth",
                "Femke",
                "Renske",
                "Sanne",
                "Tessa",
                "Hilde",
                "Bregje",
                "Willemijn",
                "Aleida",
                "Cornelia",
                "Johanna",
                "Catharina",
                "Petronella",
                "Henriette",
                "Elsje",
                "Doortje",
                "Miep",
                "Jet",
                "Fenna",
                "Nienke",
                "Anouk",
                "Lotte",
                "Hanneke",
                "Joke",
                "Anniek",
                "Suzanna",
                "Rika",
                "Wilhelmina",
                "Alida",
                "Bep",
                "Tiny",
                "Tineke",
                "Saskia",
                "Baukje",
                "Jitske");

        var words = Arrays.asList(
                "the ",
                "but ",
                "so ",
                "very good idea ",
                "new ",
                "created ",
                "he ",
                "she ",
                "they ",
                "viewers ",
                "audience ",
                "exploded ",
                "amazing ",
                "find ",
                "I ",
                "bad idea ",
                "hopeless ",
                ".",

                // Common English words
                "a ",
                "an ",
                "and ",
                "or ",
                "if ",
                "then ",
                "because ",
                "when ",
                "where ",
                "what ",
                "why ",
                "who ",
                "how ",
                "this ",
                "that ",
                "these ",
                "those ",
                "is ",
                "was ",
                "are ",
                "were ",
                "be ",
                "been ",
                "have ",
                "has ",
                "had ",
                "do ",
                "does ",
                "did ",
                "can ",
                "could ",
                "will ",
                "would ",
                "should ",
                "to ",
                "from ",
                "with ",
                "without ",
                "for ",
                "about ",
                "into ",
                "over ",
                "under ",
                "before ",
                "after ",
                "people ",
                "friends ",
                "family ",
                "story ",
                "world ",
                "music ",
                "sound ",
                "voice ",
                "night ",
                "day ",
                "life ",
                "dream ",
                "love ",
                "heart ",
                "mind ",
                "feeling ",
                "beautiful ",
                "perfect ",
                "crazy ",
                "wild ",
                "happy ",
                "sad ",
                "strong ",
                "alone ",
                "everything ",
                "nothing ",
                "something ",
                "anything ",
                "always ",
                "never ",
                "sometimes ",
                "again ",
                "today ",
                "tomorrow ",
                "yesterday ",
                "big ",
                "small ",
                "fast ",
                "slow ",
                "good ",
                "great ",
                "best ",
                "worst ",

                // Pop music themed
                "song ",
                "songs ",
                "album ",
                "albums ",
                "track ",
                "tracks ",
                "single ",
                "hit ",
                "chart ",
                "billboard ",
                "radio ",
                "stream ",
                "streaming ",
                "viral ",
                "trending ",
                "concert ",
                "tour ",
                "festival ",
                "show ",
                "stage ",
                "performance ",
                "dance ",
                "beat ",
                "rhythm ",
                "melody ",
                "chorus ",
                "verse ",
                "hook ",
                "bridge ",
                "drop ",
                "lyrics ",
                "singer ",
                "artist ",
                "band ",
                "producer ",
                "studio ",
                "record ",
                "microphone ",
                "guitar ",
                "piano ",
                "drums ",
                "fans ",
                "crowd ",
                "spotlight ",
                "celebrity ",
                "famous ",
                "superstar ",
                "party ",
                "club ",
                "disco ",
                "romance ",
                "breakup ",
                "emotion ",
                "summer hit ",
                "love song ",
                "dance floor ",
                "top charts ",
                "music video ",
                "world tour ",
                "encore ",
                "headline ",
                "backstage ",
                "acoustic ",
                "remix ",
                "collab ",
                "synth ",
                "bass ",
                "vocals ",
                "autotune ",
                "playlist ",
                "anthem ",
                "pop star ",
                "gold record ",
                "platinum ",
                "number one ",
                "fanbase ",
                "live performance ");

        int firstNameSize = firstNames.size();

        // ============================================================
        // USERS
        // ============================================================

        System.out.println("Seeding users");

        for (int batchStart = 1; batchStart < totalUsers; batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, totalUsers);

            StringBuilder query = new StringBuilder("INSERT INTO users "
                    + "(id, first_name, prefix, last_name, email, password_hash, role_id, active) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {

                String firstName = firstNames.get(rand.nextInt(firstNameSize));
                String lastName = firstNames.get(rand.nextInt(firstNameSize)) + "son";

                int user_num = i + batchStart * BATCH_SIZE;
                UUID userId = createNumericUUID(user_num);

                query.append("('")
                        .append(userId)
                        .append("', '")
                        .append(firstName)
                        .append("', ")
                        .append("null, '")
                        .append(lastName)
                        .append("', '")
                        .append(userId)
                        .append("@theorg.nl', '")
                        .append(password)
                        .append("', '")
                        .append(userRole.getId())
                        .append("', ")
                        .append("true),");

                users.add(userId);
            }

            query.setLength(query.length() - 1);
            query.append(";");

            db.update(query.toString());
        }

        System.out.println("Users Seeded");

        // ============================================================
        // CONTENT MANAGERS
        // ============================================================

        System.out.println("Seeding content managers");

        for (int batchStart = 0; batchStart < totalManagers; batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, totalManagers);

            StringBuilder query = new StringBuilder("INSERT INTO users "
                    + "(id, first_name, prefix, last_name, email, password_hash, role_id, active) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {

                String firstName = firstNames.get(rand.nextInt(firstNameSize));
                String lastName = firstNames.get(rand.nextInt(firstNameSize)) + "son";

                int manager_num = i + batchStart * BATCH_SIZE;
                UUID managerId = createNumericUUID(manager_num);

                query.append("('")
                        .append(managerId)
                        .append("', '")
                        .append(firstName)
                        .append("', ")
                        .append("null, '")
                        .append(lastName)
                        .append("', '")
                        .append(manager_num)
                        .append("@theorg.nl', '")
                        .append(password)
                        .append("', '")
                        .append(contentManagerRole.getId())
                        .append("', ")
                        .append("true),");

                contentManagers.add(managerId);
            }

            query.setLength(query.length() - 1);
            query.append(";");

            db.update(query.toString());
        }

        System.out.println("Content Managers Seeded");

        // ============================================================
        // ARTICLES
        // ============================================================

        System.out.println("Seeding articles");

        for (int batchStart = 0; batchStart < totalArticles; batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, totalArticles);

            StringBuilder query = new StringBuilder(
                    "INSERT INTO articles " + "(id, title, text_content, publication_status) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {

                StringBuilder articleTitle = new StringBuilder("Good Read: ");
                StringBuilder articleContent = new StringBuilder("So I think we should ");

                for (int j = 0; j < 7; j++) {
                    articleTitle.append(words.get(rand.nextInt(words.size())));
                }

                for (int j = 0; j < 100; j++) {
                    articleContent.append(words.get(rand.nextInt(words.size())));
                }

                UUID articleId = createNumericUUID(i + batchStart * BATCH_SIZE);

                query.append("('")
                        .append(articleId)
                        .append("', '")
                        .append(articleTitle)
                        .append("', '")
                        .append(articleContent)
                        .append("', ")
                        .append("'PUBLISHED'),");

                articles.add(articleId);
            }

            query.setLength(query.length() - 1);
            query.append(";");

            db.update(query.toString());
        }

        System.out.println("Articles Seeded");

        // ============================================================
        // ARTICLE AUTHORS
        // ============================================================

        System.out.println("Seeding article authors");
        for (int batchStart = 0; batchStart < articles.size(); batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, articles.size());

            StringBuilder query = new StringBuilder("INSERT INTO article_authors (article_id, author_id) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {
                UUID cm = contentManagers.get(rand.nextInt(contentManagers.size()));
                UUID art = articles.get(i);

                query.append("('").append(art).append("', '").append(cm).append("'),");
            }

            query.setLength(query.length() - 1);

            query.append(" ON CONFLICT (article_id, author_id) DO NOTHING;");

            db.update(query.toString());
        }

        for (int batchStart = 0; batchStart < extraArticleAuthors; batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, extraArticleAuthors);

            StringBuilder query = new StringBuilder("INSERT INTO article_authors (article_id, author_id) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {

                UUID cm = contentManagers.get(rand.nextInt(contentManagers.size()));
                UUID art = articles.get(rand.nextInt(articles.size()));

                query.append("('").append(art).append("', '").append(cm).append("'),");
            }

            query.setLength(query.length() - 1);

            query.append(" ON CONFLICT (article_id, author_id) DO NOTHING;");

            db.update(query.toString());
        }

        System.out.println("Article Authors Seeded");

        // ============================================================
        // ARTICLE VIEWERS
        // ============================================================

        System.out.println("Seeding article viewers");

        for (int batchStart = 0; batchStart < totalViews; batchStart += BATCH_SIZE) {

            int batchEnd = Math.min(batchStart + BATCH_SIZE, totalViews);

            StringBuilder query = new StringBuilder("INSERT INTO article_viewers (article_id, viewer_id) VALUES ");

            for (int i = batchStart; i < batchEnd; i++) {

                query.append("('")
                        .append(articles.get(rand.nextInt(articles.size())))
                        .append("', '")
                        .append(users.get(rand.nextInt(users.size())))
                        .append("'),");
            }

            query.setLength(query.length() - 1);

            query.append(" ON CONFLICT (article_id, viewer_id) DO NOTHING;");

            db.update(query.toString());
        }

        System.out.println("Article Views Seeded");
    }

    public void wipe() {
        db.update("""
            BEGIN;
            DELETE FROM article_viewers;
            DELETE FROM article_authors;
            DELETE FROM articles;
            DELETE FROM users;
            COMMIT;
            """);
    }
}
