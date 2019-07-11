package me.noobgam.pastie.main.users.cookies;

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
public class CookieDaoContextConfiguration {
    @Bean
    public CookieDao cookieDao(MongoClient client) {
        MongoAsyncCollectionX<ObjectId, Cookie> coll = MongoUtils.getAsyncCollectionX(
                client,
                "paste_auth",
                "cookies",
                Cookie.class
        );
        return new CookieMongoDao(coll);
    }
}
