package ru.practicum.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventAdminSearchDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.validation.DateRangeValidator;
import ru.practicum.validation.LocationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.ewm.stats.utils.Constants.UTC_ZONE;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/events")
public class EventAdminController {
    final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(name = "users", required = false) List<@Positive Long> usersIds,
            @RequestParam(required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<@Positive Long> categoriesIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) @Positive Long locationId,
            @RequestParam(required = false) Float lat,
            @RequestParam(required = false) Float lon,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Float radius,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        DateRangeValidator.validateDateRange(rangeStart, rangeEnd);
        LocationRequestValidator.validateLocationRequest(locationId, lat, lon);
        return eventService.getAllEventsWithFilter(new EventAdminSearchDto(
                usersIds,
                states,
                categoriesIds,
                Objects.isNull(rangeStart) ? null : rangeStart.atZone(UTC_ZONE).toInstant(),
                Objects.isNull(rangeEnd) ? null : rangeEnd.atZone(UTC_ZONE).toInstant(),
                locationId,
                lat,
                lon,
                radius,
                from,
                size));

    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable @Positive Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
