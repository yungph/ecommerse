package com.ecommerce.ecommerse.Service;

import com.ecommerce.ecommerse.DTO.LoginRequest;
import com.ecommerce.ecommerse.Models.User; // Assuming Role and User models are correctly defined
import com.ecommerce.ecommerse.Models.Role; // Assuming you have a Role entity
import com.ecommerce.ecommerse.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; // Autowire PasswordEncoder

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.GetUserByEmail(email); // Assuming this method exists in UserRepo
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        Collection<? extends GrantedAuthority> authorities;
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName())) // Assuming Role class has getName()
                    .collect(Collectors.toList());
        } else {
            // Assign a default role if no roles are found
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }

    // Method to register a new user with password encoding
    public User registerUser(User user) {
        user.setId(UUID.randomUUID().toString().replace("-", ""));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Set default roles if necessary, e.g.,
        // if (user.getRoles() == null || user.getRoles().isEmpty()) {
        //    Role userRole = roleRepository.findByName("ROLE_USER")
        //        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        //    user.setRoles(Set.of(userRole));
        // }
        return userRepo.save(user);
    }

    // Method for user login (validation against encoded password)
    public User loginUser(LoginRequest loginRequest) {
        UserDetails userDetails = loadUserByUsername(loginRequest.getUsername()); // Use UserDetails for consistency

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid credentials"); // Or a more specific authentication exception
        }
        // It's generally better to return a DTO or just confirm success,
        // rather than the full User entity after login for security reasons.
        // However, returning User object as per previous structure.
        return userRepo.GetUserByEmail(loginRequest.getUsername());
    }

    // If you have a separate CreateUser method, ensure it also encodes passwords
    public User createUser(User user) {
        user.setId(UUID.randomUUID().toString().replace("-", ""));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public void UpdateUser(User user) {
        // When updating a user, if the password field is part of the update,
        // it should be re-encoded if it has changed.
        // This logic needs to be handled carefully, perhaps by checking if the password is new and not already encoded.
        // For example:
        // User existingUser = userRepo.findById(user.getId()).orElse(null);
        // if (existingUser != null && user.getPassword() != null && !user.getPassword().equals(existingUser.getPassword())) {
        //    if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) { // Avoid re-encoding already encoded password
        //        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //    }
        // }
        userRepo.save(user);
    }

    public void DeleteUser(User user) {
        userRepo.delete(user);
    }

    public void DeleteUserById(String id) {
        userRepo.deleteById(id);
    }

    public User GetUserByEmail(String email) {
        return userRepo.GetUserByEmail(email);
    }

    public User GetUserById(String id) {
        return userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public List<User> GetAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public void UpdateAddress(String id, String address) {
        User user = GetUserById(id); // Fetch user first
        user.setAddress(address);
        userRepo.save(user); // Save updated user
        // userRepo.updateAddress(id, address); // This custom query might bypass JPA lifecycle callbacks if any
    }

    public String GetAddressById(String id) {
        return GetUserById(id).getAddress();
    }
}
