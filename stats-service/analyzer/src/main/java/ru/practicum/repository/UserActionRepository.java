package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.UserAction;

import java.util.List;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    @Query("select distinct ua.eventId from UserAction ua where ua.userId in :userIds")
    Set<Long> findAllEventIdsByUserId(@Param("userIds") Long userId);

    List<UserAction> findAllByEventIdInAndUserId(Set<Long> viewedEvents, Long userId);
}
