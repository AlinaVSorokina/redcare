package com.redcare.demo.controller;

import com.redcare.demo.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * REST controller responsible for handling repository scoring requests.
 *
 * <p>Exposes an endpoint that retrieves and ranks GitHub repositories.
 * Select repositories based on a given programming language and creation date.
 * Results are returned sorted by score in descending order.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ScoringController {

    private final ScoringService scoringService;

    private static final Comparator<RepositoryDTO> BY_SCORE_DESC =
            Comparator.comparingInt(RepositoryDTO::score).reversed();

    /**
     * Retrieves a scored and sorted list of repositories matching the given criteria.
     *
     * <p>Queries the {@link ScoringService} for repositories filtered by the specified
     * programming language and creation date, maps each result to a {@link RepositoryDTO},
     * and returns them sorted by score from highest to lowest.
     *
     * @param language     the programming language to filter repositories by (e.g. {@code "Java"})
     * @param creationDate the date from which repositories were created, in ISO date format
     *                     ({@code yyyy-MM-dd})
     * @return a list of {@link RepositoryDTO} objects sorted by score in descending order;
     *         never {@code null}, may be empty if no repositories match the criteria
     */
    @GetMapping(value = "/scoring")
    public List<RepositoryDTO> getScoring(@RequestParam String language,
                           @RequestParam
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  LocalDate creationDate) {
        log.info("get request: language {}, creationDate {}", language, creationDate);
        return scoringService.getScoringInfo(language, creationDate).stream()
                .map(ResponseMapper::map)
                .sorted(BY_SCORE_DESC)
                .toList();
    }
}
