package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventSort;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.Instant;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByIdForUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getParticipationRequestsForEventByUserId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatusByUser(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getAllEventsWithFilter(List<Long> usersIds,
                                              List<String> states,
                                              List<Long> categoriesIds,
                                              Instant rangeStart,
                                              Instant rangeEnd,
                                              Integer from,
                                              Integer size
    );

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getPublishedEventsWithFilter(String text,
                                                     List<Long> categoriesIds,
                                                     Boolean paid,
                                                     Instant rangeStart,
                                                     Instant rangeEnd,
                                                     Boolean onlyAvailable,
                                                     EventSort sort,
                                                     Integer from,
                                                     Integer size,
                                                     HttpServletRequest request);

    EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request);
}
