package com.example.secureops;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AgeService {

    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);

    public int calculateAge(LocalDate birthDate) {
        return calculateAge(birthDate, LocalDate.now());
    }

    // Version avec "today" en paramètre : plus facile à tester
    public int calculateAge(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur");
        }
        if (birthDate.isBefore(MIN_DATE)) {
            throw new IllegalArgumentException("La date de naissance doit être après 1900");
        }
        return Period.between(birthDate, today).getYears();
    }
}