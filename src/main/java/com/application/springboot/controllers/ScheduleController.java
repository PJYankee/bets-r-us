package com.application.springboot.controllers;

import com.application.springboot.interfaces.ScheduleInterface;
import com.application.springboot.objects.Game;
import com.application.springboot.objects.MockOddsResponseJson;
import com.application.springboot.objects.MockUpcomingGamesJson;
import com.application.springboot.objects.Odds;
import com.application.springboot.system.OddsApiHandler;
import com.application.springboot.system.SportsEnum;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class ScheduleController implements ScheduleInterface{
    
    private static final Logger LOGGER = LogManager.getLogger(ScheduleController.class);     
    OddsApiHandler handler;
    URL requestGamesUrl;
    URL requestOddsUrl;  
    
    @Autowired
    public void setRequestHandler(OddsApiHandler requestHandler) {
        this.handler = requestHandler;
    }    
    
    @Override
    @GetMapping("/scores/getAllUpcomingGames")
    @ResponseBody
    public List<Game> getAllUpcomingGames(SportsEnum sport) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        List<Object> responseList = new ArrayList();
        List<Game> upcomingGames = new ArrayList();
        try {

            String op = handler.getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestGamesUrl = handler.urlBuilder(headers);
         //TODO reconnect API for live upcoming games   String jsonResponseString = handler.executeGetUrl(requestGamesUrl).toString();
            MockUpcomingGamesJson mockJson = new MockUpcomingGamesJson();
            String jsonResponseString = mockJson.getMockUpcomingJson();
            responseList = handler.parseResponseList(jsonResponseString);

            for (Object scheduledGame : responseList) {
                LinkedHashMap<String, Object> gameMap = (LinkedHashMap) scheduledGame;
                Game newGame = new Game();
                if (gameMap.containsKey("commence_time")) {
                    String startTime = (String) gameMap.get("commence_time");
                    Date gameStart = sdf.parse(startTime);
                    Date now = new Date();

                    if (now.before(gameStart)) {
                        
                        newGame.setStartTime(startTime);
                        newGame.setId((String) gameMap.get("id"));
                        newGame.setHome_team((String) gameMap.get("home_team"));
                        newGame.setAway_team((String) gameMap.get("away_team"));
                        
                        Odds odds = getOdds((String) gameMap.get("id"), sport);
                        newGame.setOdds(odds);
                        upcomingGames.add(newGame);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return upcomingGames;
    }

    private Odds getOdds(String id, SportsEnum sport) {
        Map<String, Object> responseMap = new HashMap();
        List<Map<String, Object>> outcomes  = new ArrayList();      

        Odds odds = new Odds();
        try {
            MockOddsResponseJson mockJson = new MockOddsResponseJson();
            String op = handler.getSportKey(sport) + "/events/" + id + "/odds";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);
            headers.put("regions", "us");
            headers.put("markets", "h2h%2Cspreads%2Ctotals");
            headers.put("dateFormat", "iso");
            headers.put("oddsFormat", "american");
            headers.put("bookmakers", "draftkings");

            requestOddsUrl = handler.urlBuilder(headers);
            // TODO reconnect API for live odds  --->  String jsonResponseString = handler.executeGetUrl(requestOddsUrl).toString();
            String jsonResponseString = mockJson.getMockResponseJson();
            responseMap = handler.parseResponseMap(jsonResponseString);

            if (responseMap.containsKey("bookmakers")) {
                List<Object> responseObjects = (List<Object>) responseMap.get("bookmakers");
                String homeTeam = (String) responseMap.get("home_team");
                String awayTeam = (String) responseMap.get("away_team");

                Map<String, Object> markets = (Map<String, Object>) responseObjects.get(0);
                List<Object> allBetOptions = (List<Object>) markets.get("markets");

                for (Object betOption : allBetOptions) {
                    Map<String, Object> option = (Map<String, Object>) betOption;

                    switch ((String) option.get("key")) {
                        case "h2h":
                            odds.setMoneylines((List<Map<String, Object>>) option.get("outcomes"));
                            break;
                        case "spreads":
                            odds.setSpreads((List<Map<String, Object>>) option.get("outcomes"));
                            break;
                        case "totals":
                            odds.setOver_under((List<Map<String, Object>>) option.get("outcomes"));
                            break;
                        default:
                            LOGGER.error("unable to get odds for contest");
                            break;

                    }

                }
                String noop = "noop";
            
            
            }
            
        } catch (Exception ex) {
           LOGGER.error("Error retrieving odds for contest " + id, ex);
        }
        return odds;
    }

}
