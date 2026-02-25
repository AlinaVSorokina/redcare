package com.redcare.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Repository {

    private long id;
    private String fullName;
    private String htmlUrl;
    private String createdAt;
    private String updatedAt;
    private String pushedAt;
    private int stargazersCount;
    private int forksCount;
    private String language;
    private int score;

}
