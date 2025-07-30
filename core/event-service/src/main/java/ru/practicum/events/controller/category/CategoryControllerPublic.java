package ru.practicum.events.controller.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.category.dto.CategoryDto;
import ru.practicum.events.service.category.CategoryService;

import java.util.List;


@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long catId) {
        log.info("Эндпоинт /categories. GET запрос на получение категории(public) с id {}.", catId);
        return new ResponseEntity<>(categoryService.getByIDCategoryPublic(catId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        log.info("Эндпоинт /categories. GET запрос на получение всех категорий(public) с параметрами from={}, size={}.",
                from, size);
        return new ResponseEntity<>(categoryService.getAllCategoriesPublic(from, size), HttpStatus.OK);
    }
}