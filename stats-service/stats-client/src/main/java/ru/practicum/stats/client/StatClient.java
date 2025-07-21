package ru.practicum.stats.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatClient {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    StatDto hit(@Valid @RequestBody StatDto statDto) throws FeignException;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    List<ViewStats> getStat(@RequestParam(value = "start", required = false) String start,
                            @RequestParam(value = "end", required = false) String end,
                            @RequestParam(value = "uris", required = false) List<String> uris,
                            @RequestParam(value = "unique", required = false) Boolean unique)
            throws FeignException;
}