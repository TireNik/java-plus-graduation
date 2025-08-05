package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    @Query("SELECT es FROM EventSimilarity es WHERE es.eventA IN :eventIds OR es.eventB IN :eventIds ORDER BY es.score DESC")
    List<EventSimilarity> findAllByEventAInOrEventBIn(
            @Param("eventIds") Set<Long> eventIdsA,
            @Param("eventIds") Set<Long> eventIdsB,
            Pageable pageable);

    @Query("SELECT es FROM EventSimilarity es WHERE es.eventA = :eventId OR es.eventB = :eventId ORDER BY es.score DESC")
    List<EventSimilarity> findAllByEventAOrEventB(
            @Param("eventId") Long eventIdA,
            @Param("eventId") Long eventIdB,
            Pageable pageable);
}
