package ru.practicum.events.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.eventClient.event.InternalEventClient;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.events.service.event.InternalEventService;

@RestController
@RequestMapping("/internal/event")
@RequiredArgsConstructor
public class InternalEventController implements InternalEventClient {

    private final InternalEventService internalEventService;
    @Override
    public EventFullDto getEventById(Long id) {
        return internalEventService.getEventById(id);
    }
}
