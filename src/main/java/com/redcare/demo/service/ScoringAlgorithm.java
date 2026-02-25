package com.redcare.demo.service;

import com.redcare.demo.model.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ScoringAlgorithm {

    public static int calculateScore(Repository repository) {
        long daysSinceLastPush =
                ChronoUnit.DAYS.between(Instant.parse(repository.getPushedAt()), Instant.now());
        double popularity = Math.log1p(repository.getStargazersCount()) + 0.8 * Math.log1p(repository.getForksCount());
        double freshness = Math.pow(2.0, -daysSinceLastPush / 365.0);
        return (int) Math.round(popularity * freshness);
    }
}
