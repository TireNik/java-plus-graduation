package ru.practicum.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.RequestParamDto;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;
import ru.practicum.error.exceptions.ValidationException;
import ru.practicum.service.StatsService;
import ru.practicum.stats.client.StatClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatsController implements StatClient {
    private static final Logger log = LoggerFactory.getLogger(StatsController.class);
    private final StatsService service;

    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );


    @Override
    public StatDto hit(@Valid @RequestBody StatDto statDto) throws FeignException {
        log.info("Эндпоинт /hit. POST запрос. Создание ногового StatDto {}.", statDto);
        return service.createHit(statDto);
    }

    @Override
    public List<ViewStats> getStat(@RequestParam(value = "start", required = false) String start,
                                   @RequestParam(value = "end", required = false) String end,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", required = false) Boolean unique) throws FeignException {
        if (start == null || end == null) {
            throw new ValidationException("Не указан диапазон дат");
        }
        log.info("Эндпоинт /stats. GET запрос. Получение статистики по посещениям.");
        RequestParamDto requestParamDto = new RequestParamDto(
                parse(start),
                parse(end),
                uris,
                unique
        );
        return service.getAllStats(requestParamDto);
    }

    private LocalDateTime parse(String value) {
        for (DateTimeFormatter fmt : SUPPORTED_FORMATS) {
            try {
                return LocalDateTime.parse(value, fmt);
            } catch (DateTimeParseException ignored) {}
        }
        throw new IllegalArgumentException("Неверный формат даты: " + value);
    }
}