package ru.practicum.mapper;

import org.mapstruct.*;
import org.mapstruct.Named;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface StatMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "stringToLocalDateTime")
    Stat toEntity(StatDto statDto);

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "localDateTimeToString")
    StatDto toDto(Stat stat);

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String timestamp) {
        return LocalDateTime.parse(timestamp, FORMATTER);
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime timestamp) {
        return timestamp.format(FORMATTER);
    }
}