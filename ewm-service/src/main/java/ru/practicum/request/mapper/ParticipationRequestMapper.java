package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toParticipationRequestDto(List<ParticipationRequest> participationRequests) {
        List<ParticipationRequestDto> participationRequestDto = new ArrayList<>();
        for (ParticipationRequest participationRequest : participationRequests) {
            participationRequestDto.add(toParticipationRequestDto(participationRequest));
        }
        return participationRequestDto;
    }


}
