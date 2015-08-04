package de.leetgeeks.jgl.leapx.backend.kirkwoodservice;

import de.leetgeeks.jgl.leapx.backend.kirkwoodservice.model.Score;
import retrofit.http.GET;

import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 02.08.2015
 * Time: 12:02
 */
public interface KirkwoodScoreService {
    @GET("/scores")
    List<Score> listScores();
}
