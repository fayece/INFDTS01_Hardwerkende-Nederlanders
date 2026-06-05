package nl.hardwerkendenederlanders.hrcms.database.mongodb;

import java.util.Optional;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    Optional<Profile> findByUsername(String username);
}
