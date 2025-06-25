package com.ecommerce.ecommerse.Service;

import com.ecommerce.ecommerse.Models.Product;
import com.ecommerce.ecommerse.Repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections; // For empty list
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepo productRepo;

    public Product AddProduct(Product product) {
        return productRepo.save(product);
    }

    public Product updateProduct(Product product) {
        // Ensure product exists before updating, or save handles it as upsert
        return productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(long id) { // Changed id to long
        return productRepo.findById(id).orElse(null); // Return null or throw exception if not found
    }

    public void deleteProduct(long id) { // Changed id to long
        // Assuming SoftDelete is a custom method in ProductRepo that sets a flag
        // If it's a standard delete, it would be productRepo.deleteById(id);
        // For soft delete, you might fetch, set flag, and save:
        // Optional<Product> productOpt = productRepo.findById(id);
        // productOpt.ifPresent(product -> {
        //     product.setDeleted(true); // Assuming a boolean field 'isDeleted' or similar in Product model
        //     productRepo.save(product);
        // });
        // Or if ProductRepo has a custom @Query for soft delete by ID:
        productRepo.SoftDelete(id); // Ensure this method exists and works as expected
    }

    public List<Product> getDeletedProducts() {
        return productRepo.findIsDeleted(); // Ensure this method exists
    }

    public Page<Product> getAllProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepo.findAll(pageable);
    }

    public List<Product> GetProductByCategoryName(String categoryName) {
        return productRepo.findAllByCategoryName(categoryName); // Ensure this method exists
    }

    public List<Product> FindByName(String productName) { // Changed return type to List<Product>
        // Assuming productRepo.findByName now returns List<Product>
        // If it's meant to be unique, it should return Optional<Product> and controller logic adjusted
        List<Product> products = productRepo.findByName(productName);
        return products != null ? products : Collections.emptyList();
    }
}
