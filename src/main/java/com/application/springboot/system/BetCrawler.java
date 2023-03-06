package com.application.springboot.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 
 * @author "paul.perez"
 */
//cron strings 
// 0 0 */1 * * *" once per hour
// 0 */5 * * * *" once every 5 minutes
@Component
@EnableScheduling
public class BetCrawler {
  
    private static final Logger LOGGER = LogManager.getLogger(BetCrawler.class);
    
    @Scheduled(cron = "0 0 */1 * * *")
    @Async
    public void reconcileBets(){
        LOGGER.info("Running the interval scan of active bets ");
    }
}
