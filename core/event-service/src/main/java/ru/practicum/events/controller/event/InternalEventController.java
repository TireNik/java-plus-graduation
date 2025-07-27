package ru.practicum.events.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.InternalEventClient;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.events.service.event.InternalEventService;

@RestController
@RequestMapping("/internal/event")
@RequiredArgsConstructor
@Slf4j
public class InternalEventController implements InternalEventClient {

    private final InternalEventService internalEventService;

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable("id") Long id) {
        return internalEventService.getEventById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public void createEvent(@RequestBody EventFullDto eventFullDto) {
        internalEventService.createEvent(eventFullDto);
        log.info("Event created: {}", eventFullDto);
    }
}
