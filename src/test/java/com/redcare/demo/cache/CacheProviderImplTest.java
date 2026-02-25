package com.redcare.demo.cache;

import com.redcare.demo.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CacheProviderImplTest {

    private CacheProviderImpl cache;

    @BeforeEach
    void setUp() {
        cache = new CacheProviderImpl();
    }

    @Test
    void containsReturnsFalseWhenEmpty() {
        assertThat(cache.contains("Java", LocalDate.of(2024, 1, 1))).isFalse();
    }

    @Test
    void putContainsGet() {
        CacheProviderImpl cache = new CacheProviderImpl();

        String language = "Java";
        LocalDate creationDate = LocalDate.of(2021, 1, 1);

        List<Repository> repos = List.of(
                Repository.builder().id(1L).fullName("name1").build(),
                Repository.builder().id(2L).fullName("name2").build()
        );

        cache.put(language, creationDate, repos);

        assertTrue(cache.contains(language, creationDate));
        List<Repository> cached = cache.get(language, creationDate);
        assertNotNull(cached);
        assertEquals(2, cached.size());
        assertEquals(1L, cached.getFirst().getId());
    }

}
