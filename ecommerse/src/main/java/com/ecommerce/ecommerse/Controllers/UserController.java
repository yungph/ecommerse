package com.ecommerce.ecommerse.Controllers;

import com.ecommerce.ecommerse.Models.Request; // Assuming this DTO contains 'address', 'id', 'email' as needed.
                                             // Consider creating more specific DTOs.
import com.ecommerce.ecommerse.Models.User;
import com.ecommerce.ecommerse.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users") // Changed base path for clarity
public class UserController {

    @Autowired
    private UserService userService;

    // Helper method to get current authenticated user's email (username)
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    // Endpoint to get the currently authenticated user's details
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        User user = userService.GetUserByEmail(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (#id == @userService.GetUserByEmail(authentication.principal.username)?.id)")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User userUpdateRequest) {
        // Ensure the ID in the path matches the ID in the body, or is not set in body to prevent confusion.
        // Or, primarily use the ID from the path.
        User existingUser = userService.GetUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        // Add logic to update fields from userUpdateRequest to existingUser
        // For example: existingUser.setName(userUpdateRequest.getName()); etc.
        // Handle password update separately and carefully (i.e., encode if changed).
        userService.UpdateUser(existingUser); // Assuming UpdateUser handles password encoding if necessary
        return ResponseEntity.ok("User updated successfully.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (#id == @userService.GetUserByEmail(authentication.principal.username)?.id)")
    public ResponseEntity<?> deleteUserById(@PathVariable String id) {
        User existingUser = userService.GetUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        userService.DeleteUserById(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    // This endpoint might be redundant if /users/me and /users/{id} (for admins) cover needs.
    // Kept for now based on original structure, but consider if it's needed.
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.GetUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (#id == @userService.GetUserByEmail(authentication.principal.username)?.id)")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.GetUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.GetAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/address")
    @PreAuthorize("hasRole('ADMIN') or (#id == @userService.GetUserByEmail(authentication.principal.username)?.id)")
    public ResponseEntity<?> updateAddress(@PathVariable String id, @RequestBody Request addressRequest) {
        User user = userService.GetUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Assuming Request DTO has getAddress()
        userService.UpdateAddress(id, addressRequest.getAddress());
        return ResponseEntity.ok("Address updated successfully.");
    }

    @GetMapping("/{id}/address")
    @PreAuthorize("hasRole('ADMIN') or (#id == @userService.GetUserByEmail(authentication.principal.username)?.id)")
    public ResponseEntity<String> getAddressById(@PathVariable String id) {
        User user = userService.GetUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        String address = userService.GetAddressById(id);
        if (address == null) { // Should not happen if user exists and GetAddressById is consistent
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }
}
