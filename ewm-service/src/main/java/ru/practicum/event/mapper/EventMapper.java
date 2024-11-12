package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.location.model.Location;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event, Long views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(Objects.requireNonNullElse(views, 0L))
                .build();
    }

    public static List<EventFullDto> toEventFullDto(List<Event> events, Map<Long, Long> views) {
        List<EventFullDto> eventFullDtosDtos = new ArrayList<>();
        for (Event event : events) {
            eventFullDtosDtos.add(toEventFullDto(event, views.get(event.getId())));
        }
        return eventFullDtosDtos;
    }

    public static EventShortDto toEventShortDto(Event event, Long views) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(Objects.requireNonNullElse(views, 0L))
                .build();
    }

    public static List<EventShortDto> toEventShortDto(List<Event> events, Map<Long, Long> views) {
        List<EventShortDto> eventShortDtosDtos = new ArrayList<>();
        for (Event event : events) {
            eventShortDtosDtos.add(toEventShortDto(event, views.get(event.getId())));
        }
        return eventShortDtosDtos;
    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category, Location location) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }
}
