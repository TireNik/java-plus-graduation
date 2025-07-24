package ru.practicum.eventClient.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.eventClient.category.dto.CategoryDto;

import java.util.List;

@FeignClient(name = "category-service", path = "/categories")
public interface CategoryPublicClient {

    @GetMapping("/{catId}")
    CategoryDto getById(@PathVariable("catId") Long catId);

    @GetMapping
    List<CategoryDto> getAll(@RequestParam(defaultValue = "0") Integer from,
                             @RequestParam(defaultValue = "10") Integer size);
}
