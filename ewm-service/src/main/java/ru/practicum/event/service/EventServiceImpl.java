package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.AbstractUpdateEventRequestDto;
import ru.practicum.event.dto.EventAdminSearchDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventPublicSearchDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventSort;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.StateActionAdmin;
import ru.practicum.event.dto.StateActionUser;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.location.model.Location;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.model.QLocation;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.ParticipationRequestUpdateStatus;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationRequestStatus;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.stats.StatsService;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    final EventRepository eventRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final LocationRepository locationRepository;
    final ParticipationRequestRepository participationRequestRepository;
    final StatsService statsService;

    @Value("${spring.application.name}")
    String appName;


    static final String UPDATE_NOT_PENDING_OR_CANCELED_EVENT_ERROR_MSG = "Можно обновить только отмененное событие или событие в режиме ожидания";
    static final String PUBLISH_NOT_PENDING_EVENT_ERROR_MSG = "Нельзя публиковать событие не в статусе ожидает публикации";
    static final String CANCEL_NOT_PUBLISHED_EVENT_ERROR_MSG = "Нельзя отклонить неопубликованное событие";
    static final String USER_UPDATE_EVENTDATE_ERROR_MSG = "Дата события не может быть раньше, чем через два часа от текущего момента";
    static final String ADMIN_UPDATE_EVENTDATE_ERROR_MSG = "Дата события должна быть не ранее чем за час от даты публикации";
    static final String PARTICIPATION_INFO_REQUESTED_NOT_BY_INITIATOR_ERROR_MSG = "Информация о запросах события может быть запрошена только его инициатором";
    static final String REQUEST_STATUS_UPDATE_BY_NOT_INITIATOR_ERROR_MSG = "Статус запросов события может быть изменен только его инициатором";
    static final String NOT_PENDING_REQUESTS_UPDATE_ERROR_MSG = "Могут быть обновлены только заявки в статусе PENDING";
    static final String CANT_CONFIRM_OVER_LIMIT_ERROR_MSG = "Исчерпан лимит подтвержденных заявок события";

    static final Integer USER_UPDATE_LIMIT_HOURS = 2;
    static final Integer ADMIN_UPDATE_LIMIT_HOURS = 1;

    static final String DEFAULT_LOCATION_FROM_EVENT_PREFIX = "Локация для: %s";

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.getUserById(userId);
        Category category = categoryRepository.getCategoryById(newEventDto.getCategory());
        Location location = getLocation(newEventDto.getLocation(), newEventDto.getTitle());
        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(newEventDto, initiator, category, location)), 0L);
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        userRepository.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Long, Long> views = statsService.getViewsForEvents(events);
        return EventMapper.toEventShortDto(events, views);
    }

    @Override
    public EventFullDto getEventByIdForUser(Long userId, Long eventId) {
        userRepository.getUserById(userId);
        Event event = eventRepository.getEventById(eventId);
        return EventMapper.toEventFullDto(event, statsService.getViewsFromStartToNow(event.getCreatedOn(), eventId));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userRepository.getUserById(userId);
        Event event = eventRepository.getEventById(eventId);
        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException(UPDATE_NOT_PENDING_OR_CANCELED_EVENT_ERROR_MSG);
        }
        if (!Objects.isNull(updateEventUserRequest.getEventDate()) && updateEventUserRequest.getEventDate().isBefore(
                Instant.now().plus(Duration.ofHours(USER_UPDATE_LIMIT_HOURS)))) {
            throw new ConflictException(USER_UPDATE_EVENTDATE_ERROR_MSG);
        }
        setEventRequestsDtoToEvent(updateEventUserRequest, event);
        if (!Objects.isNull(updateEventUserRequest.getStateAction())) {
            StateActionUser stateActionUser = StateActionUser.valueOf(updateEventUserRequest.getStateAction());
            switch (stateActionUser) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event), statsService.getViewsFromStartToNow(event.getCreatedOn(), eventId));
    }

    private void setEventRequestsDtoToEvent(AbstractUpdateEventRequestDto updateEventRequestDto, Event event) {
        if (!Objects.isNull(updateEventRequestDto.getCategoryId())) {
            Category category = categoryRepository.getCategoryById(updateEventRequestDto.getCategoryId());
            event.setCategory(category);
        }
        event.setAnnotation(Objects.requireNonNullElse(updateEventRequestDto.getAnnotation(), event.getAnnotation()));
        event.setDescription(Objects.requireNonNullElse(updateEventRequestDto.getDescription(), event.getDescription()));
        event.setEventDate(Objects.requireNonNullElse(updateEventRequestDto.getEventDate(), event.getEventDate()));
        event.setTitle(Objects.requireNonNullElse(updateEventRequestDto.getTitle(), event.getTitle()));
        if (!Objects.isNull(updateEventRequestDto.getLocation())) {
            Location location = getLocation(updateEventRequestDto.getLocation(), event.getTitle());
            event.setLocation(location);
        }
        event.setPaid(Objects.requireNonNullElse(updateEventRequestDto.getPaid(), event.isPaid()));
        event.setParticipantLimit(Objects.requireNonNullElse(updateEventRequestDto.getParticipantLimit(), event.getParticipantLimit()));
        event.setRequestModeration(Objects.requireNonNullElse(updateEventRequestDto.getRequestModeration(), event.isRequestModeration()));
        event.setEventDate(Objects.requireNonNullElse(updateEventRequestDto.getEventDate(), event.getEventDate()));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsForEventByUserId(Long userId, Long eventId) {
        User initiator = userRepository.getUserById(userId);
        Event event = eventRepository.getEventById(eventId);
        if (!initiator.equals(event.getInitiator())) {
            throw new ConflictException(PARTICIPATION_INFO_REQUESTED_NOT_BY_INITIATOR_ERROR_MSG);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatusByUser(Long userId,
                                                                         Long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User initiator = userRepository.getUserById(userId);
        Event event = eventRepository.getEventById(eventId);
        if (!initiator.equals(event.getInitiator())) {
            throw new ConflictException(REQUEST_STATUS_UPDATE_BY_NOT_INITIATOR_ERROR_MSG);
        }
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        //для задания строгой определенности в порядке подтверждения заявок: от более ранних к более поздним
        List<ParticipationRequest> requests = participationRequestRepository.findAllByIdInOrderByCreatedAsc(requestIds);
        Optional<ParticipationRequest> isNotPendingStatus = requests.stream()
                .filter(request -> request.getStatus() != ParticipationRequestStatus.PENDING)
                .findFirst();
        if (isNotPendingStatus.isPresent())
            throw new ConflictException(NOT_PENDING_REQUESTS_UPDATE_ERROR_MSG);

        ParticipationRequestUpdateStatus status = ParticipationRequestUpdateStatus.valueOf(eventRequestStatusUpdateRequest.getStatus());

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (status.equals(ParticipationRequestUpdateStatus.CONFIRMED)) {
            int participantLimit = event.getParticipantLimit();
            int confirmedRequestsCount = event.getConfirmedRequests();
            if (participantLimit <= confirmedRequestsCount) {
                throw new ConflictException(CANT_CONFIRM_OVER_LIMIT_ERROR_MSG);
            }
            for (ParticipationRequest request : requests) {
                if (participantLimit > confirmedRequestsCount) {
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    confirmedRequestsCount++;
                } else {
                    request.setStatus(ParticipationRequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            event.setConfirmedRequests(confirmedRequestsCount);
            eventRepository.save(event);
        } else {
            for (ParticipationRequest request : requests) {
                request.setStatus(ParticipationRequestStatus.REJECTED);
            }
            rejectedRequests.addAll(requests);
        }
        participationRequestRepository.saveAll(requests);
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(ParticipationRequestMapper.toParticipationRequestDto(confirmedRequests))
                .rejectedRequests(ParticipationRequestMapper.toParticipationRequestDto(rejectedRequests))
                .build();
    }

    @Override
    public List<EventFullDto> getAllEventsWithFilter(EventAdminSearchDto eventAdminSearchDto) {
        BooleanExpression condition = Expressions.TRUE.isTrue();
        Long locationId = eventAdminSearchDto.getLocationId();
        Float lat = eventAdminSearchDto.getLat();
        Float lon = eventAdminSearchDto.getLon();
        Float radius = eventAdminSearchDto.getRadius();
        if (!Objects.isNull(locationId) || (!Objects.isNull(lat) && !Objects.isNull(lon))) {
            if (!Objects.isNull(locationId)) {
                Location location = locationRepository.getLocationById(locationId);
                lat = location.getLat();
                lon = location.getLon();
                radius = location.getRadius();
            }
            if (Objects.isNull(radius) || radius.equals(0.0F)) {
                condition = condition.and(QLocation.location.lat.eq(lat)
                        .and(QLocation.location.lon.eq(lon)));
            } else {
                condition = condition.and(
                        Expressions.numberTemplate(Float.class, "distance({0}, {1}, {2}, {3})",
                                        lat, lon, QLocation.location.lat, QLocation.location.lon)
                                .loe(radius));
            }
        }
        List<Long> usersIds = eventAdminSearchDto.getUsersIds();
        if (!Objects.isNull(usersIds) && !usersIds.isEmpty()) {
            condition = condition.and(QEvent.event.initiator.id.in(usersIds));
        }
        List<String> states = eventAdminSearchDto.getStates();
        if (!Objects.isNull(states) && !states.isEmpty()) {
            List<EventState> eventStates = states.stream()
                    .map(EventState::valueOf)
                    .toList();
            condition = condition.and(QEvent.event.state.in(eventStates));
        }
        List<Long> categoriesIds = eventAdminSearchDto.getCategoriesIds();
        if (!Objects.isNull(categoriesIds) && !categoriesIds.isEmpty()) {
            List<Category> categoryList = categoryRepository.findAllById(categoriesIds);
            condition = condition.and(QEvent.event.category.in(categoryList));
        }
        Instant rangeStart = eventAdminSearchDto.getRangeStart();
        if (!Objects.isNull(rangeStart)) {
            condition = condition.and(QEvent.event.eventDate.after(rangeStart));
        }
        Instant rangeEnd = eventAdminSearchDto.getRangeEnd();
        if (!Objects.isNull(rangeEnd)) {
            condition = condition.and(QEvent.event.eventDate.before(rangeEnd));
        }
        Integer from = eventAdminSearchDto.getFrom();
        Integer size = eventAdminSearchDto.getSize();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findAll(condition, page).getContent();
        Map<Long, Long> views = statsService.getViewsForEvents(events);
        return EventMapper.toEventFullDto(events, views);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.getEventById(eventId);
        event.setEventDate(Objects.requireNonNullElse(updateEventAdminRequest.getEventDate(), event.getEventDate()));
        if (!Objects.isNull(updateEventAdminRequest.getStateAction())) {
            StateActionAdmin stateActionAdmin = StateActionAdmin.valueOf(updateEventAdminRequest.getStateAction());
            if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException(PUBLISH_NOT_PENDING_EVENT_ERROR_MSG);
            }
            if (stateActionAdmin.equals(StateActionAdmin.REJECT_EVENT) && event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException(CANCEL_NOT_PUBLISHED_EVENT_ERROR_MSG);
            }
            if (stateActionAdmin.equals(StateActionAdmin.PUBLISH_EVENT) && !event.getEventDate().isAfter(
                    Instant.now().plus(Duration.ofHours(ADMIN_UPDATE_LIMIT_HOURS)))) {
                throw new ConflictException(ADMIN_UPDATE_EVENTDATE_ERROR_MSG);
            }
            switch (stateActionAdmin) {
                case PUBLISH_EVENT -> event.setState(EventState.PUBLISHED);
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
            }
        }
        setEventRequestsDtoToEvent(updateEventAdminRequest, event);
        return EventMapper.toEventFullDto(eventRepository.save(event), statsService.getViewsFromStartToNow(event.getCreatedOn(), eventId));
    }

    @Override
    public List<EventShortDto> getPublishedEventsWithFilter(EventPublicSearchDto eventPublicSearchDto,
                                                            HttpServletRequest request) {
        BooleanExpression condition = QEvent.event.state.eq(EventState.PUBLISHED);
        Long locationId = eventPublicSearchDto.getLocationId();
        Float lat = eventPublicSearchDto.getLat();
        Float lon = eventPublicSearchDto.getLon();
        Float radius = eventPublicSearchDto.getRadius();
        if (!Objects.isNull(locationId) || (!Objects.isNull(lat) && !Objects.isNull(lon))) {
            if (!Objects.isNull(locationId)) {
                Location location = locationRepository.getLocationById(locationId);
                lat = location.getLat();
                lon = location.getLon();
                radius = location.getRadius();
            }
            if (Objects.isNull(radius) || radius.equals(0.0F)) {
                condition = condition.and(QLocation.location.lat.eq(lat)
                        .and(QLocation.location.lon.eq(lon)));
            } else {
                condition = condition.and(
                        Expressions.numberTemplate(Float.class, "distance({0}, {1}, {2}, {3})",
                                        lat, lon, QLocation.location.lat, QLocation.location.lon)
                                .loe(radius));
            }
        }
        statsService.create(EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());
        String text = eventPublicSearchDto.getText();
        if (!Objects.isNull(text) && !text.isBlank()) {
            BooleanExpression conditionText = QEvent.event.annotation.containsIgnoreCase(text).or(QEvent.event.description.containsIgnoreCase(text));
            condition = condition.and(conditionText);
        }
        List<Long> categoriesIds = eventPublicSearchDto.getCategoriesIds();
        if (!Objects.isNull(categoriesIds) && !categoriesIds.isEmpty()) {
            List<Category> categoryList = categoryRepository.findAllById(categoriesIds);
            condition = condition.and(QEvent.event.category.in(categoryList));
        }
        Instant rangeStart = eventPublicSearchDto.getRangeStart();
        Instant rangeEnd = eventPublicSearchDto.getRangeEnd();
        if (Objects.isNull(rangeStart) && Objects.isNull(rangeEnd)) {
            condition = condition.and(QEvent.event.eventDate.after(Instant.now()));
        } else {
            if (!Objects.isNull(rangeStart)) {
                condition = condition.and(QEvent.event.eventDate.after(rangeStart));
            }
            if (!Objects.isNull(rangeEnd)) {
                condition = condition.and(QEvent.event.eventDate.before(rangeEnd));
            }
        }
        Boolean onlyAvailable = eventPublicSearchDto.getOnlyAvailable();
        if (!Objects.isNull(onlyAvailable)) {
            condition = condition.and(QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit));
        }
        List<Event> events;
        EventSort sort = eventPublicSearchDto.getEventSort();
        Integer size = eventPublicSearchDto.getSize();
        Integer from = eventPublicSearchDto.getFrom();
        PageRequest page;
        if (!Objects.isNull(sort) && sort.equals(EventSort.EVENT_DATE)) {
            page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("eventDate").ascending());
            events = eventRepository.findAll(condition, page).getContent();
        } else {
            page = PageRequest.of(from > 0 ? from / size : 0, size);
            events = eventRepository.findAll(condition, page).getContent();
        }
        Map<Long, Long> views = statsService.getViewsForEvents(events);
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDto(events, views);
        if (!Objects.isNull(sort) && sort.equals(EventSort.VIEWS)) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return eventShortDtos;
    }

    @Override
    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(eventId, Event.class.toString()));
        statsService.create(EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());
        long views = statsService.getViewsFromStartToNow(event.getCreatedOn(), eventId);
        return EventMapper.toEventFullDto(event, views);
    }

    private Location getLocation(LocationDto locationDto, String eventTitle) {
        return locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon())
                .orElseGet(() -> locationRepository.save(Location.builder()
                        .name(String.format(DEFAULT_LOCATION_FROM_EVENT_PREFIX, eventTitle))
                        .lat(locationDto.getLat())
                        .lon(locationDto.getLon())
                        .radius(0.0F)
                        .build()));
    }
}
