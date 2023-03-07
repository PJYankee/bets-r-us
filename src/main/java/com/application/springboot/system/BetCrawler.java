package com.application.springboot.system;

import com.application.springboot.objects.Bet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 
 * @author "paul.perez"
 */
//cron strings 
// 0 0 */1 * * * once per hour
// 0 */5 * * * * once every 5 minutes
@Component
@EnableScheduling
public class BetCrawler {

    private static final Logger LOGGER = LogManager.getLogger(BetCrawler.class);
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(cron = "0 */2 * * * *")
    @Async
    public void reconcileBets() {
        LOGGER.debug("Running the interval scan of active bets ");
        List<Bet> allBets = listAllBets();
        
        String noop = "noop";

    }

    private List<Bet> listAllBets() {
        List<Bet> bets = mongoTemplate.findAll(Bet.class);
        return bets;
    }
}
