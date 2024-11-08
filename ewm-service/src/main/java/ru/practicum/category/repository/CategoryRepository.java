package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.exception.NotFoundException;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByName(String name);

    default Category getCategoryById(Long categoryId) {
        return findById(categoryId)
                .orElseThrow(() -> new NotFoundException(categoryId, Category.class.toString()));
    }
}
