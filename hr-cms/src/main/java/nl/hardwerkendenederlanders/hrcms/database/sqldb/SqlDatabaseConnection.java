package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.sql.DriverManager;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqlDatabaseConnection {

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
}
