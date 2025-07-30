package ru.practicum.eventClient.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.eventClient.event.dto.EventFullDto;

@FeignClient(name = "event-service", contextId = "InternalEventClient", path = "/internal/event")
public interface InternalEventClient {

    @GetMapping("/{id}")
    EventFullDto getEventById(@PathVariable("id")Long id);

    @PostMapping()
    void createEvent(@RequestBody EventFullDto eventFullDto);
}
