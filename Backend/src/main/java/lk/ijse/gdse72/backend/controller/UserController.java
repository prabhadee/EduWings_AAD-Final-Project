package lk.ijse.gdse72.backend.controller;

import lk.ijse.gdse72.backend.dto.UserDTO;
import lk.ijse.gdse72.backend.entity.User;
import lk.ijse.gdse72.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getNumber(),
                        user.getRole() != null ? user.getRole().name() : null
                ))
                .toList();

        return ResponseEntity.ok(users);
    }


    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Getting current user for: {}", userDetails != null ? userDetails.getUsername() : "null");

        if (userDetails == null) {
            logger.warn("Unauthorized access attempt to /current");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());
            logger.info("Found user: {}", user.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error fetching current user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Update request for user ID: {}", id);
        logger.info("Authenticated user: {}", userDetails.getUsername());
        logger.info("Update data - Username: {}, Email: {}, Number: {}",
                updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getNumber());

        try {
            // Get the current authenticated user
            User currentUser = userService.findByUsername(userDetails.getUsername());

            // Check if the authenticated user is updating their own profile
            if (!currentUser.getId().equals(id)) {
                logger.warn("Forbidden: User {} cannot update user {}", userDetails.getUsername(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Update the user
            currentUser.setUsername(updatedUser.getUsername());
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setNumber(updatedUser.getNumber());

            User savedUser = userService.save(currentUser);
            logger.info("User updated successfully: {}", savedUser.getUsername());
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}