package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.model.App;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Uri;

import java.time.Instant;

import static ru.practicum.ewm.stats.utils.Constants.DATE_TIME_FORMATTER;

@UtilityClass
public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp().getName())
                .uri(endpointHit.getUri().getName())
                .ip(endpointHit.getIp())
                .timestamp(DATE_TIME_FORMATTER.format(endpointHit.getTimestamp()))
                .build();
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto, App app, Uri uri) {
        return EndpointHit.builder()
                .app(app)
                .uri(uri)
                .ip(endpointHitDto.getIp())
                .timestamp(DATE_TIME_FORMATTER.parse(endpointHitDto.getTimestamp(), Instant::from))
                .build();
    }
}
