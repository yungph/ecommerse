package com.ecommerce.ecommerse.Service;

import com.ecommerce.ecommerse.Models.Category;
import com.ecommerce.ecommerse.Repo.CategoryRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    CategoryRepo categoryRepo;

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(long id) { // Changed id to long
        return categoryRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
    }

    public Category saveCategory(Category category) { // Changed return type to Category
        return categoryRepo.save(category);
    }

    public Category updateCategory(Category category) { // Changed return type to Category
        // Ensure ID is not null for update
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID must not be null for update.");
        }
        Category existingCategory = categoryRepo.findById(category.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + category.getId()));

        existingCategory.setName(category.getName());
        // Update other fields if necessary
        return categoryRepo.save(existingCategory);
    }

    public void deleteCategoryById(long id) { // Changed id to long
        if (!categoryRepo.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id " + id + " for delete operation.");
        }
        categoryRepo.deleteById(id);
    }

    public Page<Category> getAllCategoriesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepo.findAll(pageable);
    }
}
