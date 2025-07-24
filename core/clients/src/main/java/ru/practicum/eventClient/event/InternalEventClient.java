package ru.practicum.eventClient.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.eventClient.event.dto.EventFullDto;

@FeignClient(name = "event-client", path = "/internal/event")
public interface InternalEventClient {

    @GetMapping("/{id}")
    EventFullDto getEventById(Long id);
}
