package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.eventClient.event.dto.LocationDto;
import ru.practicum.events.model.event.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto locationDto);

    LocationDto toLocationDto(Location location);
}