package ru.practicum.eventClient.event;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventShortDto;
import ru.practicum.eventClient.event.dto.NewEventDto;
import ru.practicum.eventClient.event.dto.UpdateEventUserRequestDto;

import java.util.List;

@FeignClient(name = "event-service", path = "/users/{userId}/events")
public interface PrivateEventClient {

    @GetMapping("/{eventId}")
    EventFullDto getEvent(@PathVariable("userId") Long userId,
                          @PathVariable("eventId") Long eventId,
                          @RequestHeader("X-Forwarded-For") String ip);

    @PostMapping
    EventFullDto addEvent(@PathVariable("userId") Long userId,
                          @RequestBody @Valid NewEventDto newEventDto);

    @GetMapping
    List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size);

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(@PathVariable("userId") Long userId,
                             @PathVariable("eventId") Long eventId,
                             @RequestBody @Valid UpdateEventUserRequestDto updateRequest);

    @GetMapping("/subscriptions")
    List<EventShortDto> getSubscribedEvents(@PathVariable("userId") Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestHeader("X-Forwarded-For") String ip);
}