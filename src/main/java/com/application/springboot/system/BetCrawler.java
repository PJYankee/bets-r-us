package com.application.springboot.system;

import com.application.springboot.mockdata.MockFinalScoresJson;
import com.application.springboot.mockdata.MockInProgressScoresResponseJson;
import com.application.springboot.objects.Bankroll;
import com.application.springboot.objects.Bet;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    OddsApiHandler handler;
    URL requestUrl;

    @Autowired
    public void setRequestHandler(OddsApiHandler requestHandler) {
        this.handler = requestHandler;
    }

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(cron = "0 */2 * * * *")
    @Async
    public void reconcileBets() {
        LOGGER.debug("Running the interval scan of active bets ");
        List<Bet> allOpenBets = listAllOpenBets();
        List<Object> liveScores = new ArrayList();
        List<Object> finalScores = getFinalScores(SportsEnum.HOCKEY);

        for (Bet bet : allOpenBets) {
            BetStatusEnum status = bet.getStatus();
            String betId = bet.getEventId();
            switch (status) {
                case PLACED:
                    liveScores = getLiveScores(bet.getSport());
                    for (Object liveScore : liveScores) {
                        Map<String, Object> liveScoreMap = (Map<String, Object>) liveScore;

                        if (liveScoreMap.containsKey("id")) {
                            String id = (String) liveScoreMap.get("id");
                            if (bet.getEventId().equals(id)) {
                                LOGGER.info("Event " + bet.getEventId() + " has started, changing status from PLACED to IN_PROGRESS");
                                setNewBetStatus(BetStatusEnum.IN_PROGRESS, bet);
                            }

                        }
                    }
                    break;
                case IN_PROGRESS:
                    for (Object finalScore : finalScores) {
                        Map<String, Object> finalScoreMap = (Map<String, Object>) finalScore;
                        if (finalScoreMap.containsKey("id")) {
                            String id = (String) finalScoreMap.get("id");
                            if (bet.getEventId().equals(id)) {
                                LOGGER.info("Event " + bet.getEventId() + " has ended, determine bet result and set status to WIN, LOSS, or PUSH");
                                BetStatusEnum newStatus = getBetOutcome(finalScoreMap, bet);
                                setNewBetStatus(newStatus, bet);
                                if(newStatus.equals(BetStatusEnum.WIN ) || newStatus.equals(BetStatusEnum.PUSH )){
                                updateBankroll(newStatus, bet);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private List<Bet> listAllOpenBets() {
        List<Bet> bets = mongoTemplate.findAll(Bet.class);
        List<Bet> openBets = new ArrayList();
        for (Bet bet : bets) {
            if (bet.getStatus() == BetStatusEnum.IN_PROGRESS || bet.getStatus() == BetStatusEnum.PLACED) {
                openBets.add(bet);
            }
        }
        return openBets;
    }

    private List<Object> getLiveScores(SportsEnum sport) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        List<Object> responseList = new ArrayList();
        List<Object> liveScoreList = new ArrayList();
        String jsonResponseString = "";

        try {
            MockInProgressScoresResponseJson mockJson = new MockInProgressScoresResponseJson();
            String op = handler.getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestUrl = handler.urlBuilder(headers);
            // TODO reconnect to API to enable live scoring String jsonResponseString = handler.executeGetUrl(requestUrl).toString();
            jsonResponseString = mockJson.getMockInProgressJson();
            responseList = handler.parseResponseList(jsonResponseString);

            for (Object game : responseList) {
                LinkedHashMap<String, Object> gameMap = (LinkedHashMap) game;
                if (gameMap.containsKey("commence_time") && gameMap.containsKey("completed")) {
                    String startTime = (String) gameMap.get("commence_time");
                    Date gameStart = sdf.parse(startTime);
                    Date now = new Date();
                    Boolean isCompleted = (Boolean) gameMap.get("completed");

                    if (gameStart.before(now) && !isCompleted) {
                        liveScoreList.add(game);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return liveScoreList;
    }

    private List<Object> getFinalScores(SportsEnum sport) {
        List<Object> responseList = new ArrayList();
        List<Object> finalScores = new ArrayList();
        String jsonResponseString = "";
        try {
            MockFinalScoresJson mockJson = new MockFinalScoresJson();
            jsonResponseString = mockJson.getFinalScoresJson();
            String op = handler.getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);
            headers.put("daysFrom", "1");

            requestUrl = handler.urlBuilder(headers);
            //TODO reconnect to odds-api for live scoring    String jsonResponseString = handler.executeGetUrl(requestUrl).toString();
            responseList = handler.parseResponseList(jsonResponseString);

            for (Object game : responseList) {
                LinkedHashMap<String, Object> gameMap = (LinkedHashMap) game;

                if (gameMap.containsKey("completed")) {
                    Boolean isCompleted = (Boolean) gameMap.get("completed");
                    if (Objects.equals(isCompleted, Boolean.TRUE)) {
                        finalScores.add(game);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return finalScores;
    }

    private BetStatusEnum getBetOutcome(Map<String, Object> finalScoreMap, Bet bet) {
        String homeTeam = (String) finalScoreMap.get("home_team");
        String awayTeam = (String) finalScoreMap.get("away_team");
        double pointSpread = 0;
        double selectionScore = 0;
        double opposingScore = 0;
        BetStatusEnum newStatus = BetStatusEnum.IN_PROGRESS;
        String betSelection = bet.getSelection();
        String homeTeamScoreStr = "";
        String awayTeamScoreStr = "";
        List<Object> finalScores = (List<Object>) finalScoreMap.get("scores");
        for (Object scoreObj : finalScores) {
            Map<String, String> score = (Map<String, String>) scoreObj;
            if (score.get("name").equals(finalScoreMap.get("home_team"))) {
                homeTeamScoreStr = score.get("score");
            } else if (score.get("name").equals(finalScoreMap.get("away_team"))) {
                awayTeamScoreStr = score.get("score");
            } else {
                LOGGER.error("Unable to get bet Outcome, set status to UNRESOLVED and manually review");
            }
        }
        double homeTeamScore = Double.parseDouble(homeTeamScoreStr);
        double awayTeamScore = Double.parseDouble(awayTeamScoreStr);

        switch (bet.getBet_type()) {
            case HEAD_TO_HEAD:
                List<Map<String, Object>> moneylines = bet.getOdds().getMoneylines();
                for (Map<String, Object> moneyline : moneylines) {
                    if (moneyline.get("name").equals(betSelection)) {
                        if (betSelection.equals(homeTeam)) {
                            selectionScore = homeTeamScore;
                            opposingScore = awayTeamScore;
                        }
                        if (betSelection.equals(awayTeam)) {
                            selectionScore = awayTeamScore;
                            opposingScore = homeTeamScore;
                        }
                    }
                }
                if (selectionScore > opposingScore) {
                    newStatus = BetStatusEnum.WIN;
                }
                if (selectionScore < opposingScore) {
                    newStatus = BetStatusEnum.LOSS;
                }
                if (selectionScore == opposingScore) {
                    newStatus = BetStatusEnum.PUSH;
                }
                break;
            case SPREAD:
                List<Map<String, Object>> spreads = bet.getOdds().getSpreads();
                for (Map<String, Object> spread : spreads) {
                    if (spread.get("name").equals(betSelection)) {
                        pointSpread = (double) spread.get("point");
                        if (betSelection.equals(homeTeam)) {
                            selectionScore = homeTeamScore;
                            opposingScore = awayTeamScore;
                        }
                        if (betSelection.equals(awayTeam)) {
                            selectionScore = awayTeamScore;
                            opposingScore = homeTeamScore;
                        }
                    }
                }
                if (selectionScore + pointSpread > opposingScore) {
                    newStatus = BetStatusEnum.WIN;
                }
                if (selectionScore + pointSpread < opposingScore) {
                    newStatus = BetStatusEnum.LOSS;
                }
                if (selectionScore + pointSpread == opposingScore) {
                    newStatus = BetStatusEnum.PUSH;
                }
                break;

            case OVER_UNDER:
                List<Map<String, Object>> overunders = bet.getOdds().getOver_under();
                for (Map<String, Object> overunder : overunders) {
                    if (overunder.get("name").equals(betSelection)) {
                        double totalPoints = homeTeamScore + awayTeamScore;
                        double betPoints = (double) overunder.get("point");
                        if (betSelection.equals("Over")) {
                            if (betPoints < totalPoints) {
                                newStatus = BetStatusEnum.WIN;
                            }
                            if (betPoints > totalPoints) {
                                newStatus = BetStatusEnum.LOSS;
                            }
                            if (betPoints == totalPoints) {
                                newStatus = BetStatusEnum.PUSH;
                            }
                        }

                        if (betSelection.equals("Under")) {
                            if (betPoints > totalPoints) {
                                newStatus = BetStatusEnum.WIN;
                            }
                            if (betPoints < totalPoints) {
                                newStatus = BetStatusEnum.LOSS;
                            }
                            if (betPoints == totalPoints) {
                                newStatus = BetStatusEnum.PUSH;
                            }
                        }
                    }
                }
                break;
            default:
                LOGGER.error("Unable to calculate payout for bet with odds " + bet.getOdds().toString());
        }
        return newStatus;
    }

    private void setNewBetStatus(BetStatusEnum newStatus, Bet bet) {
        Query query = new Query();
        query.addCriteria(Criteria.where("date").is(bet.getDate()));
        Update update = new Update();
        update.set("status", newStatus);
        mongoTemplate.findAndModify(query, update, Bet.class);
    }
    
    private void updateBankroll(BetStatusEnum newStatus, Bet bet){
    String username = bet.getUsername();
    Bankroll userBankroll = getBankroll(bet.getUsername());
    double oldBalance = userBankroll.getBalance();
    double depositAmount = 0;
    if(newStatus == BetStatusEnum.PUSH){
    depositAmount = bet.getBet_amount();
    }
    if(newStatus == BetStatusEnum.WIN){
    depositAmount = bet.getPayout();
    }
    depositAmount = oldBalance + depositAmount;
    Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(username));
        Update update = new Update();
        update.set("balance", depositAmount);
        mongoTemplate.findAndModify(query, update, Bankroll.class);
    }
    
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
}
