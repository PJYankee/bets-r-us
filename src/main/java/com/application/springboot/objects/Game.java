/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.application.springboot.objects;

import org.springframework.data.annotation.Id;

/**
 *
 * @author "paul.perez"
 */
public class Game {
    @Id
    private String id;
    private String startTime;
    private String home_team;
    private String away_team;
    private Odds odds;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }

    public Odds getOdds() {
        return odds;
    }

    public void setOdds(Odds odds) {
        this.odds = odds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    

    @Override
    public String toString() {
        return "Bankroll{"
                + ", id = " + id + " "
                + ", home_team = " + home_team + " "
                + ", away_team = " + away_team + " "
                + ", odds = " + odds + " "
                + '}';
    }  
}
