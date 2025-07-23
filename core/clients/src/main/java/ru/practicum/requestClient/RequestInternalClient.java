package ru.practicum.requestClient;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.requestClient.dto.RequestStatus;

@FeignClient(name = "request-service", path = "/internal/requests")
public interface RequestInternalClient {

    @GetMapping("/count")
    long countConfirmedByEvent(@RequestParam("eventId") Long eventId,
                               @RequestParam("status") RequestStatus status) throws FeignException;
}