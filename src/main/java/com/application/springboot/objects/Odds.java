package com.application.springboot.objects;


import java.util.List;
import java.util.Map;
/**
 *
 * @author "paul.perez"
 */
public class Odds {
    List<Map<String, Object>> moneylines;
        List<Map<String, Object>> spreads;
            List<Map<String, Object>> over_under;

    public List<Map<String, Object>> getMoneylines() {
        return moneylines;
    }

    public void setMoneylines(List<Map<String, Object>> moneylines) {
        this.moneylines = moneylines;
    }

    public List<Map<String, Object>> getSpreads() {
        return spreads;
    }

    public void setSpreads(List<Map<String, Object>> spreads) {
        this.spreads = spreads;
    }

    public List<Map<String, Object>> getOver_under() {
        return over_under;
    }

    public void setOver_under(List<Map<String, Object>> over_under) {
        this.over_under = over_under;
    }
    @Override
    public String toString() {
        return "Odds{"
                + ", moneylines = " + moneylines 
                + ", spreads = " + spreads + " "
                + ", over_under = " + over_under + " "
           
                + '}';
    }

}
