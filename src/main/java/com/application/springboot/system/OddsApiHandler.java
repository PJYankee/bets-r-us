package com.application.springboot.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

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
            urlStringBuilder = urlStringBuilder.append(headers.get("operation")).append("?").append("apiKey=").append(apikey);
        } else {
            throw new Exception("The requested url contains no operation");
        }
        for (String key : headers.keySet()) {
            if (!key.equals("operation")) {
                urlStringBuilder = urlStringBuilder.append(key).append("=").append(headers.get(key)).append("&");
            }
        }
        String requestUrlString = urlStringBuilder.toString();

        if (requestUrlString.contains("&")) {
            urlStringBuilder.substring(0, urlStringBuilder.lastIndexOf("&"));
        }
        requestUrlString = requestUrlString.replaceAll(" ", "+");
        URL url = new URL(requestUrlString);

        return url;
    }

    public StringBuffer executeUrl(URL url) {
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
            LOGGER.error("Error executing call to external function ", x);
        }

        return content;
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
