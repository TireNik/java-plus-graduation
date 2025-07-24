package ru.practicum.events.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.event.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}