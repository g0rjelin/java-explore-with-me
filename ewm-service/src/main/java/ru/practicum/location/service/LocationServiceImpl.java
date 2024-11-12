package ru.practicum.location.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.QLocation;
import ru.practicum.location.repository.LocationRepository;

import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    final LocationRepository locationRepository;
    final EventRepository eventRepository;

    static final String DELETE_LOCATION_WITH_EVENT_ERROR_MSG = "Нельзя удалить локацию, к которой привязано хотя бы одно событие";

    @Override
    public List<LocationFullDto> getLocations(Float lat, Float lon, Float radius, Integer from, Integer size) {
        BooleanExpression condition = Expressions.TRUE.isTrue();
        if (!Objects.isNull(lat) && !Objects.isNull(lon) && !Objects.isNull(radius)) {
            condition = condition.and(
                    Expressions.numberTemplate(Float.class, "distance({0}, {1}, {2}, {3})",
                                    lat, lon, QLocation.location.lat, QLocation.location.lon)
                            .loe(radius));
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Location> locations = locationRepository.findAll(condition, page).getContent();
        return LocationMapper.toLocationFullDto(locations);
    }

    @Override
    public LocationFullDto getLocationByLocId(Long locationId) {
        return LocationMapper.toLocationFullDto(locationRepository.getLocationById(locationId));
    }

    @Override
    public LocationFullDto createLocation(NewLocationDto newLocationDto) {
        return LocationMapper.toLocationFullDto(locationRepository.save(LocationMapper.toNewLocation(newLocationDto)));
    }

    @Override
    public LocationFullDto updateLocation(Long locationId, UpdateLocationDto updateLocationDto) {
        Location location = locationRepository.getLocationById(locationId);
        location.setName(Objects.requireNonNullElse(updateLocationDto.getName(), location.getName()));
        location.setLat(Objects.requireNonNullElse(updateLocationDto.getLat(), location.getLat()));
        location.setLon(Objects.requireNonNullElse(updateLocationDto.getLon(), location.getLon()));
        location.setRadius(Objects.requireNonNullElse(updateLocationDto.getRadius(), location.getRadius()));
        return LocationMapper.toLocationFullDto(locationRepository.save(location));
    }

    @Override
    public void deleteLocation(Long locationId) {
        locationRepository.getLocationById(locationId);
        if (eventRepository.existsEventByLocation_Id(locationId)) {
            throw new ConflictException(DELETE_LOCATION_WITH_EVENT_ERROR_MSG);
        }
        locationRepository.deleteById(locationId);
    }


}
