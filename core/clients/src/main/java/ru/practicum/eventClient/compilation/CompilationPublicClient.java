package ru.practicum.eventClient.compilation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.eventClient.compilation.dto.CompilationDtoResponse;

import java.util.List;

@FeignClient(name = "event-service", path = "/compilations")
public interface CompilationPublicClient {

    @GetMapping("/{compId}")
    CompilationDtoResponse getById(@PathVariable("compId") Long compId);

    @GetMapping
    List<CompilationDtoResponse> getAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size);
}