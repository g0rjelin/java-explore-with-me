package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.model.ParticipationRequestStatus;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Data
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant created;
    Long event;
    Long requester;
    ParticipationRequestStatus status;
}
