package ru.practicum.eventClient.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.eventClient.event.dto.EventFullDto;

@FeignClient(name = "event-client", contextId = "InternalEventClient", path = "/internal/event")
public interface InternalEventClient {

    @GetMapping("/{id}")
    EventFullDto getEventById(Long id);

    @PostMapping()
    void createEvent(EventFullDto eventFullDto);
}
