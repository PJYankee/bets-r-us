package com.application.springboot.objects;

/**
 *
 * @author "paul.perez"
 */
public class MockOddsResponseJson {
    
    String mockResponseJson = "{\n" +
"  \"id\": \"1349c6fefff297a9e31e13dd732dd97b\",\n" +
"  \"sport_key\": \"icehockey_nhl\",\n" +
"  \"sport_title\": \"NHL\",\n" +
"  \"commence_time\": \"2023-03-04T18:00:00Z\",\n" +
"  \"home_team\": \"Boston Bruins\",\n" +
"  \"away_team\": \"New York Rangers\",\n" +
"  \"bookmakers\": [\n" +
"    {\n" +
"      \"key\": \"draftkings\",\n" +
"      \"title\": \"DraftKings\",\n" +
"      \"markets\": [\n" +
"        {\n" +
"          \"key\": \"h2h\",\n" +
"          \"last_update\": \"2023-03-04T18:00:47Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Boston Bruins\",\n" +
"              \"price\": -170\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"New York Rangers\",\n" +
"              \"price\": 145\n" +
"            }\n" +
"          ]\n" +
"        },\n" +
"        {\n" +
"          \"key\": \"spreads\",\n" +
"          \"last_update\": \"2023-03-04T18:00:47Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Boston Bruins\",\n" +
"              \"price\": 145,\n" +
"              \"point\": -1.5\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"New York Rangers\",\n" +
"              \"price\": -170,\n" +
"              \"point\": 1.5\n" +
"            }\n" +
"          ]\n" +
"        },\n" +
"        {\n" +
"          \"key\": \"totals\",\n" +
"          \"last_update\": \"2023-03-04T18:00:47Z\",\n" +
"          \"outcomes\": [\n" +
"            {\n" +
"              \"name\": \"Over\",\n" +
"              \"price\": 100,\n" +
"              \"point\": 6\n" +
"            },\n" +
"            {\n" +
"              \"name\": \"Under\",\n" +
"              \"price\": -120,\n" +
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
