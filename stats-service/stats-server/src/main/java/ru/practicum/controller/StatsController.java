package ru.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RequestParamDto;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.stats.client.StatClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController implements StatClient {
    private static final Logger log = LoggerFactory.getLogger(StatsController.class);
    private final StatsService service;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public StatDto hit(@Valid @RequestBody StatDto statDto) throws FeignException {
        log.info("Эндпоинт /hit. POST запрос. Создание ногового StatDto {}.", statDto);
        return service.createHit(statDto);
    }

    @Override
    public List<ViewStats> getStat(@RequestParam("start") String start,
                                   @RequestParam("end") String end,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", required = false) boolean unique) throws FeignException {
        log.info("Эндпоинт /stats. GET запрос. Получение статистики по посещениям.");
        RequestParamDto requestParamDto = new RequestParamDto(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter), uris, unique);
        return service.getAllStats(requestParamDto);
    }
}