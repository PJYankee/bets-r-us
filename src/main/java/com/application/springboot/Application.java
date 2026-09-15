package com.application.springboot;

import com.application.springboot.system.OddsApiHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author "paul.perez"
 * swagger endpoint accessible at http://localhost:8080/swagger-ui.html#/
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    @Value("${application.hostname}") private String hostname; 
    @Value("${apikey}") private String apikey;
    @Value("${baseurl}") private String baseurl;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args); 
    }
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + hostname + ":27017");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "bettingDB");
    } 
    
    @Bean
    public OddsApiHandler oddsApiHandler(){
        OddsApiHandler handler = new OddsApiHandler();
        handler.apikey = apikey;
        handler.baseUrl = baseurl;
    
        return handler;
    }
  
}
