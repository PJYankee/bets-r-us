package com.application.springboot.system;

import com.application.springboot.mockdata.MockOddsResponseJson;
import com.application.springboot.objects.Odds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

/**
 *
 * @author "paul.perez"
 */
public class OddsApiHandler {

    private static final Logger LOGGER = LogManager.getLogger(OddsApiHandler.class);

    public String apikey;
    public String baseUrl;

    public URL urlBuilder(Map<String, String> headers) throws MalformedURLException, Exception {
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append(baseUrl);

        if (headers.containsKey("operation")) {
            urlStringBuilder = urlStringBuilder.append(headers.get("operation")).append("?").append("apiKey=").append(apikey).append("&");
        } else {
            throw new Exception("The requested url contains no operation");
        }
        for (String key : headers.keySet()) {
            if (!key.equals("operation")) {
                urlStringBuilder = urlStringBuilder.append(key).append("=").append(headers.get(key)).append("&");
            }
        }
        String requestUrlString = urlStringBuilder.toString();
        requestUrlString = requestUrlString.substring(0, requestUrlString.lastIndexOf("&"));
        requestUrlString = requestUrlString.replaceAll(" ", "+");
        URL url = new URL(requestUrlString);

        return url;
    }

    public StringBuffer executeGetUrl(URL url) {
        StringBuffer content = new StringBuffer();
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
        } catch (IOException x) {
            LOGGER.error("Error executing call to external api ", x);
        }

        return content;
    }
    
        public StringBuffer executePostUrl(URL url) {
        StringBuffer content = new StringBuffer();
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
        } catch (IOException x) {
            LOGGER.error("Error executing call to external api ", x);
        }

        return content;
    }
        
    public String getSportKey(SportsEnum sport) {
        if (sport == null){
            throw new NullPointerException("null pointer exception, sport cannot have null value");
        }
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
    
 public Odds getOdds(String id, SportsEnum sport) {
        Map<String, Object> responseMap = new HashMap();
        List<Map<String, Object>> outcomes  = new ArrayList();
        URL requestOddsUrl;          
        String jsonResponseString = "";
        Odds odds = new Odds();
        try {
            MockOddsResponseJson mockJson = new MockOddsResponseJson();
            String op = getSportKey(sport) + "/events/" + id + "/odds";
            Map<String, String> headers = new HashMap();
            headers.put("operation", op);
            headers.put("regions", "us");
            headers.put("markets", "h2h%2Cspreads%2Ctotals");
            headers.put("dateFormat", "iso");
            headers.put("oddsFormat", "american");
            headers.put("bookmakers", "draftkings");

            requestOddsUrl = urlBuilder(headers);
            // TODO reconnect API for live odds  --->  String jsonResponseString = handler.executeGetUrl(requestOddsUrl).toString();
            if (id.equals("aeefbac96aa33824e9cc1478ae3dd33c")) {               //remove after reconnecting live odds
                jsonResponseString = mockJson.getMockResponse1Json();     //remove after reconnecting live odds
            }                                                              //remove after reconnecting live odds
            if (id.equals("4a6ea91bac0ce0ad0f5088055b7d63b4")) {           //remove after reconnecting live odds
                jsonResponseString = mockJson.getMockResponse2Json();     //remove after reconnecting live odds
            }                                                              //remove after reconnecting live odds
            if (id.equals("d01e947e77dac6c0546d87f60de60c34")) {           //remove after reconnecting live odds
                jsonResponseString = mockJson.getMockResponse3Json();     //remove after reconnecting live odds
            }                                                              //remove after reconnecting live odds
                                                                          //remove after reconnecting live odds
            responseMap = parseResponseMap(jsonResponseString);     //remove after reconnecting live odds

            if (responseMap.containsKey("bookmakers")) {
                List<Object> responseObjects = (List<Object>) responseMap.get("bookmakers");
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
            }
            
        } catch (Exception ex) {
           LOGGER.error("Error retrieving odds for contest " + id, ex);
        }
        return odds;
    }    

    public Map<String, Object> parseResponseMap(String json) {

        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map< String, Object> responseMap = springParser.parseMap(json);

        return responseMap;
    }

    public List<Object> parseResponseList(String json) {
        JsonParser springParser = JsonParserFactory.getJsonParser();
        List<Object> responseMap = springParser.parseList(json);

        return responseMap;
    }
}
