package com.redcare.demo.service;

import com.redcare.demo.model.Repository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class ScoringAlgorithmTest {

    private static final Instant NOW = Instant.parse("2026-02-25T00:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void returnsZeroWhenStarsAndForksAreZero() {
        Repository repo = Repository.builder()
                .stargazersCount(0)
                .forksCount(0)
                .pushedAt(NOW.toString())
                .build();

        assertEquals(0, ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK));
    }

    @Test
    void calculatesExactScoreWhenFreshnessIsOne() {
        // daysSinceLastPush = 0 => freshness = 1
        Repository repo = Repository.builder()
                .stargazersCount(1000)
                .forksCount(100)
                .pushedAt(NOW.toString())
                .build();

        double popularity = Math.log1p(1000) + 0.8 * Math.log1p(100);
        int expected = (int) Math.round(popularity);

        assertEquals(expected, ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK));
    }

    @Test
    void halvesScoreAfterExactlyOneYear() {
        // daysSinceLastPush = 365 => freshness = 2^(-1) = 0.5
        Instant pushedAt = NOW.minusSeconds(365L * 24 * 60 * 60);

        Repository repo = Repository.builder()
                .stargazersCount(1000)
                .forksCount(100)
                .pushedAt(pushedAt.toString())
                .build();

        double popularity = Math.log1p(1000) + 0.8 * Math.log1p(100);
        int expected = (int) Math.round(popularity * 0.5);

        assertEquals(expected, ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK));
    }

    @Test
    void throwsIfPushedAtIsNotIsoInstant() {
        Repository repo = Repository.builder()
                .stargazersCount(10)
                .forksCount(2)
                .pushedAt("not-an-instant")
                .build();

        assertThrows(RuntimeException.class, () -> ScoringAlgorithm.calculateScore(repo, FIXED_CLOCK));
    }
}
