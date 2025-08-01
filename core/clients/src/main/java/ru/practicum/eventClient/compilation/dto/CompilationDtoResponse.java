package ru.practicum.eventClient.compilation.dto;

import lombok.*;
import ru.practicum.eventClient.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDtoResponse {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
