package ru.practicum.events.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventShortDto;
import ru.practicum.eventClient.event.dto.NewEventDto;
import ru.practicum.eventClient.event.dto.UpdateEventUserRequestDto;
import ru.practicum.events.service.event.EventService;

import java.util.List;


@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable("userId") Long userId,
                                                 @PathVariable("eventId") Long eventId,
                                                 HttpServletRequest request) {
        EventFullDto dto = eventService.privateGetUserEvent(userId, eventId, request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto newEventDto) {
        log.info("New event: {}", newEventDto);
        EventFullDto event = eventService.addEvent(userId, newEventDto);
        log.info("Event created: {}", event);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(0) int size) {
        List<EventShortDto> events = eventService.getEvents(userId, from, size);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequestDto updateRequest) {
        EventFullDto updatedEvent = eventService.updateEvent(userId, eventId, updateRequest);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<EventShortDto>> getSubscribedEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(0) int size,
            HttpServletRequest request) {
        List<EventShortDto> events = eventService.getSubscribedEvents(userId, from, size, request);
        return ResponseEntity.ok(events);
    }

}
