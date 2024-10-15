package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.ViewStatsRequestDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Uri;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.EndpointHitRepository;
import ru.practicum.repository.UriRepository;

import java.time.Instant;
import java.util.List;

import static ru.practicum.ewm.stats.utils.Constants.MSK_ZONE;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    final EndpointHitRepository endpointHitRepository;
    final UriRepository uriRepository;
    final AppRepository appRepository;

    @Override
    public EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto) {
        App app = getAppByName(endpointHitDto.getApp());
        Uri uri = getUriByName(endpointHitDto.getUri());
        return EndpointHitMapper.toEndpointHitDto(endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto, app, uri)));
    }

    @Override
    public List<ViewStatsDto> getStats(ViewStatsRequestDto viewStatsRequestDto) {
        Instant start = viewStatsRequestDto.getStart().atZone(MSK_ZONE).toInstant();
        Instant end = viewStatsRequestDto.getEnd().atZone(MSK_ZONE).toInstant();
        return ViewStatsMapper.toViewStatsDto(endpointHitRepository.findViewStats(start, end, viewStatsRequestDto.getUris(), viewStatsRequestDto.isUnique()));
    }

    private App getAppByName(String appStr) {
        return appRepository.findByName(appStr)
                .orElseGet(() -> appRepository.save(App.builder().name(appStr).build()));
    }

    private Uri getUriByName(String uriStr) {
        return uriRepository.findByName(uriStr)
                .orElseGet(() -> uriRepository.save(Uri.builder().name(uriStr).build()));
    }
}
