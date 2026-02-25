package com.redcare.demo.controller;

public record RepositoryDTO(long repoId,
                            int score,
                            String fullName,
                            String htmlUrl,
                            String language) {
}
