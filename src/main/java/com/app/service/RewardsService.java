package com.app.service;

import com.app.model.CartRequest;
import com.app.model.ProfileDTO;
import com.app.model.RewardsResponse;
import com.app.model.SessionDTO;
import com.app.talonone.TalonOneClient;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for rewards and discounts.
 * Integrates with Talon.One API to evaluate and confirm rewards/discounts for users.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {

    private final TalonOneClient talonOneClient;

    /**
     * Evaluates the cart for personalized rewards and discounts via Talon.One.
     * @param req The cart request containing items and user info.
     * @return RewardsResponse containing applicable discounts and rewards.
     */
    public RewardsResponse evaluateCart(CartRequest req) {
        // Update user profile in Talon.One
        ProfileDTO profileDTO = ProfileDTO.fromCartRequest(req);
        talonOneClient.updateProfile(profileDTO);

        // Evaluate session for discounts/rewards
        SessionDTO sessionDTO = SessionDTO.fromCartRequest(req);
        RewardsResponse response = talonOneClient.evaluateSession(sessionDTO);

        return response;
    }

    /**
     * Confirms the usage of loyalty points for a user via Talon.One.
     * @param userId The user ID.
     * @param total The total amount for which loyalty points are being confirmed.
     */
    public void confirmLoyalty(String userId, double total) {
        talonOneClient.confirmLoyalty(userId, total);
    }

    /**
     * Evaluates rewards for an order request.
     * @param orderRequest The order request.
     * @return RewardsResponse containing discounts and rewards.
     */
    public RewardsResponse evaluateRewards(com.app.model.OrderRequest orderRequest) {
        CartRequest cartRequest = orderRequest.toCartRequest();
        return evaluateCart(cartRequest);
    }
}
