package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.UserAction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    List<UserAction> findAllByEventIdInAndUserId(Set<Long> viewedEvents, Long userId);

    List<UserAction> findAllByEventIdIn(Set<Long> eventIds);

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}

