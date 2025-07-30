package ru.practicum.eventClient.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LocationDto {
    @NotNull
    Float lat;
    @NotNull
    Float lon;
}