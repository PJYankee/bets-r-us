package com.application.springboot;

import java.util.List;
import java.util.Map;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

/**
 *
 * @author "paul.perez"
 */
public class ResponseParser {

    ResponseParser() {

    }

    public Map<String, Object> parseResponseMap(String json) {

        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map< String, Object> responseMap = springParser.parseMap(json);

        return responseMap;
    }
    
    public List<Object> parseResponseList(String json){
       JsonParser springParser = JsonParserFactory.getJsonParser();
       List<Object> responseMap = springParser.parseList(json);

       return responseMap;
    }
}
