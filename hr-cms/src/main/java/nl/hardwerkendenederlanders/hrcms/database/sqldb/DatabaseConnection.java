package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;


@Component
public class DatabaseConnection {

    @Value("${POSTGRES_DATABASE}")
    private String dbName;
    @Value("${POSTGRES_USER}")
    private String user;
    @Value("${POSTGRES_PASSWORD}")
    private String password;

    public Statement getConnection()throws Exception{
            var props = new Properties();

            String JDBC_URL = "jdbc:postgresql://localhost:6032/"+ dbName +"?currentSchema=public&user="+ user +"&password=" + password;
            // System.out.println(JDBC_URL);

            var connection = DriverManager.getConnection(JDBC_URL);
            var statement = connection.createStatement();


            return statement;
    }

    public void makeDatabase() {
        try {
            Statement connection = getConnection();

            var createTableStatement = "CREATE TABLE articles ( id SERIAL PRIMARY KEY, title VARCHAR(255), content TEXT)";

            connection.execute(createTableStatement);
        }
        catch (Exception e){
            System.out.printf("database creation failed: %s", e);
        }
    }

    public void AddArticle() {
        try {
            Statement connection = getConnection();

            var createTableStatement = "INSERT INTO articles (title, content) VALUES ('title 15','content for the article')";

            connection.execute(createTableStatement);
        }
        catch (Exception e){
            System.out.printf("database creation failed: %s", e);
        }

    }


}
