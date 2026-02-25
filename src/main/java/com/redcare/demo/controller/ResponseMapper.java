package com.redcare.demo.controller;

import com.redcare.demo.model.Repository;

public class ResponseMapper {

    public static RepositoryDTO map(Repository repository) {
        return new RepositoryDTO(repository.getId(), repository.getScore(), repository.getFullName(),
        repository.getHtmlUrl(), repository.getLanguage());
    }
}
