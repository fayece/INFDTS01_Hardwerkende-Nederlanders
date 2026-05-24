package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.graphdb.Neo4jUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class DualWriteUserRepository implements UserRepository {

    private final JdbcUserRepository jdbcUserRepository;
    private final Neo4jUserRepository neo4jUserRepository;

    public DualWriteUserRepository(JdbcUserRepository jdbcUserRepository, Neo4jUserRepository neo4jUserRepository) {
        this.jdbcUserRepository = jdbcUserRepository;
        this.neo4jUserRepository = neo4jUserRepository;
    }

    @Override
    public void insert(User user) {
        jdbcUserRepository.insert(user);
        trySync(() -> neo4jUserRepository.upsert(user));
    }

    @Override
    public void update(User user) {
        jdbcUserRepository.update(user);
        trySync(() -> neo4jUserRepository.upsert(user));
    }

    @Override
    public void deleteById(UUID id) {
        jdbcUserRepository.deleteById(id);
        trySync(() -> neo4jUserRepository.deleteById(id));
    }

    @Override
    public void updateActivityById(UUID id, boolean setActive) {
        jdbcUserRepository.updateActivityById(id, setActive);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jdbcUserRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jdbcUserRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcUserRepository.findAllUsers();
    }

    @Override
    public List<User> findAllPaginated(Integer page, Integer amount) {
        return jdbcUserRepository.findAllPaginated(page, amount);
    }

    @Override
    public List<User> findByNameOrEmailPaginated(String name, int page, int amount) {
        return jdbcUserRepository.findByNameOrEmailPaginated(name, page, amount);
    }

    @Override
    public List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount) {
        return jdbcUserRepository.findUserOnActivityPaginated(isActive, page, amount);
    }

    @Override
    public Integer countByActive(boolean active) {
        return jdbcUserRepository.countByActive(active);
    }

    @Override
    public Integer countAll() {
        return jdbcUserRepository.countAll();
    }

    @Override
    public Integer countByNameOrEmailPaginated(String name) {
        return jdbcUserRepository.countByNameOrEmailPaginated(name);
    }

    private void trySync(Runnable neo4jOp) {
        try {
            neo4jOp.run();
        } catch (Exception _) {
        }
    }
}
