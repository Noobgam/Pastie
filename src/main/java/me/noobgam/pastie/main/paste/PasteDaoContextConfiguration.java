package me.noobgam.pastie.main.paste;

import com.mongodb.async.client.MongoClient;
import me.noobgam.pastie.core.mongo.MongoAsyncCollectionX;
import me.noobgam.pastie.main.mongo.MongoClientContextConfiguration;
import me.noobgam.pastie.main.mongo.MongoUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(
        MongoClientContextConfiguration.class
)
public class PasteDaoContextConfiguration {
    @Bean
    public PasteDao pasteDao(MongoClient client) {
        MongoAsyncCollectionX<String, Paste> coll = MongoUtils.getAsyncCollectionX(
                client,
                "paste_main",
                "pastes",
                Paste.class
        );
        return new PasteMongoDao(coll);
    }
}
