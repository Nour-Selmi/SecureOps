package com.example.secureops;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class AgeController {

    private final AgeService service;
    private final Counter calculations;

    public AgeController(AgeService service, MeterRegistry registry) {
        this.service = service;
        // Métrique personnalisée, visible dans Prometheus : age_calculations_total
        this.calculations = Counter.builder("age.calculations")
                .description("Nombre de calculs d'âge réussis")
                .register(registry);
    }

    @GetMapping("/api/age")
    public Map<String, Object> age(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate) {
        int age = service.calculateAge(birthDate);
        calculations.increment();
        return Map.of("birthDate", birthDate.toString(), "age", age);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalid(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}