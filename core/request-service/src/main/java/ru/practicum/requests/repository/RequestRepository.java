package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.Request;
import ru.practicum.requestClient.dto.RequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;


import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findByRequester(Long requesterId);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findByEvent(Long eventId);

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<Request> findAllByIdIn(List<Long> requestIds);

    long countByEventAndStatus(Long eventId, RequestStatus status);

}
