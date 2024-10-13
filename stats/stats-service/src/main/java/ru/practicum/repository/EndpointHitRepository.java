package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.Instant;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = """
                SELECT
                    new ru.practicum.model.ViewStats(
                        a.name,
                        u.name,
                        case
                            when :unique = true then COUNT (DISTINCT eh.ip)
                            else COUNT (eh.ip)
                        end)
                FROM EndpointHit eh
                JOIN eh.app a
                JOIN eh.uri u
                WHERE eh.timestamp >= :start AND eh.timestamp <= :end
                    AND (:uris IS NULL OR u.name IN :uris)
                GROUP BY a.name, u.name
                ORDER BY case
                            when :unique = true then COUNT (DISTINCT eh.ip)
                            else COUNT (eh.ip)
                        end DESC
            """)
    List<ViewStats> findViewStats(@Param("start") Instant start,
                                  @Param("end") Instant end,
                                  @Param("uris") List<String> uris,
                                  @Param("unique") Boolean unique);
}
