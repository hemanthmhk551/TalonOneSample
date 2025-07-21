package com.app.service;

import com.app.model.User;
import com.app.model.UserDTO;
import com.app.model.UserStatsUpdateRequest;
import com.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service layer for user management.
 * Handles business logic related to user retrieval and statistics updates.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return The UserDTO representing the user.
     * @throws NoSuchElementException if the user does not exist.
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        return UserDTO.fromEntity(user);
    }

    /**
     * Updates the user's statistics (totalOrders, totalSpent).
     * @param id The ID of the user.
     * @param request The request containing updated stats.
     * @throws NoSuchElementException if the user does not exist.
     */
    public void updateUserStats(Long id, UserStatsUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        user.setTotalOrders(request.getTotalOrders());
        user.setTotalSpent(request.getTotalSpent());
        userRepository.save(user);
    }

    /**
     * Finds a user by ID, returns null if not found.
     * @param id The user ID.
     * @return The User entity or null.
     */
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Updates user after a successful order, e.g., incrementing order count, updating spent, loyalty, etc.
     * @param user The user entity.
     * @param order The order entity.
     * @param rewardsResult The rewards response.
     */
    public void updateUserAfterOrder(User user, com.app.model.Order order, com.app.model.RewardsResponse rewardsResult) {
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setTotalSpent(user.getTotalSpent() + order.getTotal());
        // Optionally update loyalty points or other fields based on rewardsResult
        userRepository.save(user);
    }
}
