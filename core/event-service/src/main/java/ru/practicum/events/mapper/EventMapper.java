package ru.practicum.events.mapper;

import org.mapstruct.*;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.eventClient.event.dto.EventShortDto;
import ru.practicum.eventClient.event.dto.NewEventDto;
import ru.practicum.eventClient.event.dto.UpdateEventUserRequestDto;
import ru.practicum.events.model.category.Category;
import ru.practicum.events.model.event.Event;
import ru.practicum.events.model.event.Location;
import ru.practicum.userClient.user.dto.UserDto;
import ru.practicum.userClient.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "eventDate", source = "newEventDto.eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "paid", source = "newEventDto.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "newEventDto.participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "newEventDto.requestModeration", defaultValue = "true")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "confirmedRequests", constant = "0")
    @Mapping(target = "views", ignore = true)
    Event toEvent(NewEventDto newEventDto, UserDto initiator, Category category, Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true) // Состояние обновляется отдельно
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "eventDate", source = "updateRequest.eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    void updateEventFromDto(UpdateEventUserRequestDto updateRequest, Category category, Location location, @MappingTarget Event event);

    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    EventShortDto toEventShortDto(Event event);

    Event toEventFromFullDto(EventFullDto dto);


    default Long map(UserDto userDto) {
        return userDto != null ? userDto.getId() : null;
    }

    default Long map(Event event) {
        return event != null ? event.getId() : null;
    }

    default UserShortDto map(Long userId) {
        if (userId == null) return null;
        UserShortDto dto = new UserShortDto();
        dto.setId(userId);
        return dto;
    }

    default Long map(UserShortDto dto) {
        return dto != null ? dto.getId() : null;
    }
}