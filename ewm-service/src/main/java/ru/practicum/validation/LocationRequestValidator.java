package ru.practicum.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import ru.practicum.exception.BadRequestException;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@UtilityClass
public class LocationRequestValidator {
    static final String BOTH_LOCATION_AND_ID_IN_REQUEST_ERROR_MSG = "Нельзя указывать одновременно координаты локации и идентификатор";
    static final String INCOMPLETE_COORDINATES_IN_REQUEST_ERROR_MSG = "Указаны неполные координаты локации";

    public static void validateLocationRequest(Long locationId, Float lat, Float lon) {
        if (!Objects.isNull(locationId) && !Objects.isNull(lat) && !Objects.isNull(lon)) {
            throw new BadRequestException(BOTH_LOCATION_AND_ID_IN_REQUEST_ERROR_MSG);
        }
        if (Objects.isNull(locationId) && (Objects.isNull(lat) && !Objects.isNull(lon) || !Objects.isNull(lat) && Objects.isNull(lon))) {
            throw new BadRequestException(INCOMPLETE_COORDINATES_IN_REQUEST_ERROR_MSG);
        }
    }
}
