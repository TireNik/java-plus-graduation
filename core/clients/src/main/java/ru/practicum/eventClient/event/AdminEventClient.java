package ru.practicum.eventClient.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.UpdateEventAdminRequestDto;

import java.util.List;

@FeignClient(name = "event-service", contextId = "adminEventClient", path = "/admin/events")
public interface AdminEventClient {

    @GetMapping
    List<EventFullDto> getEventById(@RequestParam(required = false) List<Long> userIds,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(name = "from", defaultValue = "0") Long from,
                                    @RequestParam(name = "size", defaultValue = "10") Long size);

    @PatchMapping("/{eventId}")
    EventFullDto updateEvent(@PathVariable Long eventId,
                             @RequestBody UpdateEventAdminRequestDto dto);
}