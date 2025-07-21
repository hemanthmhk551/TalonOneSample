package com.app.service;

import com.app.model.Order;
import com.app.model.OrderRequest;
import com.app.model.RewardsResponse;
import com.app.model.User;
import com.app.repository.OrderRepository;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for order processing.
 * Handles business logic for placing and saving orders, applying discounts, and updating user statistics.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final RewardsService rewardsService;
    private final OrderRepository orderRepository;

    /**
     * Saves an order after evaluating rewards and discounts.
     * @param orderRequest The order request from the client.
     * @param rewardsResult The evaluated rewards/discounts for the order.
     * @return The saved Order entity.
     */
    public Order saveOrder(OrderRequest orderRequest, RewardsResponse rewardsResult) {
        // Retrieve user
        User user = userService.findUserById(orderRequest.getUserId());
        if (user == null) {
            return null;
        }

        // Calculate final total after applying discounts
        double discount = rewardsResult != null ? rewardsResult.getTotalDiscount() : 0.0;
        double finalTotal = orderRequest.getTotal() - discount;

        // Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setItems(orderRequest.getItems());
        order.setTotal(finalTotal);
        order.setDiscount(discount);
        order.setStatus("PLACED");
        order.setCreatedAt(java.time.LocalDateTime.now());

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Optionally, confirm loyalty point usage after order placement
        if (rewardsResult != null && rewardsResult.isLoyaltyUsed()) {
            rewardsService.confirmLoyalty(user.getId().toString(), finalTotal);
        }

        return savedOrder;
    }

    /**
     * Places an order by evaluating rewards, saving the order, and updating user stats.
     * @param req The order request.
     * @return The saved Order entity.
     */
    public Order placeOrder(OrderRequest req) {
        // Step 1: Retrieve user
        User user = userService.findUserById(req.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + req.getUserId());
        }

        // Step 2: Evaluate discounts/rewards
        RewardsResponse rewards = rewardsService.evaluateCart(req.toCartRequest());

        // Step 3: Create and save order with applied discount
        double discount = rewards != null ? rewards.getTotalDiscount() : 0.0;
        double finalTotal = req.getTotal() - discount;

        Order order = new Order();
        order.setUser(user);
        order.setItems(req.getItems());
        order.setTotal(finalTotal);
        order.setDiscount(discount);
        order.setStatus("PLACED");
        order.setCreatedAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Step 4: Update user statistics
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setTotalSpent(user.getTotalSpent() + finalTotal);
        userService.updateUserAfterOrder(user, savedOrder, rewards);

        // Step 5: Confirm loyalty point usage if applicable
        if (rewards != null && rewards.isLoyaltyUsed()) {
            rewardsService.confirmLoyalty(user.getId().toString(), finalTotal);
        }

        return savedOrder;
    }
}
