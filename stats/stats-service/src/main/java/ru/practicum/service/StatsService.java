package ru.practicum.service;

import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.ViewStatsRequestDto;

import java.util.List;

public interface StatsService {
    EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(ViewStatsRequestDto viewStatsRequestDto);
}
