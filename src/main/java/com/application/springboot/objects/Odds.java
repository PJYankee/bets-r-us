package com.application.springboot.objects;


import java.util.List;
/**
 *
 * @author "paul.perez"
 */
public class Odds {
   Integer home_moneyline;
   Integer away_moneyline;
   List<Float> homeSpreads;
   List<Float> awaySpreads;
   List<Float> overTotals;
   List<Float> underTotals;

    public Integer getHome_moneyline() {
        return home_moneyline;
    }

    public void setHome_moneyline(Integer home_moneyline) {
        this.home_moneyline = home_moneyline;
    }

    public Integer getAway_moneyline() {
        return away_moneyline;
    }

    public void setAway_moneyline(Integer away_moneyline) {
        this.away_moneyline = away_moneyline;
    }

    public List<Float> getHomeSpreads() {
        return homeSpreads;
    }

    public void setHomeSpreads(List<Float> homeSpreads) {
        this.homeSpreads = homeSpreads;
    }

    public List<Float> getAwaySpreads() {
        return awaySpreads;
    }

    public void setAwaySpreads(List<Float> awaySpreads) {
        this.awaySpreads = awaySpreads;
    }

    public List<Float> getOverTotals() {
        return overTotals;
    }

    public void setOverTotals(List<Float> overTotals) {
        this.overTotals = overTotals;
    }

    public List<Float> getUnderTotals() {
        return underTotals;
    }

    public void setUnderTotals(List<Float> underTotals) {
        this.underTotals = underTotals;
    }

    @Override
    public String toString() {
        return "Odds{"
                + ", home_moneyline = " + home_moneyline 
                + ", away_moneyline = " + away_moneyline + " "
                + ", homeSpreads = " + homeSpreads + " "
                + ", awaySpreads = " + awaySpreads + " " 
                + ", overTotals = " + overTotals + " "    
                + ", underTotals = " + underTotals + " "                
                + '}';
    }

}
