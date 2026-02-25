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

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScoringController {

    private final ScoringService scoringService;

    public static final Comparator<RepositoryDTO> BY_SCORE_DESC =
            Comparator.comparingInt(RepositoryDTO::score).reversed();

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
