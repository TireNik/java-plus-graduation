package ru.practicum.events.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.compilation.dto.CompilationDtoRequest;
import ru.practicum.eventClient.compilation.dto.CompilationDtoResponse;
import ru.practicum.eventClient.compilation.dto.CompilationDtoUpdate;
import ru.practicum.events.service.compilation.CompilationService;


@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDtoResponse> create(
            @RequestBody @Valid CompilationDtoRequest compilationRequestDto) {
        log.info("Эндпоинт /admin/compilations. POST запрос на создание админом новой подборки {}.",
                compilationRequestDto);
        return new ResponseEntity<>(compilationService.createCompilationAdmin(compilationRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDtoResponse> update(
            @RequestBody @Valid CompilationDtoUpdate compilationUpdateDto,
            @PathVariable Long compId) {
        log.info("Эндпоинт /admin/compilations/{}. PATCH запрос на обновление админом подборки на {}.",
                compId, compilationUpdateDto);
        return new ResponseEntity<>(compilationService.updateCompilationAdmin(compilationUpdateDto, compId), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<?> delete(@PathVariable Long compId) {
        compilationService.deleteCompilationAdmin(compId);
        log.info("Эндпоинт /admin/compilations/{}. DELETE запрос  на удаление админом подборки с id {}.", compId,
                compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}