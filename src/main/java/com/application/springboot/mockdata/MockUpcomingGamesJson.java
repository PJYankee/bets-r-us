/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.application.springboot.mockdata;

/**
 *
 * @author "paul.perez"
 */
public class MockUpcomingGamesJson {
    
    String mockUpcomingJson = "[\n" +
"  {\n" +
"    \"id\": \"1349c6fefff297a9e31e13dd732dd97b\",\n" +
"    \"sport_key\": \"icehockey_nhl\",\n" +
"    \"sport_title\": \"NHL\",\n" +
"    \"commence_time\": \"2023-03-04T23:59:00Z\",\n" +
"    \"completed\": false,\n" +
"    \"home_team\": \"Boston Bruins\",\n" +
"    \"away_team\": \"New York Rangers\",\n" +
"    \"scores\": null,\n" +
"    \"last_update\": null\n" +
"  }\n" +
"]";
    
    
    public String getMockUpcomingJson() {
        return mockUpcomingJson;
    }
}
