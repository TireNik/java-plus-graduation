package ru.practicum.events.controller.event;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventShortDto;
import ru.practicum.events.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;
    private final String AuthHeaderKey = "X-EWM-USER-ID";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false)
                                         @Size(min = 1, max = 7000)
                                         String text,
                                         @RequestParam(required = false)
                                         List<Long> categories,
                                         @RequestParam(required = false)
                                         Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false")
                                         Boolean onlyAvailable,
                                         @RequestParam(required = false)
                                         String sort,
                                         @RequestParam(defaultValue = "0")
                                         @Min(value = 0)
                                         int from,
                                         @RequestParam(defaultValue = "10")
                                         int size,
                                         HttpServletRequest request) {
        return eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@RequestHeader("X-EWM-USER-ID") Long userId, @PathVariable Long eventId) {
        return eventService.getEventById(eventId, eventId);
    }


    @GetMapping("/recommendations")
    public List<EventShortDto> getEventsRecommendations(@RequestHeader(AuthHeaderKey) Long userId,
                                                        @RequestParam(defaultValue = "10") int maxResults) {
        log.info("Пришел GET запрос /events/recommendations от пользователя {} с параметром maxResults={}",
                userId, maxResults);
        List<EventShortDto> recommendations = eventService.getEventsRecommendations(userId, maxResults);
        log.info("Отправлен ответ GET /events/recommendations пользователю {} с телом: {}",
                userId, recommendations);
        return recommendations;
    }

    @PutMapping("/{eventId}/like")
    public void addLikeToEvent(@PathVariable Long eventId, @RequestHeader(AuthHeaderKey) Long userId) {
        log.info("Пришел PUT запрос /events/{}/like от пользователя {}", eventId, userId);
        eventService.addLikeToEvent(eventId, userId);
        log.info("Обработан PUT запрос /events/{}/like от пользователя {}", eventId, userId);
    }
}
