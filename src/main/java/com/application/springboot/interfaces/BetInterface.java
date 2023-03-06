package com.application.springboot.interfaces;

import com.application.springboot.objects.Bet;
import com.application.springboot.system.BetTypeEnum;
import com.application.springboot.system.SportsEnum;
import java.util.List;

/**
 *
 * @author paul.perez
 */
public interface BetInterface {
   
    public Bet placeBet (String username, String eventId, String selection, double bet_amount, BetTypeEnum bet_type, SportsEnum sport);
    
    public List<Bet> getAllBetHistory (String username);
    
    public List<Bet> getWonBetHistory (String username);
    
    public List<Bet> getLostBetHistory (String username);
    
    public List<Bet> getInProgressBets (String username);
    
}
