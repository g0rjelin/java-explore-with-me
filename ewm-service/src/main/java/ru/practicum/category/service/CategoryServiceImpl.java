package ru.practicum.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    final CategoryRepository categoryRepository;
    final EventRepository eventRepository;

    static final String DELETE_CATEGORY_WITH_EVENT_ERROR_MSG = "Нельзя удалить категорию, к которой привязано хотя бы одно событие";


    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.getCategoryById(categoryId);
        if (eventRepository.existsEventByCategory_Id(categoryId)) {
            throw new ConflictException(DELETE_CATEGORY_WITH_EVENT_ERROR_MSG);
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto update(Long categoryId, CategoryDto updCategoryDto) {
        Category category = categoryRepository.getCategoryById(categoryId);
        category.setName(updCategoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(categoryRepository.getCategoryById(categoryId));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return CategoryMapper.toCategoryDtoList(categoryRepository.findAll(page).getContent());
    }
}
