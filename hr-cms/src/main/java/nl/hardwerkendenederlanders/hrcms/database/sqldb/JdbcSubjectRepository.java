package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.SubjectRepository;
import nl.hardwerkendenederlanders.hrcms.models.Subject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcSubjectRepository implements SubjectRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcSubjectRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbc = namedParameterJdbcTemplate;
    }

    protected RowMapper<Subject> rowMapper() {
        return (rs, _) -> new Subject(rs.getObject("id", UUID.class), rs.getString("subject_name"));
    }

    @Override
    public Subject[] findAll() {
        String query = """
            SELECT *
            FROM subjects
            LIMIT 100;
        """;
        List<Subject> subjects = jdbc.query(query, rowMapper());
        return subjects.toArray(new Subject[0]);
    }

    @Override
    public void insert(Subject subject) {
        var query = """
        INSERT INTO subjects
        VALUES (:id, :subject_name);
        """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("id", subject.getId());
        mapping.addValue("subject_name", subject.getSubjectName());
        jdbc.queryForObject(query, mapping, rowMapper());
    }
}
