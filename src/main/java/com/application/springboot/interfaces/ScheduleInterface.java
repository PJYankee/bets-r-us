package com.application.springboot.interfaces;

import com.application.springboot.objects.Game;
import com.application.springboot.system.SportsEnum;
import java.util.List;
/**
 *
 * @author "paul.perez"
 */
public interface ScheduleInterface {
    
    public List<Game> getAllUpcomingGames(SportsEnum sport);
}
