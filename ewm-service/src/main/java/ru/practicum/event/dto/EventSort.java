package ru.practicum.event.dto;

import java.util.Optional;

public enum EventSort {
    EVENT_DATE, VIEWS;

    public static Optional<EventSort> from(String stringEventSort) {
        for (EventSort state : values()) {
            if (state.name().equalsIgnoreCase(stringEventSort)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
