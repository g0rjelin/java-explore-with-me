package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    List<String> enumStringList;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumStringList = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name).toList();

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return enumStringList.contains(value.toUpperCase());
    }
}