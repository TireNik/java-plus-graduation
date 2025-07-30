package ru.practicum.requestClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requestClient.dto.ParticipationRequestDto;
import ru.practicum.requestClient.dto.RequestUpdateDto;
import ru.practicum.requestClient.dto.RequestUpdateResultDto;

import java.util.List;

@FeignClient(name = "request-service", contextId = "requestClient", path = "/users")
public interface RequestClient {

    @GetMapping("/{userId}/requests")
    List<ParticipationRequestDto> getUserRequests(@PathVariable("userId") Long userId);

    @PostMapping("/{userId}/requests")
    ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
                                          @RequestParam("eventId") Long eventId);

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                          @PathVariable("requestId") Long requestId);

    @GetMapping("/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId);

    @PatchMapping("/{userId}/events/{eventId}/requests")
    RequestUpdateResultDto updateEventRequests(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId,
                                               @RequestBody RequestUpdateDto updateDto);
}