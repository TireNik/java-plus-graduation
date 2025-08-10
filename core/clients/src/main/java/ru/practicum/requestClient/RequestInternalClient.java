package ru.practicum.requestClient;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.requestClient.dto.ParticipationRequestDto;
import ru.practicum.requestClient.dto.RequestStatus;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service", contextId = "requestInternalClient", path = "/internal/requests")
public interface RequestInternalClient {

    @GetMapping("/count")
    long countConfirmedByEvent(@RequestParam("eventId") Long eventId,
                               @RequestParam("status") RequestStatus status) throws FeignException;

    @GetMapping("/requests/{eventId}/check-user-confirmed/{userId}")
    boolean checkExistStatusRequest(@PathVariable Long eventId, @PathVariable Long userId,
                                    @RequestParam RequestStatus status);

    @GetMapping("/confirmed")
    Map<Long, List<ParticipationRequestDto>> getConfirmedRequests(@RequestParam List<Long> eventIds) throws FeignException;
}