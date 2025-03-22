package com.stage.services;

import com.stage.persistans.CategoryType;
import com.stage.repositories.CategoryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryTypeService {
    private final CategoryTypeRepository categoryTypeRepository;


    public List<CategoryType> findAll() {
        return categoryTypeRepository.findAll();
    }
    public CategoryType findById(Long id) {
        return categoryTypeRepository.findById(id).orElse(null);
    }
    public CategoryType save(CategoryType categoryType) {
        return categoryTypeRepository.save(categoryType);
    }

    public void delete(CategoryType categoryType) {
        categoryTypeRepository.delete(categoryType);
    }

    public CategoryType update(CategoryType categoryType) {
        CategoryType oldCategoryType = categoryTypeRepository.findById(categoryType.getId()).get();
        if (oldCategoryType != null) {
            oldCategoryType.setCategoryType(categoryType.getCategoryType());
            categoryTypeRepository.save(oldCategoryType);
        }
        return oldCategoryType;
    }
}
