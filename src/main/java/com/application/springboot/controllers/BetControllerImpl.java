package com.application.springboot.controllers;

import com.application.springboot.interfaces.BetInterface;
import com.application.springboot.objects.Bet;
import com.application.springboot.objects.Odds;
import com.application.springboot.system.BetStatusEnum;
import com.application.springboot.system.BetTypeEnum;
import com.application.springboot.system.OddsApiHandler;
import com.application.springboot.system.SportsEnum;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class BetControllerImpl implements BetInterface{
    private static final Logger LOGGER = LogManager.getLogger(BetControllerImpl.class);
    private OddsApiHandler handler;
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
  
    @Autowired
    public void setOddsApiHandler(OddsApiHandler oddsApiHandler) {
        this.handler = oddsApiHandler;
    }    

    @Override
    @ApiOperation(value = "Place a bet on a game", notes = "Returns a Bet object")      
    @GetMapping("/bets/placeBet")
    public Bet placeBet(String username, String eventId, String selection, Float bet_amount, BetTypeEnum bet_type, SportsEnum sport) {
        Date now = new Date();
        Bet bet = new Bet();
        Odds odds = handler.getOdds(eventId, sport);
        
        bet.setUsername(username);
        bet.setEventId(eventId);
        bet.setSelection(selection);
        bet.setBet_amount(bet_amount);
        bet.setBet_type(bet_type);
        bet.setSport(sport);
        bet.setDate(now.toString());
        bet.setPayout(calculatePayout(odds, bet_amount, bet_type, selection));
        bet.setStatus(BetStatusEnum.PLACED);
        
         LOGGER.debug("User " + username +  "successfully placed bet on " + selection);
            mongoTemplate.save(bet);

        return bet;
    }

    @Override
    @ApiOperation(value = "Retrieve a history of all bets placed by the user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getAllBetHistory")    
    public List<Bet> getAllBetHistory(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));        
        List<Bet> betList = mongoTemplate.findAll(Bet.class);
        
        return betList;
    }

    @Override
    @ApiOperation(value = "Retrieve a history of winning bets placed by the user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getWonBetHistory") 
    public List<Bet> getWonBetHistory(String username) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    @ApiOperation(value = "Retrieve a history of lost bets placed by the user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getLostBetHistory")     
    public List<Bet> getLostBetHistory(String username) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    @ApiOperation(value = "Retrieve a List of in progress bets by user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getInProgressBets")     
    public List<Bet> getInProgressBets(String username) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    private Float calculatePayout (Odds odds, Float bet_amount, BetTypeEnum betType, String selection){
    Float payout = bet_amount;
    
    payout = bet_amount * 2.253F;
    
    return payout;
    }
    
        /* Do not expose the following endpoints to the end user */
    @ApiIgnore
    @GetMapping("/bets/deleteAllBets")
    public void deleteAllBets() {
        List<Bet> allBets = listAllBets();
        for (Bet bet : allBets) {
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(bet.getUsername()));
            mongoTemplate.findAllAndRemove(query, Bet.class, "bets");
        }
    }

    @GetMapping("/bets/listAllBets")
    @ResponseBody
 //   @ApiIgnore
    public List<Bet> listAllBets() {
        List<Bet> bets = mongoTemplate.findAll(Bet.class);
        return bets;
    }    
}
