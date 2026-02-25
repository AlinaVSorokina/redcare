package com.redcare.demo.service;

import com.redcare.demo.model.Repository;

import java.time.LocalDate;
import java.util.List;

public interface CacheProvider {

    void put(String language, LocalDate creationDate, List<Repository> items);

    boolean contains(String language, LocalDate creationDate);

    List<Repository> get(String language, LocalDate creationDate);

}
