package ru.practicum.event;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventSort;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.BadRequestException;
import ru.practicum.validation.DateRangeValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.ewm.stats.utils.Constants.MSK_ZONE;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
public class EventPublicController {
    final EventService eventService;

    static final String WRONG_EVENT_SORT_ENUM_ERROR_MSG = "Некорректное значение сортировки: %s. Должно быть EVENT_DATE или VIEWS";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(name = "categories", required = false) List<@Positive Long> categoriesIds,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        EventSort eventSort = Objects.isNull(sort) ? null : EventSort.from(sort)
                .orElseThrow(() -> new BadRequestException(String.format(WRONG_EVENT_SORT_ENUM_ERROR_MSG, sort)));
        DateRangeValidator.validateDateRange(rangeStart, rangeEnd);
        return eventService.getPublishedEventsWithFilter(text,
                categoriesIds,
                paid,
                Objects.isNull(rangeStart) ? null : rangeStart.atZone(MSK_ZONE).toInstant(),
                Objects.isNull(rangeEnd) ? null : rangeEnd.atZone(MSK_ZONE).toInstant(),
                onlyAvailable,
                eventSort,
                from, size, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublishedEventById(@PathVariable(name = "id") @Positive Long eventId, HttpServletRequest request) {
        return eventService.getPublishedEventById(eventId, request);
    }

}
