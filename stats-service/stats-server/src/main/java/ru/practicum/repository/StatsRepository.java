package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStats;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stat, Long> {

    @Query(value = "select new ru.practicum.ViewStats(st.app, st.uri, count(st.ip) as cnt) " +
            "from Stat st " +
            "where st.timestamp >= :start and st.timestamp <= :end " +
            "group by st.uri, st.app " +
            "order by cnt desc")
    List<ViewStats> getAllStats(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.ViewStats(st.app, st.uri, count(distinct(st.ip)) as cnt) " +
            "from Stat st " +
            "where st.timestamp >= :start and st.timestamp <= :end " +
            "group by st.uri, st.app " +
            "order by cnt desc")
    List<ViewStats> getAllStatsUniqueIp(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);


    @Query(value = "select new ru.practicum.ViewStats(st.app, st.uri, count(st.ip) as cnt) " +
            "from Stat st " +
            "where st.timestamp >= :start and st.timestamp <= :end and st.uri in :uris " +
            "group by st.uri, st.app " +
            "order by cnt desc")
    List<ViewStats> getStats(@Param("uris") List<String> uris,
                                @Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query(value = "select new ru.practicum.ViewStats(st.app, st.uri, count(distinct(st.ip)) as cnt) " +
            "from Stat st " +
            "where st.timestamp >= :start and st.timestamp <= :end and st.uri in :uris " +
            "group by st.uri, st.app " +
            "order by cnt desc")
    List<ViewStats> getStatsUniqueIp(@Param("uris") List<String> uris,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}
