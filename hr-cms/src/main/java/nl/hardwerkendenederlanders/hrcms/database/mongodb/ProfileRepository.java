package nl.hardwerkendenederlanders.hrcms.database.mongodb;

import nl.hardwerkendenederlanders.hrcms.models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    //CREATE and UPDATE -> profilereposityory.save(profile)
    //UPDATE -> MongoDb saves the whole document again so just call save()
    //DELETE -> deleteById(UUID id) built in, id is username
    //FINDBYID -> findById(UUID id)

    Optional<Profile> findByUsername(String username);
}
