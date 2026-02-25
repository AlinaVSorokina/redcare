package com.redcare.demo.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class GitHubClient {

    private static final String SEARCH_REQUEST = "language:%s created:>=%s";

    private final RestClient restClient;
    private final GithubSearchParser parser;

    private static final String BASE_URL = "https://api.github.com";
    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String USER_AGENT_NAME = "test-app-name";
    private static final String SORT_ORDER = "desc";
    private static final int PAGE_SIZE = 100;

    public GitHubClient(GithubSearchParser parser) {
        this.parser = parser;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT_NAME)
                .build();
    }

    public List<GitHubRepository> getPageOfRepos(String language, LocalDate date,
                                                 String sort, int page) throws IOException {
        String json = this.searchPage(getQuery(language, date), sort, page);
        return parser.parseRepos(json);
    }

    private String searchPage(String q, String sort, int page) {
        log.info("execute request - query: {}, sort by {}, page number: {} ", q, sort, page);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", q)
                        .queryParam("sort", sort)
                        .queryParam("order", SORT_ORDER)
                        .queryParam("per_page", PAGE_SIZE)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(String.class);
    }

    private String getQuery(String language, LocalDate date) {
        return String.format(SEARCH_REQUEST, language, date.toString());
    }

}
