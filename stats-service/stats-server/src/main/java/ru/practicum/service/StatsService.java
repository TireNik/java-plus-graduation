package ru.practicum.service;

import ru.practicum.RequestParamDto;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatDto createHit(StatDto dto);

    List<ViewStats> getAllStats(RequestParamDto params);
}