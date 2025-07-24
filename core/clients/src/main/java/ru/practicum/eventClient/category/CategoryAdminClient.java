package ru.practicum.eventClient.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventClient.category.dto.CategoryDto;
import ru.practicum.eventClient.category.dto.CategoryDtoNew;

@FeignClient(name = "category-service", path = "/admin/categories")
public interface CategoryAdminClient {

    @PostMapping
    CategoryDto create(@RequestBody CategoryDtoNew newCategoryDto);

    @PatchMapping("/{catId}")
    CategoryDto update(@RequestBody CategoryDtoNew newCategoryDto,
                       @PathVariable("catId") Long catId);

    @DeleteMapping("/{catId}")
    void delete(@PathVariable("catId") Long catId);
}