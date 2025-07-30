package ru.practicum.events.service.compilation;

import ru.practicum.eventClient.compilation.dto.CompilationDtoRequest;
import ru.practicum.eventClient.compilation.dto.CompilationDtoResponse;
import ru.practicum.eventClient.compilation.dto.CompilationDtoUpdate;

import java.util.List;

public interface CompilationService {
    CompilationDtoResponse createCompilationAdmin(CompilationDtoRequest compilationDtoRequest);

    CompilationDtoResponse updateCompilationAdmin(CompilationDtoUpdate compilationDtoRequest, Long compId);

    void deleteCompilationAdmin(Long compId);

    CompilationDtoResponse getCompilationByIdPublic(Long compId);

    List<CompilationDtoResponse> getAllCompilationsPublic(Boolean pinned, Integer from, Integer size);
}