package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FutureHoursConstraintValidator implements ConstraintValidator<FutureHours, Instant> {
    int hours;

    @Override
    public void initialize(FutureHours constraintAnnotation) {
        this.hours = constraintAnnotation.hours();
    }

    @Override
    public boolean isValid(Instant date, ConstraintValidatorContext context) {
        if (Objects.isNull(date)) {
            return true;
        }
        return date.isAfter(Instant.now().truncatedTo(ChronoUnit.SECONDS).plus(Duration.ofHours(hours)));
    }
}