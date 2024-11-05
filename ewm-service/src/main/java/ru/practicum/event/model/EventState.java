package ru.practicum.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED,
    CREATED
}
