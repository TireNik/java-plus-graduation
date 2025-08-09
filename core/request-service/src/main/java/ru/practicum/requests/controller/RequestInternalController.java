package ru.practicum.requests.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requestClient.RequestInternalClient;
import ru.practicum.requestClient.dto.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.requests.service.RequestService;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class RequestInternalController implements RequestInternalClient {
    private final RequestRepository requestRepository;
    private final RequestService requestService;

    @GetMapping("/count")
    @Override
    public long countConfirmedByEvent(@RequestParam("eventId") Long eventId,
                                      @RequestParam("status") RequestStatus status) throws FeignException {
        return requestRepository.countByEventAndStatus(eventId, status);
    }

    @GetMapping("/{eventId}/check-user-confirmed/{userId}")
    public boolean checkExistStatusRequest(@PathVariable Long eventId,@PathVariable Long userId,
                                                               @RequestParam RequestStatus status) {
        return requestService.checkExistsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }
}
