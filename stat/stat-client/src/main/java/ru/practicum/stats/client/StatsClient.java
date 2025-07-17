package ru.practicum.stats.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    void hit(@Valid @RequestBody StatDto statDto) throws FeignException;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    List<ViewStats> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                            @RequestParam(required = false) List<String> uris,
                            @RequestParam(defaultValue = "false") boolean unique) throws FeignException;

}
