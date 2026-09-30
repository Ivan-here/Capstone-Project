package com.example.listingservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import com.example.listingservice.model.Listing;
@Configuration
public class InventoryVersionMigration {
    @Bean ApplicationRunner initializeInventoryVersions(MongoTemplate mongo) {
        return args -> mongo.updateMulti(Query.query(Criteria.where("version").exists(false)), new Update().set("version", 0L), Listing.class);
    }
}
