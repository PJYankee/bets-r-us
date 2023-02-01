package com.application.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author "paul.perez"
 */
@Component
public class FunctionExecutor {
    public FunctionExecutor(){
    
    }
    private static final Logger LOGGER = LogManager.getLogger(FunctionExecutor.class);
    private static String hostname;
    private static String port;
    @Value("${application.hostname}")
    public void setHostname(String value) {
        this.hostname = value;
    } 
    @Value("${application.port}")
    public void setPort(String value) {
        this.port = value;
    } 
    
    
    public URL createUrl(Map<String, String> headers) throws MalformedURLException, Exception {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://");
        urlString.append(hostname);
        urlString.append(":");
        urlString.append(port);
        urlString.append("/");
        if (headers.containsKey("operation")) {
            urlString.append(headers.get("operation") + "?");
        }
        else{
        throw new Exception ("The requested function contains no operation");
        }
        for (String key : headers.keySet()) {
            if (!key.equals("operation")) {
                urlString.append(key + "=" + headers.get(key) + "&");
            }
        }
            urlString.substring(0, urlString.lastIndexOf("&"));
            String requestUrlString = urlString.toString();
            requestUrlString = requestUrlString.replaceAll(" ", "+");
            URL url = new URL(requestUrlString);
       
        return url;
    }
    
    public StringBuffer executeUrl(URL url){
        StringBuffer content = new StringBuffer();
        try{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        }catch(IOException x){
        LOGGER.error("Error executing call to external function ", x);
        }

        return content;
    }
}
