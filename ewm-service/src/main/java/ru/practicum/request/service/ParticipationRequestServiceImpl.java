package ru.practicum.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.ParticipationRequestStatus;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    final ParticipationRequestRepository participationRequestRepository;
    final UserRepository userRepository;
    final EventRepository eventRepository;

    static final String INITIATOR_CANT_BE_REQUESTER_CONFLICT_ERROR_MSG = "Инициатор события не может сделать на него запрос";
    static final String NOT_PUBLISHED_EVENT_REQUESTED_ERROR_MSG = "Нельзя сделать запрос на неопубликованное событие";
    static final String PARTICIPATION_LIMIT_REACHED_ERROR_MSG = "Исчерпан лимит на участие в событии";
    static final String CANCEL_PARTICIPATION_CONFLICT_ERROR_MSG = "Только инициатор может отказаться от своей заявки";
    static final String REPEAT_REQUEST_ERROR_MSG = "Нельзя добавить повторный запрос";

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User requester = userRepository.getUserById(userId);
        Event event = eventRepository.getEventById(eventId);
        if (participationRequestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(REPEAT_REQUEST_ERROR_MSG);
        } else if (requester.equals(event.getInitiator())) {
            throw new ConflictException(INITIATOR_CANT_BE_REQUESTER_CONFLICT_ERROR_MSG);
        } else if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(NOT_PUBLISHED_EVENT_REQUESTED_ERROR_MSG);
        } else if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException(PARTICIPATION_LIMIT_REACHED_ERROR_MSG);
        }
        ParticipationRequest participationRequest = participationRequestRepository.save(ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .status((!event.isRequestModeration() || event.getParticipantLimit() == 0) ?
                        ParticipationRequestStatus.CONFIRMED : ParticipationRequestStatus.PENDING)
                .build());
        if (participationRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsByUserId(Long userId) {
        userRepository.getUserById(userId);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        User requester = userRepository.getUserById(userId);
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(requestId, ParticipationRequest.class.toString()));
        if (!requester.equals(participationRequest.getRequester())) {
            throw new ConflictException(CANCEL_PARTICIPATION_CONFLICT_ERROR_MSG);
        }
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequestRepository.save(participationRequest);
        Event event = participationRequest.getEvent();
        event.setConfirmedRequests(participationRequest.getEvent().getConfirmedRequests() - 1);
        eventRepository.save(event);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }
}
