package nl.hardwerkendenederlanders.hrcms.mongodb;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
MongoDatabase database = mongoClient.getDatabase("myDatabase");

public class ProfileRepository {
    private final MongoCollection<Document> collection;

    public UserRepository(MongoDatabase database) {
        this.collection = database.getCollection("users");
    }
}
