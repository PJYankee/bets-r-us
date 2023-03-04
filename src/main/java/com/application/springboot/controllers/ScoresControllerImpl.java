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
import java.util.TimeZone;
import com.application.springboot.interfaces.ScoresInterface;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class ScoresControllerImpl implements ScoresInterface{

    private static final Logger LOGGER = LogManager.getLogger(ScoresControllerImpl.class);     
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
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
       sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
       List<Object> responseList= new ArrayList();
       List<Object> liveScoreList= new ArrayList();       
       
        try {

            String op = handler.getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeGetUrl(requestUrl).toString();
            
            responseList = handler.parseResponseList(jsonResponseString);
            
            for (Object game : responseList){
                LinkedHashMap<String, Object> gameMap = (LinkedHashMap) game;
                
                if (gameMap.containsKey("commence_time") && gameMap.containsKey("completed")){
                    String startTime = (String) gameMap.get("commence_time");
                    Date gameStart = sdf.parse(startTime);
                    Date now = new Date();
                    Boolean isCompleted = (Boolean) gameMap.get("completed");
                    
                        if (gameStart.before(now) && !isCompleted ){
                        liveScoreList.add(game);
                        }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
          
        return liveScoreList;
    }

    @Override
    @GetMapping("/scores/getFinalScores")
    @ResponseBody
    public List<Object> getFinalScores(SportsEnum sport) {
       List<Object> responseList= new ArrayList();
       List<Object> finalScores= new ArrayList();  
         try {

            String op = handler.getSportKey(sport) + "/scores";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);
            headers.put("daysFrom", "1");

            requestUrl = handler.urlBuilder(headers);
            String jsonResponseString = handler.executeGetUrl(requestUrl).toString();
            
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

}
