package com.ecommerce.ecommerse.Controllers;

import com.ecommerce.ecommerse.Models.Category;
import com.ecommerce.ecommerse.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Added for getAllCategories return type consistency

@RestController
@RequestMapping("/categories") // Added base path
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/create") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category newCategory = categoryService.saveCategory(category); // Assuming saveCategory returns the created category
        return ResponseEntity.ok(newCategory);
    }

    @PutMapping("/admin/update") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(category); // Assuming updateCategory returns the updated category
        return ResponseEntity.ok(updatedCategory);
    }

    @GetMapping // Path: /categories
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}") // Path: /categories/{id}
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) { // Changed to long
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/admin/delete/{id}") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) { // Changed to long
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/paginated") // Path: /categories/paginated
    public ResponseEntity<Page<Category>> getAllCategoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categories = categoryService.getAllCategoriesPaginated(page, size);
        return ResponseEntity.ok(categories);
    }
}
