package com.redcare.demo.service;

import com.redcare.demo.model.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.redcare.demo.service.ScoringAlgorithm.calculateScore;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final SearchProvider searchProvider;
    private final CacheProvider cacheProvider;


    public List<Repository> getScoringInfo(String language, LocalDate creationDate) {
        if (cacheProvider.contains(language, creationDate)) {
            log.info("records found in cache: language {}, creationDate {}", language, creationDate);
            return cacheProvider.get(language, creationDate);
        } else {
            log.info("need to fetch from GitHub: language {}, creationDate {}", language, creationDate);
            List<Repository> scoredRepos = searchProvider.getSearchResults(language, creationDate)
                    .stream()
                    .map(this::withScore)
                    .toList();
            cacheProvider.put(language, creationDate, scoredRepos);
            return scoredRepos;
        }
    }

    private Repository withScore(Repository repository) {
        repository.setScore(calculateScore(repository));
        return repository;
    }
}
