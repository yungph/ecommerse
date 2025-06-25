package com.ecommerce.ecommerse.Service;

import com.ecommerce.ecommerse.Models.Cart;
import com.ecommerce.ecommerse.Models.CartItem;
import com.ecommerce.ecommerse.Models.Product;
import com.ecommerce.ecommerse.Models.User; // Assuming User model is needed
import com.ecommerce.ecommerse.Repo.CartItemRepo;
import com.ecommerce.ecommerse.Repo.CartRepo;
import com.ecommerce.ecommerse.Repo.ProductRepo;
import com.ecommerce.ecommerse.Repo.UserRepo; // To fetch User
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepository;

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private CartItemRepo cartItemRepository;

    @Autowired
    private UserRepo userRepo; // To link cart to user

    @Transactional
    public Cart getCartByUserId(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        // Assuming Cart has a 'user' field or a way to find by user
        // This might be cartRepository.findByUser(user) or cartRepository.findByUserId(userId)
        // For this example, let's assume findByUser:
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user); // Assuming Cart has setUser(User user) and a User field
            // cart.setUserId(userId); // Or if Cart stores userId directly
            return cartRepository.save(cart);
        }
        return cart;
    }

    @Transactional
    public Cart addProductToCart(String userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem); // Ensure Cart's items collection is managed correctly
            cartItemRepository.save(newItem); // Save the new item
        }
        // cart.recalculateTotal(); // If you have a total field in Cart
        return cartRepository.save(cart); // Re-save cart to update any aggregate fields or version
    }

    @Transactional
    public Cart updateCartItem(String userId, Long productId, int quantity) {
        if (quantity <= 0) {
            // To remove item if quantity is 0 or less, call removeProductFromCart
            return removeProductFromCart(userId, productId);
        }
        Cart cart = getCartByUserId(userId);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product not found in cart with ID: " + productId));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        // cart.recalculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(String userId, Long productId) {
        Cart cart = getCartByUserId(userId);
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isPresent()) {
            CartItem itemToRemove = itemOpt.get();
            cart.getItems().remove(itemToRemove);
            cartItemRepository.delete(itemToRemove);
            // cart.recalculateTotal();
            return cartRepository.save(cart);
        } else {
            // Optionally, just return the cart as is, or throw exception if item must exist
            // For consistency with update, let's assume item should exist if trying to remove by specific ID
            throw new EntityNotFoundException("Product not found in cart for removal, ID: " + productId);
        }
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        // Delete all items associated with this cart
        // This assumes CartItem has a reference to Cart and cascading delete is not set up to do this automatically
        // or you want explicit control.
        Set<CartItem> itemsToDelete = cart.getItems().stream().collect(Collectors.toSet()); // Avoid ConcurrentModificationException
        for (CartItem item : itemsToDelete) {
            cart.getItems().remove(item); // Remove from association
            cartItemRepository.delete(item); // Delete from DB
        }
        // cart.recalculateTotal(); // e.g., set total to 0
        cartRepository.save(cart);
    }

    // Original createCart - might not be directly used by controller now but could be internally useful
     public Cart createCart(User user) { // If cart should be tied to a user from start
         Cart newCart = new Cart();
         newCart.setUser(user);
         return cartRepository.save(newCart);
     }
}
