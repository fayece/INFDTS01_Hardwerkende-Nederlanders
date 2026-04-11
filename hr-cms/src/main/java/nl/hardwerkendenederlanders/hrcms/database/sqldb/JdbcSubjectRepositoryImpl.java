package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.SubjectRepository;
import nl.hardwerkendenederlanders.hrcms.models.Subject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class JdbcSubjectRepositoryImpl implements SubjectRepository {
    private final SqlDatabaseConnection dbCon;

    public  JdbcSubjectRepositoryImpl(SqlDatabaseConnection dbCon){
        this.dbCon = dbCon;
    }

    protected RowMapper<Subject> rowMapper() {
        return (rs, _) -> new Subject(
                rs.getObject("id", UUID.class),
                rs.getString("subject_name")
        );
    }

    private Subject[] mapRows(ResultSet rs) throws SQLException {
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        while (rs.next())
            subjects.add(rowMapper().mapRow(rs, 1));
        return  subjects.toArray(new Subject[0]);
    }

    @Override
    public Subject[] GetAll() {
        try (Connection con = dbCon.GetConnection()){
            var result = con.prepareStatement("""
                SELECT *
                FROM subjects
                LIMIT 100;
            """).executeQuery();
            return mapRows(result);
        }
        catch (SQLException e){
            System.out.println();
        }
        return new Subject[0];
    }
}
