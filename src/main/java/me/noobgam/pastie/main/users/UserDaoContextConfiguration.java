package me.noobgam.pastie.main.users;

import com.mongodb.async.client.MongoClient;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import me.noobgam.pastie.main.mongo.MongoClientContextConfiguration;
import me.noobgam.pastie.main.mongo.MongoUtils;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(
        MongoClientContextConfiguration.class
)
public class UserDaoContextConfiguration {
    @Bean
    public UserDao userDao(MongoClient client) {
        MongoAsyncCollectionX<ObjectId, User> coll = MongoUtils.getAsyncCollectionX(
                client,
                "paste_auth",
                "users",
                User.class
        );
        return new UserMongoDao(coll);
    }

    @Bean
    public UserCacheHolder userCacheHolder(UserDao userDao) {
        return new UserCacheHolder(userDao);
    }
}
