package com.application.springboot.controllers;

import com.application.springboot.system.SportsEnum;
import com.application.springboot.interfaces.LiveScoreInterface;
import com.application.springboot.system.OddsApiHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class LiveScoreControllerImpl implements LiveScoreInterface{

    private static final Logger LOGGER = LogManager.getLogger(LiveScoreControllerImpl.class);     
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
        try {

            String op = getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeUrl(requestUrl).toString();
            
            responseList = handler.parseResponseList(jsonResponseString);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
          
        return responseList;
    }

    @Override
    @GetMapping("/scores/getFinalScores")
    @ResponseBody
    public Map<String, Object> getFinalScores(SportsEnum sport) {

        Map<String, Object> response = new HashMap();
        
        String op = getSportKey(sport) + "/scores";
        Map<String, String> headers = new HashMap();
        headers.put("operation", op);
            
        return response;
    }

    @Override
    @GetMapping("/scores/getScheduledGames")
    @ResponseBody
    public Map<String, Object> getScheduledGames(SportsEnum sport) {

        Map<String, Object> response = new HashMap();
        
        String op = getSportKey(sport) + "/scores";
        Map<String, String> headers = new HashMap();
        headers.put("operation", op);
            
        return response;
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
