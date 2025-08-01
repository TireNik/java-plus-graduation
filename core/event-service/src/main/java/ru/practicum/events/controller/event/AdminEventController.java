package ru.practicum.events.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.events.service.event.EventService;

import java.util.List;


@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventById(@RequestParam(required = false) List<Long> userIds,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        return eventService.getAdminEventById(userIds, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequestDto dto) {
        EventFullDto eventFullDto = eventService.updateEventAdmin(eventId, dto);
        return ResponseEntity.ok(eventFullDto);
    }
}
