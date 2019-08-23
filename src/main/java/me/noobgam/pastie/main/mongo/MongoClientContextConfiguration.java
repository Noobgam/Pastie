package me.noobgam.pastie.main.mongo;

import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import me.noobgam.pastie.core.properties.PropertiesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class MongoClientContextConfiguration {
    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = buildCredentials();
        String[] hosts = PropertiesHolder.getProperty("mongo.hosts").split(",");
        int port = PropertiesHolder.getIntProperty("mongo.port");

        ClusterSettings clusterSettings =
                ClusterSettings.builder()
                        .hosts(
                                Stream.of(hosts).map(name -> new ServerAddress(
                                        name, port
                                )).collect(Collectors.toList())
                        )
                        .build();

        return MongoClients.create(
                MongoClientSettings.builder()
                        .credential(credential)
                        .clusterSettings(clusterSettings)
                        .readPreference(ReadPreference.nearest())
                        .writeConcern(WriteConcern.W1)
                        .build()
        );
    }

    private MongoCredential buildCredentials() {
        String database = PropertiesHolder.getProperty("mongo.auth.db");
        String user = PropertiesHolder.getProperty("mongo.login");
        String password = PropertiesHolder.getProperty("mongo.password");
        return MongoCredential.createCredential(
                user, database, password.toCharArray()
        );
    }
}
