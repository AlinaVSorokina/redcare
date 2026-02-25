package com.redcare.demo.controller;

import com.redcare.demo.model.Repository;
import com.redcare.demo.service.ScoringService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ScoringControllerTest {

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private ScoringController scoringController;

    private MockMvc mockMvc() {
        return MockMvcBuilders.standaloneSetup(scoringController).build();
    }

    @Test
    void returnScoredRepositories() throws Exception {
        LocalDate date = LocalDate.of(2023, 1, 1);

        Repository repoLow  = buildRepo("repoLow",  10);
        Repository repoHigh = buildRepo("repoHigh", 90);
        Repository repoMid  = buildRepo("repoMid",  50);

        when(scoringService.getScoringInfo("Java", date))
                .thenReturn(List.of(repoLow, repoHigh, repoMid));

        mockMvc().perform(get("/scoring")
                        .param("language", "Java")
                        .param("creationDate", "2023-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("repoHigh"))
                .andExpect(jsonPath("$[1].fullName").value("repoMid"))
                .andExpect(jsonPath("$[2].fullName").value("repoLow"));

        verify(scoringService).getScoringInfo("Java", date);
    }

    @Test
    void returnEmptyList() throws Exception {
        LocalDate date = LocalDate.of(2023, 6, 15);

        when(scoringService.getScoringInfo("Kotlin", date))
                .thenReturn(Collections.emptyList());

        mockMvc().perform(get("/scoring")
                        .param("language", "Kotlin")
                        .param("creationDate", "2023-06-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void return400WhenLanguageParamIsMissing() throws Exception {
        mockMvc().perform(get("/scoring")
                        .param("creationDate", "2023-01-01"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(scoringService);
    }

    @Test
    void return400WhenCreationDateParamIsMissing() throws Exception {
        mockMvc().perform(get("/scoring")
                        .param("language", "Java"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(scoringService);
    }

    @Test
    void return400WhenCreationDateHasInvalidFormat() throws Exception {
        mockMvc().perform(get("/scoring")
                        .param("language", "Java")
                        .param("creationDate", "01-01-2023"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(scoringService);
    }

    private Repository buildRepo(String name, int score) {
        return Repository.builder()
                .fullName(name)
                .score(score)
                .build();
    }
}