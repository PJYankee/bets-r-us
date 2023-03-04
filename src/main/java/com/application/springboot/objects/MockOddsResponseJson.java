/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.application.springboot.objects;

/**
 *
 * @author "paul.perez"
 */
public class MockOddsResponseJson {
    
    String mockResponseJson = "  {\n" +
"  \"id\": \"54c416e4d51794304103b36b1c61403f\",\n" +
"  \"sport_key\": \"icehockey_nhl\",\n" +
"  \"sport_title\": \"NHL\",\n" +
"  \"commence_time\": \"2023-03-04T02:30:00Z\",\n" +
"  \"home_team\": \"Arizona Coyotes\",\n" +
"  \"away_team\": \"Carolina Hurricanes\",\n" +
"  \"bookmakers\": [\n" +
"    {\n" +
"      \"key\": \"draftkings\",\n" +
"      \"title\": \"DraftKings\",\n" +
"      \"markets\": [\n" +
"        {\n" +
"          \"key\": \"h2h\",\n" +
"          \"last_update\": \"2023-03-03T14:04:38Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Arizona Coyotes\",\n" +
"              \"price\": 295\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"Carolina Hurricanes\",\n" +
"              \"price\": -360\n" +
"            }\n" +
"          ]\n" +
"        },\n" +
"        {\n" +
"          \"key\": \"spreads\",\n" +
"          \"last_update\": \"2023-03-03T14:04:38Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Arizona Coyotes\",\n" +
"              \"price\": 115,\n" +
"              \"point\": 1.5\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"Carolina Hurricanes\",\n" +
"              \"price\": -135,\n" +
"              \"point\": -1.5\n" +
"            }\n" +
"          ]\n" +
"        },\n" +
"        {\n" +
"          \"key\": \"totals\",\n" +
"          \"last_update\": \"2023-03-03T14:04:38Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Over\",\n" +
"              \"price\": -110,\n" +
"              \"point\": 6\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"Under\",\n" +
"              \"price\": -110,\n" +
"              \"point\": 6\n" +
"            }\n" +
"          ]\n" +
"        }\n" +
"      ]\n" +
"    }\n" +
"  ]\n" +
"}";

    public String getMockResponseJson() {
        return mockResponseJson;
    }
}
