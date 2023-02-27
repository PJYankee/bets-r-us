package com.application.springboot.controllers;

import com.application.springboot.system.SportsEnum;
import com.application.springboot.system.OddsApiHandler;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.application.springboot.interfaces.ScoresAndScheduleInterface;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class ScoresAndScheduleController implements ScoresAndScheduleInterface{

    private static final Logger LOGGER = LogManager.getLogger(ScoresAndScheduleController.class);     
    OddsApiHandler handler;
    
    @Autowired
    public void setRequestHandler(OddsApiHandler requestHandler) {
        this.handler = requestHandler;
    }
    
    URL requestUrl;

    @Override
    @GetMapping("/scores/getLiveScores")
    @ResponseBody
    public List<Object> getLiveScores(SportsEnum sport) {
       List<Object> responseList= new ArrayList();
       List<Object> liveScoreList= new ArrayList();       
       
        try {

            String op = getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeUrl(requestUrl).toString();
            
            responseList = handler.parseResponseList(jsonResponseString);
            
            for (Object game : responseList){
                LinkedHashMap<String, String> gameMap = new LinkedHashMap();
                gameMap = (LinkedHashMap) game;
                
                if (gameMap.containsKey("commence_time")){}
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
          
        return responseList;
    }

    @Override
    @GetMapping("/scores/getFinalScores")
    @ResponseBody
    public List<Object> getFinalScores(SportsEnum sport) {
       List<Object> responseList= new ArrayList();
       List<Object> finalScores= new ArrayList();  
         try {

            String op = getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);
            headers.put("daysFrom", "1");

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeUrl(requestUrl).toString();
            
            responseList = handler.parseResponseList(jsonResponseString);
            
            for (Object game : responseList){
                LinkedHashMap<String, Object> gameMap = (LinkedHashMap) game;

                if (gameMap.containsKey("completed")){
                    Boolean isCompleted = (Boolean) gameMap.get("completed");
                    if (Objects.equals(isCompleted, Boolean.TRUE)){
                    finalScores.add(game);
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
          
        return finalScores;
    }

    @Override
    @GetMapping("/scores/getScheduledGames")
    @ResponseBody
    public List<Object> getUpcomingGames(SportsEnum sport) {
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'Z'");
       List<Object> responseList= new ArrayList();
       List<Object> upcomingGames= new ArrayList();  
         try {

            String op = getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeUrl(requestUrl).toString();
            
            responseList = handler.parseResponseList(jsonResponseString);
            
            for (Object game : responseList){
                LinkedHashMap<String, Object> gameMap = new LinkedHashMap();
                gameMap = (LinkedHashMap) game;
                
                if (gameMap.containsKey("commence_time")){
                    Date gameStart = sdf.parse(gameMap.get("commence_time").toString());
                    Date now = new Date();
                    
                        if (now.before(gameStart)){
                        upcomingGames.add(game);
                        }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
          
        return upcomingGames;
    }

    private String getSportKey(SportsEnum sport) {
        
        String sportKey = "";
        switch (sport) {
            case FOOTBALL:
                sportKey = "americanfootball_nfl";
                break;
            case BASKETBALL:
                sportKey = "basketball_nba";
                break;
            case BASEBALL:
                sportKey = "baseball_mlb";
                break;
            case HOCKEY:
                sportKey = "icehockey_nhl";
                break;
        }
        return sportKey;
    }

}
