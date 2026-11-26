package com.example.secureops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Period;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class AgeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void validDateReturnsAge() throws Exception {
        int expected = Period.between(LocalDate.of(2000, 1, 1), LocalDate.now()).getYears();
        mvc.perform(get("/api/age").param("birthDate", "2000-01-01"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.age").value(expected));
    }

    @Test
    void futureDateReturns400() throws Exception {
        mvc.perform(get("/api/age").param("birthDate", "3000-01-01"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void tooOldDateReturns400() throws Exception {
        mvc.perform(get("/api/age").param("birthDate", "1800-01-01"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void invalidFormatReturns400() throws Exception {
        mvc.perform(get("/api/age").param("birthDate", "abc"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void missingParameterReturns400() throws Exception {
        mvc.perform(get("/api/age"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void webPageIsServed() throws Exception {
        mvc.perform(get("/index.html"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Calculateur d")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("birthDate")));
    }

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
           .andExpect(status().isOk());
    }

    @Test
    void prometheusEndpointIsExposed() throws Exception {
        mvc.perform(get("/actuator/prometheus"))
           .andExpect(status().isOk());
    }
}