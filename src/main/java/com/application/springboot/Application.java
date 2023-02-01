package com.application.springboot;

import java.io.File;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.index.Index;

/**
 *
 * @author "paul.perez"
 */
@SpringBootApplication
@EnableSwagger2
public class Application {

    @Value("${application.hostname}") private String hostname; 

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args); 
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.application.springboot")).build();
    }
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://" + hostname + ":27017");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate template = new MongoTemplate(mongoClient(), "bettingDB");
        template.indexOps("users").ensureIndex(new Index("email", Direction.ASC).unique());
        template.indexOps("users").ensureIndex(new Index("userName", Direction.ASC).unique());
        return template;
    } 
}
