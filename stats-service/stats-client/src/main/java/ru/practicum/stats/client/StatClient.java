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
    List<ViewStats> getStat(@RequestParam("start") String start,
                            @RequestParam("end") String end,
                            @RequestParam("uris") List<String> uris,
                            @RequestParam("unique") boolean unique) throws FeignException;
}