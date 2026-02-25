package com.redcare.demo.search;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GithubSearchParser {

    private final ObjectMapper objectMapper;

    public GithubSearchParser() {
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<GitHubRepository> parseRepos(String json) throws IOException {
        GitHubSearchResponse response = objectMapper.readValue(json, GitHubSearchResponse.class);

        if (response.getItems() == null) {
            return Collections.emptyList();
        }

        return response.getItems();
    }
}
