package com.ecommerce.ecommerse.Controllers;

import com.ecommerce.ecommerse.Models.Cart;
import com.ecommerce.ecommerse.Service.CartService;
import com.ecommerce.ecommerse.Service.UserService; // To get user ID from principal
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// DTO for adding/updating item in cart
class CartItemRequest {
    private Long productId;
    private int quantity;

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()") // All cart operations require authentication
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService; // To resolve user ID

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName(); // This is the email
        // Assuming UserService has a method to get User by email and then get ID
        // Or UserDetails includes the user ID. For now, let's assume email is sufficient if CartService can use it.
        // A more robust way would be to have User ID in UserDetails or fetch User by email then get ID.
        // For simplicity, if CartService is adapted to use email as user identifier for cart:
        // return currentPrincipalName;
        // If CartService needs actual User ID (long/String):
        com.ecommerce.ecommerse.Models.User currentUser = userService.GetUserByEmail(currentPrincipalName);
        if (currentUser != null) {
            return currentUser.getId(); // Assuming User model has getId() returning String or Long
        }
        throw new RuntimeException("User not found for cart operations");
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        String userId = getCurrentUserId();
        Cart cart = cartService.getCartByUserId(userId); // Requires CartService.getCartByUserId(String userId)
        if (cart == null) {
            // Optionally create a cart if one doesn't exist, or return not found/empty response
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addProductToCart(@RequestBody CartItemRequest itemRequest) {
        String userId = getCurrentUserId();
        // Assumes CartService.addProductToCart(String userId, Long productId, int quantity)
        Cart updatedCart = cartService.addProductToCart(userId, itemRequest.getProductId(), itemRequest.getQuantity());
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Long productId, @RequestParam int quantity) {
        String userId = getCurrentUserId();
        // Assumes CartService.updateCartItem(String userId, Long productId, int quantity)
        Cart updatedCart = cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(@PathVariable Long productId) {
        String userId = getCurrentUserId();
        // Assumes CartService.removeProductFromCart(String userId, Long productId)
        Cart updatedCart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        String userId = getCurrentUserId();
        cartService.clearCart(userId); // Requires CartService.clearCart(String userId)
        return ResponseEntity.ok().build();
    }
}
