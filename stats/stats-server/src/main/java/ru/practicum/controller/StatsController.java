package ru.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;
import ru.practicum.service.StatsService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class StatsController implements StatsClient {
    private final StatsService service;

    @Override
    public void hit(StatDto statDto) throws FeignException {
        log.info("Эндпоинт /hit. POST запрос. Создание ногового StatDto {}.", statDto);
        service.createHit(statDto);
    }

    @Override
    public List<ViewStats> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) throws FeignException {
        log.info("Эндпоинт /stats. GET запрос. Получение статистики по посещениям.");
        return service.getAllStats(start, end, uris, unique);
    }
}