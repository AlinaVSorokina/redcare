package com.redcare.demo.service;

import com.redcare.demo.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    private static final Instant NOW = Instant.parse("2026-02-25T00:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private SearchProvider searchProvider;

    @Mock
    private CacheProvider cacheProvider;

    private ScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new ScoringService(searchProvider, cacheProvider, FIXED_CLOCK);
    }

    @Test
    void returnsFromCacheWhenCacheContainsKey() {
        String language = "Java";
        LocalDate date = LocalDate.of(2021, 1, 1);

        List<Repository> cached = List.of(
                Repository.builder().id(1).score(10).build(),
                Repository.builder().id(2).score(20).build()
        );

        when(cacheProvider.contains(language, date)).thenReturn(true);
        when(cacheProvider.get(language, date)).thenReturn(cached);

        List<Repository> result = scoringService.getScoringInfo(language, date);

        assertSame(cached, result);

        verify(cacheProvider).contains(language, date);
        verify(cacheProvider).get(language, date);
        verifyNoInteractions(searchProvider);
        verify(cacheProvider, never()).put(any(), any(), any());
    }

    @Test
    void fetchesFromProviderWhenCacheMiss() {
        String language = "Java";
        LocalDate date = LocalDate.of(2021, 1, 1);

        Repository repo1 = Repository.builder()
                .id(1)
                .stargazersCount(1000)
                .forksCount(100)
                .pushedAt(NOW.minus(30, ChronoUnit.DAYS).toString())
                .build();

        Repository repo2 = Repository.builder()
                .id(2)
                .stargazersCount(2000)
                .forksCount(200)
                .pushedAt(NOW.minus(60, ChronoUnit.DAYS).toString())
                .build();

        when(cacheProvider.contains(language, date)).thenReturn(false);
        when(searchProvider.getSearchResults(language, date)).thenReturn(List.of(repo1, repo2));

        List<Repository> result = scoringService.getScoringInfo(language, date);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getScore() > 0);
        assertTrue(result.get(1).getScore() > 0);

        ArgumentCaptor<List<Repository>> captor = ArgumentCaptor.forClass(List.class);
        verify(cacheProvider).put(eq(language), eq(date), captor.capture());

        List<Repository> cachedValue = captor.getValue();
        assertEquals(2, cachedValue.size());
        assertTrue(cachedValue.get(0).getScore() > 0);
        assertTrue(cachedValue.get(1).getScore() > 0);

        verify(searchProvider).getSearchResults(language, date);
    }
}
