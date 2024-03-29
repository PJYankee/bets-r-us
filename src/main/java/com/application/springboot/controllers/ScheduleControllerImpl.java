package com.application.springboot.controllers;

import com.application.springboot.interfaces.ScheduleInterface;
import com.application.springboot.objects.Game;
import com.application.springboot.mockdata.MockUpcomingGamesJson;
import com.application.springboot.objects.Odds;
import com.application.springboot.system.OddsApiHandler;
import com.application.springboot.system.SportsEnum;
import io.swagger.annotations.ApiOperation;
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
public class ScheduleControllerImpl implements ScheduleInterface{
    
    private static final Logger LOGGER = LogManager.getLogger(ScheduleControllerImpl.class);     
    OddsApiHandler handler;
    URL requestGamesUrl;
  
    @Autowired
    public void setOddsApiHandler(OddsApiHandler oddsApiHandler) {
        this.handler = oddsApiHandler;
    }    
   
    /**
     * 
     * @param sport
     * @return 
     */
    @Override
    @GetMapping("/schedule/getAllUpcomingGames")
    @ResponseBody
    @ApiOperation(value = "Allows the user to view all upcoming games and the associated odds", notes = "Returns a list of Game objects")    
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

                    //TODO reconnect for live API calls       if (now.before(gameStart)) {
                    if (true)  {    
                        newGame.setStartTime(startTime);
                        newGame.setId((String) gameMap.get("id"));
                        newGame.setHome_team((String) gameMap.get("home_team"));
                        newGame.setAway_team((String) gameMap.get("away_team"));
                        
                        Odds odds = handler.getOdds((String) gameMap.get("id"), sport);
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

}
