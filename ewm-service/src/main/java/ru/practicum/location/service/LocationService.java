package ru.practicum.location.service;

import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.dto.UpdateLocationDto;

import java.util.List;

public interface LocationService {
    List<LocationFullDto> getLocations(Float lat, Float lon, Float radius, Integer from, Integer size);

    LocationFullDto getLocationByLocId(Long locationId);

    LocationFullDto createLocation(NewLocationDto newLocationDto);

    LocationFullDto updateLocation(Long locationId, UpdateLocationDto updateLocationDto);

    void deleteLocation(Long locationId);
}
