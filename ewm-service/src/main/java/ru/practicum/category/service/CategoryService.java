package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long categoryId);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getCategories(Integer from, Integer size);
}
