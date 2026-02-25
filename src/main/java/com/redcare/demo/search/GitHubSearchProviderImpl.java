package com.redcare.demo.search;

import com.redcare.demo.exception.BusinessException;
import com.redcare.demo.model.Repository;
import com.redcare.demo.service.SearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubSearchProviderImpl implements SearchProvider {

    private final GitHubClient gitHubClient;

    private static final String STAR_SORT = "stars";
    private static final String FORK_SORT = "forks";

    @Override
    public List<Repository> getSearchResults(String language, LocalDate creationDate) {
        try {
            List<GitHubRepository> repos =
                    new ArrayList<>(gitHubClient.getPageOfRepos(language, creationDate, STAR_SORT, 1));
            Thread.sleep(1000);
            repos.addAll(gitHubClient.getPageOfRepos(language, creationDate, FORK_SORT, 1));
            return repos.stream()
                    .distinct()
                    .map(this::withScore)
                    .toList();
        } catch (IOException e) {
            throw new BusinessException("unexpected data");
        } catch (InterruptedException e) {
            throw new BusinessException("unexpected failure");
        }
    }

    private Repository withScore(GitHubRepository repoItem) {
        return Repository.builder()
                .id(repoItem.getId())
                .fullName(repoItem.getFullName())
                .htmlUrl(repoItem.getHtmlUrl())
                .language(repoItem.getLanguage())
                .pushedAt(repoItem.getPushedAt())
                .stargazersCount(repoItem.getStargazersCount())
                .forksCount(repoItem.getForksCount())
                .build();
    }
}
