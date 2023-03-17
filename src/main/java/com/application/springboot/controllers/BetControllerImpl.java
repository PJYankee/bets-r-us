package com.application.springboot.controllers;

import com.application.springboot.interfaces.BetInterface;
import com.application.springboot.objects.Bankroll;
import com.application.springboot.objects.Bet;
import com.application.springboot.objects.Odds;
import com.application.springboot.objects.User;
import com.application.springboot.system.BetStatusEnum;
import com.application.springboot.system.BetTypeEnum;
import com.application.springboot.system.OddsApiHandler;
import com.application.springboot.system.SportsEnum;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     *
     * @param username
     * @param eventId
     * @param selection
     * @param bet_amount
     * @param bet_type
     * @param sport
     * @return Bet
     */
    @Override
    @ApiOperation(value = "Place a bet on a game", notes = "Returns a Bet object")
    @GetMapping("/bets/placeBet")
    public Bet placeBet(String username, String eventId, String selection, double bet_amount, BetTypeEnum bet_type, SportsEnum sport) {
        Date now = new Date();
        Bet bet = new Bet();
        Bet noBet = new Bet();        
        Odds odds = handler.getOdds(eventId, sport);
        Boolean userExists = getUser(username) != null;
        if (!userExists)
        {
        LOGGER.error("NO BET PLACED, the user " + username + " does not exist in the system");
        return noBet;
        }

        bet.setUsername(username);
        bet.setEventId(eventId);
        bet.setSelection(selection);
        bet.setBet_amount(bet_amount);
        bet.setBet_type(bet_type);
        bet.setSport(sport);
        bet.setDate(now.toString());
        bet.setPayout(calculatePayout(odds, bet_amount, bet_type, selection));
        bet.setStatus(BetStatusEnum.PLACED);
        bet.setOdds(odds);

        try {
            deductBetAmountFromBankroll(username, bet_amount);
            LOGGER.debug("User " + username + "successfully placed bet on " + selection);
            mongoTemplate.save(bet);
        } catch (Exception ex) {
            LOGGER.error("NO BET PLACED : INSUFFICIENT BANKROLL FUNDS", ex);
            return noBet;
        }
        return bet;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    @ApiOperation(value = "Retrieve a history of all bets placed by the user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getAllBetHistory")    
    public List<Bet> getAllBetHistory(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));        
        List<Bet> betList = mongoTemplate.find(query, Bet.class, "bets");
        
        return betList;
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    @ApiOperation(value = "Retrieve a history of winning bets placed by the user", notes = "Returns a list of Bet objects")
    @GetMapping("/bets/getWonBetHistory")
    public List<Bet> getWonBetHistory(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));    
        List<Bet> bets = mongoTemplate.find(query, Bet.class, "bets");
        List<Bet> wonBets = new ArrayList();
        //probably should query with conditions rather than get all and exclude lost bets
        for (Bet bet : bets) {
            if (bet.getStatus() == BetStatusEnum.WIN) {
                wonBets.add(bet);
            }
        }
        return wonBets;
    }

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    @ApiOperation(value = "Retrieve a history of lost bets placed by the user", notes = "Returns a list of Bet objects")          
    @GetMapping("/bets/getLostBetHistory")     
    public List<Bet> getLostBetHistory(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));    
        List<Bet> bets = mongoTemplate.find(query, Bet.class, "bets");
        List<Bet> lostBets = new ArrayList();
        //probably should query with conditions rather than get all and exclude won or pushed bets
        for (Bet bet : bets) {
            if (bet.getStatus() == BetStatusEnum.LOSS) {
                lostBets.add(bet);
            }
        }
        return lostBets;
    }

    /**
     * 
     * @param username
     * @return 
     */
    @Override
    @ApiOperation(value = "Retrieve a List of in progress bets by user", notes = "Returns a list of Bet objects")
    @GetMapping("/bets/getInProgressBets")
    public List<Bet> getInProgressBets(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));            
        List<Bet> bets = mongoTemplate.find(query, Bet.class, "bets");
        List<Bet> openBets = new ArrayList();
        //probably should query with conditions rather than get all and exclude not active bets
        for (Bet bet : bets) {
            if (bet.getStatus() == BetStatusEnum.IN_PROGRESS || bet.getStatus() == BetStatusEnum.PLACED) {
                openBets.add(bet);
            }
        }
        return openBets;
    }

    
    /**
     * 
     * @param odds
     * @param bet_amount
     * @param betType
     * @param selection
     * @return 
     */
    private double calculatePayout(Odds odds, double bet_amount, BetTypeEnum betType, String selection) {
        double payout = bet_amount;
        int priceInt = 0;
        double price = 0.00F;
        switch (betType) {
            case HEAD_TO_HEAD:
                List<Map<String, Object>> moneylines = odds.getMoneylines();
                for (Map<String, Object> moneyline : moneylines) {
                    if (moneyline.get("name").equals(selection)) {
                        priceInt = (int) moneyline.get("price");
                        price = (double) priceInt;
                    }
                }
                break;
            case SPREAD:
                List<Map<String, Object>> spreads = odds.getSpreads();
                for (Map<String, Object> spread : spreads) {
                    if (spread.get("name").equals(selection)) {
                        priceInt = (int) spread.get("price");
                        price = (double) priceInt;                        
                    }
                }
                break;
            case OVER_UNDER:
                List<Map<String, Object>> overunders = odds.getOver_under();
                for (Map<String, Object> overunder : overunders) {
                    if (overunder.get("name").equals(selection)) {
                        priceInt = (int) overunder.get("price");
                        price = (double) priceInt;                        
                    }
                }
                break;
            default:
                LOGGER.error("Unable to calculate payout for bet with odds " + odds.toString());
                break;
        }
        if (price >= 0){
        payout = ((price/100) * bet_amount) + bet_amount;
        }
        else{
        payout = ((100/Math.abs(price)) * bet_amount) + bet_amount;
        }
        
    return Math.round(payout *100.0) / 100.0;
    }
    
    /**
     * 
     * @param userName
     * @param amount
     * @throws Exception 
     */
    public void deductBetAmountFromBankroll(String userName, double amount) throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Bankroll bankroll = getBankroll(userName);
        double newBalance = bankroll.getBalance() - amount;
        newBalance = Math.round(newBalance * 100.0) / 100.0;
        if (newBalance < 0.00){
        throw new Exception("Cannot place bet, insufficient funds in bankroll");
        }
        Update update = new Update();
        update.set("balance", newBalance);
        mongoTemplate.findAndModify(query, update, Bankroll.class);
    }

    /**
     * 
     * @param userName
     * @return 
     */
    public Bankroll getBankroll(String userName) {
        List<Bankroll> bankrollList = new ArrayList();
        Bankroll bankroll = new Bankroll();
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        bankrollList = mongoTemplate.find(query, Bankroll.class, "bankrolls");
        try {
            bankroll = bankrollList.get(0);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Cannot retrieve the bankroll for this user, the userName provided (" + userName + ") does not exist in the system ");
        }
        return bankroll;
    }

    /**
     * 
     * @param userName
     * @return 
     */
    public User getUser(@RequestParam String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        List<User> userList = mongoTemplate.find(query, User.class, "users");
        if (userList.isEmpty()) {
            return null;
        } else {
            return userList.get(0);
        }
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
    @ApiIgnore
    public List<Bet> listAllBets() {
        List<Bet> bets = mongoTemplate.findAll(Bet.class);
        return bets;
    }    
}
