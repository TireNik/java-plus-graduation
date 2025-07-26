package ru.practicum.events.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(Long id) {
        return internalEventService.getEventById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public void createEvent(EventFullDto eventFullDto) {
        internalEventService.createEvent(eventFullDto);
        log.info("Event created: {}", eventFullDto);
    }
}
