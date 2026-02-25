package com.redcare.demo.service;

import com.redcare.demo.model.Repository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringAlgorithmTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void recentlyPushedHighStarsGivesHighScore() {
        Repository repo = Repository.builder()
                .pushedAt("2023-12-31T00:00:00Z") // 1 day ago
                .stargazersCount(10000)
                .forksCount(1000)
                .build();

        int score = ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK);

        assertThat(score).isGreaterThan(0);
    }

    @Test
    void veryOldPushDateReducesScore() {
        Repository newRepo = Repository.builder()
                .pushedAt("2023-12-31T00:00:00Z") // 1 day ago
                .stargazersCount(1000)
                .forksCount(100)
                .build();

        Repository oldRepo = Repository.builder()
                .pushedAt("2020-01-01T00:00:00Z") // ~4 years ago
                .stargazersCount(1000)
                .forksCount(100)
                .build();

        int newScore = ScoringAlgorithm.calculateScore(newRepo, FIXED_CLOCK);
        int oldScore = ScoringAlgorithm.calculateScore(oldRepo, FIXED_CLOCK);

        assertThat(newScore).isGreaterThan(oldScore);
    }

    @Test
    void zeroStarsAndForksGivesZeroScore() {
        Repository repo = Repository.builder()
                .pushedAt("2023-12-31T00:00:00Z")
                .stargazersCount(0)
                .forksCount(0)
                .build();

        int score = ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK);

        // log1p(0) = 0, so popularity = 0, score = 0
        assertThat(score).isZero();
    }

    @Test
    void moreStarsGivesHigherScore() {
        Repository lowStars = Repository.builder()
                .pushedAt("2023-12-01T00:00:00Z")
                .stargazersCount(10)
                .forksCount(0)
                .build();

        Repository highStars = Repository.builder()
                .pushedAt("2023-12-01T00:00:00Z")
                .stargazersCount(10000)
                .forksCount(0)
                .build();

        int lowScore = ScoringAlgorithm.calculateScore(lowStars, FIXED_CLOCK);
        int highScore = ScoringAlgorithm.calculateScore(highStars, FIXED_CLOCK);

        assertThat(highScore).isGreaterThan(lowScore);
    }

    @Test
    void forksPositivelyAffectScore() {
        Repository noForks = Repository.builder()
                .pushedAt("2023-12-01T00:00:00Z")
                .stargazersCount(100)
                .forksCount(0)
                .build();

        Repository withForks = Repository.builder()
                .pushedAt("2023-12-01T00:00:00Z")
                .stargazersCount(100)
                .forksCount(1000)
                .build();

        int noForksScore = ScoringAlgorithm.calculateScore(noForks, FIXED_CLOCK);
        int withForksScore = ScoringAlgorithm.calculateScore(withForks, FIXED_CLOCK);

        assertThat(withForksScore).isGreaterThan(noForksScore);
    }

    @Test
    void scoreIsNonNegative() {
        Repository repo = Repository.builder()
                .pushedAt("2000-01-01T00:00:00Z") // very old
                .stargazersCount(1)
                .forksCount(1)
                .build();

        int score = ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK);

        assertThat(score).isGreaterThanOrEqualTo(0);
    }
}