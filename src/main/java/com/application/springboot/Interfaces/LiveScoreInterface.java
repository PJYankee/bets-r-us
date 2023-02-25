package com.application.springboot.interfaces;

import com.application.springboot.system.SportsEnum;
import java.util.List;
import java.util.Map;

/**
 *
 * @author paul.perez
 */
public interface LiveScoreInterface {

    public List<Object> getLiveScores(SportsEnum sport);

    public Map<String, Object> getFinalScores(SportsEnum sport);
    
    public Map<String, Object> getScheduledGames(SportsEnum sport);

}
