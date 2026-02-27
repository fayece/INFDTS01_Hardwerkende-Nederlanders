package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.sql.DriverManager;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection {

    @Value("${spring.datasource.url:#{null}}")
    private String url;

    @Value("${spring.datasource.username:#{null}}")
    private String user;

    @Value("${spring.datasource.password:#{null}}")
    private String password;

    public Statement getConnection() throws Exception {

        var connection = DriverManager.getConnection(url, user, password);

        return connection.createStatement();
    }

    public void makeDatabase() throws Exception {
        Statement connection = getConnection();

        var createTableStatement = "CREATE TABLE articles ( id SERIAL PRIMARY KEY, title VARCHAR(255), content TEXT)";

        connection.execute(createTableStatement);
    }

    public void AddArticle() throws Exception {

        Statement connection = getConnection();

        var createTableStatement =
                "INSERT INTO articles (title, content) VALUES ('title 15','content for the article')";

        connection.execute(createTableStatement);
    }
}
