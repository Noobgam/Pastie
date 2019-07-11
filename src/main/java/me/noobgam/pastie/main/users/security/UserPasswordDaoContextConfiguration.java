package me.noobgam.pastie.main.users.security;

import com.mongodb.async.client.MongoClient;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import me.noobgam.pastie.main.mongo.MongoClientContextConfiguration;
import me.noobgam.pastie.main.mongo.MongoUtils;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MongoClientContextConfiguration.class)
public class UserPasswordDaoContextConfiguration {
    @Bean
    public UserPasswordDao userPasswordDao(MongoClient client) {
        MongoAsyncCollectionX<ObjectId, UserPassword> coll = MongoUtils.getAsyncCollectionX(
                client,
                "paste_auth",
                "credentials",
                UserPassword.class
        );
        return new UserPasswordMongoDao(coll);
    }
}
