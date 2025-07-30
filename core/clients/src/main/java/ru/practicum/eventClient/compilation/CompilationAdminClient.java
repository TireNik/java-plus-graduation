package ru.practicum.eventClient.compilation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.compilation.dto.CompilationDtoRequest;
import ru.practicum.eventClient.compilation.dto.CompilationDtoResponse;
import ru.practicum.eventClient.compilation.dto.CompilationDtoUpdate;

@FeignClient(name = "event-service", contextId = "compilationAdminRequestClient", path = "/admin/compilations")
public interface CompilationAdminClient {

    @PostMapping
    CompilationDtoResponse create(@RequestBody CompilationDtoRequest request);

    @PatchMapping("/{compId}")
    CompilationDtoResponse update(@RequestBody CompilationDtoUpdate updateDto,
                                  @PathVariable("compId") Long compId);

    @DeleteMapping("/{compId}")
    void delete(@PathVariable("compId") Long compId);
}