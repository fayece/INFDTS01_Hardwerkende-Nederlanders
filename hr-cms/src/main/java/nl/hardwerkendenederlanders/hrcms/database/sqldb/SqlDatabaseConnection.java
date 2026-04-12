package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.sql.*;
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

    public Connection GetConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
