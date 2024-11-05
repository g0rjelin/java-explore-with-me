package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ParticipationRequestStatus {
    CONFIRMED, REJECTED, PENDING, CANCELED
}
