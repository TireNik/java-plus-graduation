package ru.practicum.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.eventClient.event.dto.EventFullDto;
import ru.practicum.requestClient.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requestClient.dto.RequestStatus;
import ru.practicum.userClient.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto toDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated().format(FORMATTER));
        dto.setEvent(request.getEvent());
        dto.setRequester(request.getRequester());
        dto.setStatus(request.getStatus().name());
        return dto;
    }

    public Request toEntity(ParticipationRequestDto dto, EventFullDto event, UserDto requester) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setCreated(dto.getCreated() != null ? LocalDateTime.parse(dto.getCreated(), FORMATTER) : null);
        request.setEvent(event.getId());
        request.setRequester(requester.getId());
        request.setStatus(RequestStatus.valueOf(dto.getStatus()));
        return request;
    }
}