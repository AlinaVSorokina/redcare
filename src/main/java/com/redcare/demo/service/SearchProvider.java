package com.redcare.demo.service;

import com.redcare.demo.model.Repository;

import java.time.LocalDate;
import java.util.List;

public interface SearchProvider {

    List<Repository> getSearchResults(String language, LocalDate creationDate);

}
