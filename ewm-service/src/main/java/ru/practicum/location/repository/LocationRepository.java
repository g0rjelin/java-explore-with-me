package ru.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, QuerydslPredicateExecutor<Location> {
    Optional<Location> findByLatAndLon(Float lat, Float lon);

    default Location getLocationById(Long locationId) {
        return findById(locationId)
                .orElseThrow(() -> new NotFoundException(locationId, Location.class.toString()));
    }
}
