package ru.practicum.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.practicum.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@UtilityClass
public class DateRangeValidator {
    static final String END_DATE_BEFORE_START_ERROR_MSG = "Дата окончания не может раньше даты начала";

    public static void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (!Objects.isNull(startDate) && !Objects.isNull(endDate) && startDate.isAfter(endDate)) {
            throw new BadRequestException(END_DATE_BEFORE_START_ERROR_MSG);
        }
    }
}
