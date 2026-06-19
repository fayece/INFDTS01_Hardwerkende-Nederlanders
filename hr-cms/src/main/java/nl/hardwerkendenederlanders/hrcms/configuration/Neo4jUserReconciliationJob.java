package nl.hardwerkendenederlanders.hrcms.configuration;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.graphdb.Neo4jUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Neo4jUserReconciliationJob {

    private final UserRepository userRepository;
    private final Neo4jUserRepository neo4jUserRepository;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void reconcile() {
        DbSessionContext.set(DbSessionRole.ADMINISTRATOR, null);
        try {
            userRepository.findAllUsers().forEach(neo4jUserRepository::upsert);
        } finally {
            DbSessionContext.clear();
        }
    }
}
