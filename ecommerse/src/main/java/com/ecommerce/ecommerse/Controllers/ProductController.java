package com.ecommerce.ecommerse.Controllers;

import com.ecommerce.ecommerse.Models.Product;
// import com.ecommerce.ecommerse.Models.Request; // Replaced with specific params or DTOs
import com.ecommerce.ecommerse.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products") // Added base path
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/add") // Changed path for consistency
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product newProduct = productService.AddProduct(product); // Assuming AddProduct returns the created product
        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("/admin/update") // Changed path for consistency
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product); // Assuming updateProduct returns the updated product
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping // Path: /products
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/paginated") // Path: /products/paginated
    public ResponseEntity<Page<Product>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Product> products = productService.getAllProductsPaginated(page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}") // Path: /products/{id}
    public ResponseEntity<Product> getProductById(@PathVariable long id) { // Changed to long for typical ID types
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/admin/deleted") // Path: /products/admin/deleted
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getDeletedProducts() {
        return ResponseEntity.ok().body(productService.getDeletedProducts());
    }

    @GetMapping("/category/{categoryName}") // Path: /products/category/{categoryName}
    public ResponseEntity<List<Product>> getProductsByCategoryName(@PathVariable String categoryName) {
        List<Product> products = productService.GetProductByCategoryName(categoryName);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/admin/delete/{id}") // Path: /products/admin/delete/{id}
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) { // Changed to long
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search") // Path: /products/search?name=...
    public ResponseEntity<List<Product>> searchProduct(@RequestParam("name") String productName) {
        List<Product> products = productService.FindByName(productName);
         if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }
}
