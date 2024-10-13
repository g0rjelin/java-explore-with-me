package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.App;
import ru.practicum.model.Uri;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.EndpointHitRepository;
import ru.practicum.repository.UriRepository;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static ru.practicum.ewm.stats.utils.Constants.DATE_TIME_FORMATTER;

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
    public List<ViewStatsDto> getStats(String startStr, String endStr, List<String> uris, boolean unique) {
        Instant start = DATE_TIME_FORMATTER.parse(java.net.URLDecoder.decode(startStr, StandardCharsets.UTF_8), Instant::from);
        Instant end = DATE_TIME_FORMATTER.parse(java.net.URLDecoder.decode(endStr, StandardCharsets.UTF_8), Instant::from);
        return ViewStatsMapper.toViewStatsDto(endpointHitRepository.findViewStats(start, end, uris, unique));
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
