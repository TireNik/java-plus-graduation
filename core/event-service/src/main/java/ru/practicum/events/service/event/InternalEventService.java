package ru.practicum.events.service.event;

import ru.practicum.eventClient.event.dto.EventFullDto;

public interface InternalEventService {
    EventFullDto getEventById(Long id);
}
