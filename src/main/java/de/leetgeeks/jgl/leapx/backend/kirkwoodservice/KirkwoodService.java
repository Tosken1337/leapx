package de.leetgeeks.jgl.leapx.backend.kirkwoodservice;

import de.leetgeeks.jgl.leapx.backend.ScoreService;
import de.leetgeeks.jgl.leapx.backend.kirkwoodservice.model.Score;
import retrofit.RestAdapter;

import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 02.08.2015
 * Time: 12:01
 */
public class KirkwoodService implements ScoreService {
    private KirkwoodScoreService scoreService;

    public void init() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com")
                .build();

        scoreService = restAdapter.create(KirkwoodScoreService.class);
    }

    public List<Score> getHighscores() {
        final List<Score> scores = scoreService.listScores();
        return scores;
    }
}
