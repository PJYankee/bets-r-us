package com.application.springboot.interfaces;

import com.application.springboot.system.SportsEnum;
import java.util.List;
import java.util.Map;

/**
 *
 * @author paul.perez
 */
public interface ScoresInterface {

    public List<Object> getLiveScores(SportsEnum sport);

    public List<Object> getFinalScores(SportsEnum sport);

}
