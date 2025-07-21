package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.RequestParamDto;
import ru.practicum.StatDto;
import ru.practicum.ViewStats;
import ru.practicum.error.exceptions.ValidationException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsServiceImpl.class);
    private final StatsRepository statsRepository;
    private final StatMapper statsMapper;

    @Transactional
    @Override
    public StatDto createHit(StatDto dto) {
        Stat stat = statsMapper.toEntity(dto);
        Stat savedStat = statsRepository.save(stat);
        return statsMapper.toDto(savedStat);
    }

    @Override
    public List<ViewStats> getAllStats(RequestParamDto params) {
        if (params.getUnique() == null) {
            params.setUnique(false);
        }
        if (params.getStart().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Время начала не может быть в прошлом");
        }


        List<ViewStats> statsToReturn;

        boolean paramsIsNotExists = params.getUris() == null || params.getUris().isEmpty();

        if (!params.getUnique()) {
            if (paramsIsNotExists) {
                statsToReturn = statsRepository.getAllStats(params.getStart(), params.getEnd());
            } else {
                statsToReturn = statsRepository.getStats(params.getUris(), params.getStart(), params.getEnd());
            }
        } else {
            if (paramsIsNotExists) {
                statsToReturn = statsRepository.getAllStatsUniqueIp(params.getStart(), params.getEnd());
            } else {
                statsToReturn = statsRepository.getStatsUniqueIp(params.getUris(), params.getStart(), params.getEnd());
            }
        }

        log.info("Статистика получена из базы данных", statsToReturn);
        return statsToReturn;
    }
}