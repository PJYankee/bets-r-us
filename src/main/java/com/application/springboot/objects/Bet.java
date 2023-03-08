package com.application.springboot.objects;

import com.application.springboot.system.BetStatusEnum;
import com.application.springboot.system.BetTypeEnum;
import com.application.springboot.system.SportsEnum;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author "paul.perez"
 */
@Document(collection = "bets")
public class Bet {
    @Id
    @ApiModelProperty(required = true)
    private String date;   
    @ApiModelProperty(required = true)
    private String username;
    @ApiModelProperty(required = true)
    private String selection;
    @ApiModelProperty(required = true)
    private String eventId;
    @ApiModelProperty(required = true)  
    private double bet_amount;
    @ApiModelProperty(required = true)  
    private double payout;    
    @ApiModelProperty(required = true)    
    private BetStatusEnum status;
    @ApiModelProperty(required = true)    
    private BetTypeEnum bet_type;
    @ApiModelProperty(required = true)    
    private SportsEnum sport;
    @ApiModelProperty(required = true)    
    private Odds odds;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getBet_amount() {
        return bet_amount;
    }

    public void setBet_amount(double bet_amount) {
        this.bet_amount = bet_amount;
    }

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }
    
    public BetStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BetStatusEnum status) {
        this.status = status;
    }

    public BetTypeEnum getBet_type() {
        return bet_type;
    }

    public void setBet_type(BetTypeEnum bet_type) {
        this.bet_type = bet_type;
    }

    public SportsEnum getSport() {
        return sport;
    }

    public void setSport(SportsEnum sport) {
        this.sport = sport;
    }

    public Odds getOdds() {
        return odds;
    }

    public void setOdds(Odds odds) {
        this.odds = odds;
    }

    @Override
    public String toString() {
        return "Bet{"
                + "  userName = " + username + " "
                + "  date = " + date + " "                
                + ", eventId=" + eventId + " "
                + "  selection = " + selection + " "                
                + ", bet_amount=" + bet_amount + " "
                + ", payout=" + payout + " "                
                + ", status=" + status + " "
                + ", bet_type=" + bet_type + " "
                + ", sport=" + sport + " "                
                + "}";
    }     
}
