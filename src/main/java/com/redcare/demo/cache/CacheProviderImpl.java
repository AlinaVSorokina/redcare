package com.redcare.demo.cache;

import com.redcare.demo.model.Repository;
import com.redcare.demo.service.CacheProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheProviderImpl implements CacheProvider {

    Map<Key, List<Repository>> cache = new HashMap<>();

    public void put(String language, LocalDate creationDate, List<Repository> items) {
        cache.put(new Key(language, creationDate), items);
    }

    public boolean contains(String language, LocalDate creationDate) {
        return cache.containsKey(new Key(language, creationDate));
    }

    public List<Repository> get(String language, LocalDate creationDate) {
        return cache.get(new Key(language, creationDate));
    }

    public record Key(String language, LocalDate creationDate) {
    }
}
