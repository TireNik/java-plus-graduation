package ru.practicum.requests.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.requestClient.RequestInternalClient;
import ru.practicum.requestClient.dto.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;

@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class RequestInternalController implements RequestInternalClient {
    private final RequestRepository requestRepository;

    @GetMapping("/count")
    @Override
    public long countConfirmedByEvent(@RequestParam("eventId") Long eventId,
                                      @RequestParam("status") RequestStatus status) throws FeignException {
        return requestRepository.countByEventAndStatus(eventId, status);
    }
}
