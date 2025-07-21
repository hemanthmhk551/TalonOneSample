package com.app.controller;

import com.app.model.OrderRequest;
import com.app.model.OrderResponse;
import com.app.service.OrderService;
import com.app.service.RewardsService;
import com.app.service.UserService;
import jakarta.validation.Valid;
lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling order-related endpoints.
 * Exposes POST /orders to process new orders, evaluate rewards, and update user data.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RewardsService rewardsService;
    private final UserService userService;

    /**
     * Handles the creation of a new order.
     * 1. Validates the incoming OrderRequest.
     * 2. Evaluates rewards for the order using RewardsService.
     * 3. Persists the order using OrderService.
     * 4. Updates the user using UserService.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            // Step 1: Validate user existence
            var user = userService.findUserById(orderRequest.getUserId());
            if (user == null) {
                return buildNotFound("User not found with id: " + orderRequest.getUserId());
            }

            // Step 2: Evaluate rewards/discounts for the order
            var rewardsResult = rewardsService.evaluateRewards(orderRequest);

            // Step 3: Save the order
            var savedOrder = orderService.saveOrder(orderRequest, rewardsResult);
            if (savedOrder == null) {
                return buildNotFound("Order could not be saved.");
            }

            // Step 4: Update user (e.g., loyalty points, order history)
            userService.updateUserAfterOrder(user, savedOrder, rewardsResult);

            // Step 5: Build response
            OrderResponse response = new OrderResponse(savedOrder, rewardsResult);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (MethodArgumentNotValidException | BindException ex) {
            return buildBadRequest("Validation failed: " + ex.getMessage());
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else if (ex instanceof BindException be) {
            be.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else {
            errors.put("error", "Validation error");
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return buildNotFound(ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> buildBadRequest(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }

    private ResponseEntity<Map<String, String>> buildNotFound(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
