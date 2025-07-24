package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.eventClient.compilation.dto.CompilationDtoRequest;
import ru.practicum.eventClient.compilation.dto.CompilationDtoResponse;
import ru.practicum.eventClient.event.dto.EventShortDto;
import ru.practicum.events.model.compilation.Compilation;
import ru.practicum.events.model.event.Event;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    @Mapping(target = "events", source = "eventDTOs")
    CompilationDtoResponse toCompilationDto(Compilation compilation, List<EventShortDto> eventDTOs);

    @Mapping(target = "events", source = "events")
    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(CompilationDtoRequest compilationDtoRequest, Set<Event> events);
}