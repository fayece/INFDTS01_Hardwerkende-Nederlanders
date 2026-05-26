package nl.hardwerkendenederlanders.hrcms.preformancetests.seeding;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.*;
import nl.hardwerkendenederlanders.hrcms.models.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class databaseSeeder {

    private final JdbcTemplate db;
    private final RoleRepository roleRepository;

    private final Random rand = new Random();

    private UUID createNumericUUID(int number){
        return UUID.fromString(String.format("%32s", number).replace(' ', '0').replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    @Transactional
    public void seed(){

        System.out.println("Running Seeder");
        var allRoles = roleRepository.findAll();

        List<UUID> users = new ArrayList<>();

        var userRole = allRoles.stream().filter(role -> Objects.equals(role.getInternalName(), "USER")).findFirst().get();
        var contentManager = allRoles.stream().filter(role -> Objects.equals(role.getInternalName(), "CONTENT_MANAGER")).findFirst().get();

        // users
        String query = "INSERT INTO users (id, first_name, prefix, last_name, email, password_hash, role_id, active) VALUES";
        for (int i = 1; i < 1_000; i++) {
            var firstNames = Arrays.asList("Berend", "Henk", "Frits", "Albert",
                    "Joost", "Ben", "Sjaak", "Willem", "Roderik", "Stein",
                    "Johan", "Annabell", "Emma", "Guusje", "Annemarie", "Maria");
            String firstName = firstNames.get(rand.nextInt(firstNames.size()));
            String lastName = firstNames.get(rand.nextInt(firstNames.size())) + "son";

            query = query.concat("('" +
                    createNumericUUID(i) + "', '" +
                    firstName + "', " +
                    "null, '" +
                    lastName + "', '" +
                    i + "@theorg.nl', " +
                    "'secret', '" +
                    userRole.getId() + "', " +
                    "true),"
                    );
            users.add(createNumericUUID(i));
        }
        query = query.substring(0, query.length() - 1); // remove trailing comma
        query += ";";
        db.update(query);

        System.out.println("Users Seeded");

        // content managers

        List<UUID> contentManagers = new ArrayList<>();

        query = "INSERT INTO users (id, first_name, prefix, last_name, email, password_hash, role_id, active) VALUES";
        for (int i = 2000; i < 2050; i++) {

            var firstNames = Arrays.asList("Berend", "Henk", "Frits", "Albert",
                    "Joost", "Ben", "Sjaak", "Willem", "Roderik", "Stein",
                    "Johan", "Annabell", "Emma", "Guusje", "Annemarie", "Maria");

            String firstName = firstNames.get(rand.nextInt(firstNames.size()));
            String lastName = firstNames.get(rand.nextInt(firstNames.size())) + "son";

            query = query.concat("('" +
                    createNumericUUID(i) + "', '" +
                    firstName + "', " +
                    "null, '" +
                    lastName + "', '" +
                    i + "@theorg.nl', " +
                    "'secret', '" +
                    contentManager.getId() + "', " +
                    "true),"
            );

            contentManagers.add(createNumericUUID(i));
        }
        query = query.substring(0, query.length() - 1); // remove trailing comma
        query += ";";
        db.update(query);
        System.out.println("Content Managers Seeded");



        // Add articles
        List<UUID> articles = new ArrayList<>();

        query = "insert into articles (id, title, text_content, publication_status) VALUES ";
        for (int i = 0; i < 2_000; i++) {
            var words = Arrays.asList("the ", "but ", "so ", "very good idea ", "new ", "created ", "he ", "she ", "they ", "viewers ", "audience ", "exploded ", "amazing ", "find ", "I ", "bad idea ", "hopeless ", ".");
            String articleTitle = "Good Read: ";
            String articleContent = "So I think we should ";
            for (int j = 0; j < 7; j++) {
                articleTitle = articleTitle.concat(words.get(rand.nextInt(words.size())));
            }
            for (int j = 0; j < 100; j++) {
                articleContent = articleContent.concat(words.get(rand.nextInt(words.size())));
            }
            query = query.concat( "('" +
            createNumericUUID(i) + "', '" +
                articleTitle + "', '" +
                articleContent + "', 'PUBLISHED'),"
            );

            articles.add(createNumericUUID(i));
        }
        query = query.substring(0, query.length() - 1); // remove trailing comma
        query += ";";
        db.update(query);
        System.out.println("Articles Seeded");

        // article authors
        query = "INSERT INTO article_authors (article_id, author_id) VALUES ";
        for (int i = 0; i < 4000; i++) {
            var cm = contentManagers.get(rand.nextInt(contentManagers.size()));
            var art = articles.get(rand.nextInt(articles.size()));
            query = query.concat("('" + art + "', '" + cm + "'),");
        }
        query = query.substring(0, query.length() - 1); // remove trailing comma
        query += "ON CONFLICT (article_id, author_id) DO NOTHING;";
        db.update(query);
        System.out.println("Article Authors Seeded");

        //article views
        query = "INSERT INTO article_viewers (article_id, viewer_id) VALUES ";
        for (int i = 0; i < 5000; i++) {
            query = query.concat("('" + articles.get(rand.nextInt(articles.size())) + "', '" + users.get(rand.nextInt(users.size())) + "'),");
        }
        query = query.substring(0, query.length() - 1); // remove trailing comma
        query += "ON CONFLICT (article_id, viewer_id) DO NOTHING;";
        db.update(query);
        System.out.println("Article Views seeded");
    }

    public void wipe(){
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
