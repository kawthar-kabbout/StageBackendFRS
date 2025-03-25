package com.stage.services;

import com.stage.persistans.CategoryType;
import com.stage.repositories.CategoryTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return categoryTypeRepository.save(categoryType);
    }

}
