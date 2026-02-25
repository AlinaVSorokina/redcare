package com.redcare.demo.search;

import com.redcare.demo.exception.BusinessException;
import com.redcare.demo.model.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubSearchProviderImplTest {

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private GitHubSearchProviderImpl provider;

    @Test
    void testSearchWithDuplication() throws Exception {
        String language = "Java";
        LocalDate creationDate = LocalDate.of(2021, 1, 1);

        GitHubRepository repo1 = getGitHubRepository(1L, "name/repo1","https://example/repo1", language,
                100, 10);

        GitHubRepository repo2 = getGitHubRepository(2L, "name/repo2","https://example/repo2", language,
                200, 20);

        GitHubRepository repo3 = getGitHubRepository(2L, "name/duplication","https://example/repo3", language,
                999, 99);

        GitHubRepository repo4 = getGitHubRepository(3L, "name/repo4","https://example/repo4", language,
                300, 30);

        when(gitHubClient.getPageOfRepos(language, creationDate, "stars", 1))
                .thenReturn(List.of(repo1, repo2));
        when(gitHubClient.getPageOfRepos(language, creationDate, "forks", 1))
                .thenReturn(List.of(repo3, repo4));

        List<Repository> result = provider.getSearchResults(language, creationDate);

        assertEquals(3, result.size());

        assertTrue(result.stream().anyMatch(r -> r.getId() == 1L && "https://example/repo1".equals(r.getHtmlUrl())));
        assertTrue(result.stream().anyMatch(r -> r.getId() == 2L));
        assertTrue(result.stream().anyMatch(r -> r.getId() == 3L && "name/repo4".equals(r.getFullName())));

        verify(rateLimiter, times(1)).acquire();
        verify(gitHubClient).getPageOfRepos(language, creationDate, "stars", 1);
        verify(gitHubClient).getPageOfRepos(language, creationDate, "forks", 1);
    }

    @Test
    void wrapsIOExceptionIntoBusinessException() throws Exception {
        when(gitHubClient.getPageOfRepos(any(), any(), any(), anyInt()))
                .thenThrow(new IOException("failure"));

        assertThrows(BusinessException.class,
                () -> provider.getSearchResults("Java", LocalDate.of(2021, 1, 1)));

        verify(rateLimiter, never()).acquire();
    }

    private GitHubRepository getGitHubRepository(long id, String name, String url,
                                                 String language, int stars, int forks) {
        GitHubRepository repo = new GitHubRepository();
        repo.setId(id);
        repo.setFullName(name);
        repo.setHtmlUrl(url);
        repo.setLanguage(language);
        repo.setPushedAt(Instant.parse("2026-02-24T00:00:00Z").toString());
        repo.setStargazersCount(stars);
        repo.setForksCount(forks);
        return repo;
    }
}