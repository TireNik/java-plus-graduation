package ru.practicum.eventClient.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.eventClient.category.dto.CategoryDto;
import ru.practicum.userClient.user.dto.UserShortDto;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    String annotation;
    boolean paid;

    String title;
    String eventDate;

    CategoryDto category;
    UserShortDto initiator;

    Long confirmedRequests;
    Long views;

}