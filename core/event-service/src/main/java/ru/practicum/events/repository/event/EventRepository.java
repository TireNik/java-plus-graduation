package ru.practicum.events.repository.event;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.event.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Optional<Event> findByIdAndInitiator(Long eventId, Long userId);

    @EntityGraph(attributePaths = {"initiator", "category", "location"})
    List<Event> findByInitiator(Long userId, Pageable pageable);

    List<Event> findEventsByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"initiator", "category", "location"})
    Page<Event> findAll(@NonNull Specification<Event> spec, @NonNull Pageable pageable);

    List<Event> findByCategoryId(Long catId);
}