package com.app.talonone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * TalonOneClient is a reusable and centralized client for interacting with the Talon.One Integration API.
 * It handles HTTP communication, authentication, and provides methods for updating user profiles,
 * evaluating sessions for rewards, and confirming loyalty points.
 *
 * <p>
 * Usage example:
 * <pre>
 *     @Autowired
 *     private TalonOneClient talonOneClient;
 *
 *     talonOneClient.updateProfile("user123", profileDTO);
 *     RewardsResponse rewards = talonOneClient.evaluateSession(sessionDTO);
 *     talonOneClient.confirmLoyalty("user123", 99.99);
 * </pre>
 * </p>
 *
 * <p>
 * Configuration properties required in application.properties:
 * <ul>
 *     <li>talonone.base-url=https://your.talon.one/api</li>
 *     <li>talonone.api-key=YOUR_API_KEY</li>
 * </ul>
 * </p>
 */
@Component
public class TalonOneClient {

    @Value("${talonone.base-url}")
    private String baseUrl;

    @Value("${talonone.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    /**
     * Constructs a TalonOneClient with a provided RestTemplate.
     * @param restTemplate the RestTemplate to use for HTTP requests
     */
    public TalonOneClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Updates a user profile in Talon.One.
     *
     * @param userId the unique identifier of the user
     * @param dto the profile data to update
     * @throws IllegalArgumentException if userId or dto is null
     * @throws RestClientException if the Talon.One API call fails
     */
    public void updateProfile(String userId, com.app.model.ProfileDTO dto) {
        if (userId == null || dto == null) {
            throw new IllegalArgumentException("userId and dto must not be null");
        }
        String url = String.format("%s/v1/profiles/%s", baseUrl, userId);
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<com.app.model.ProfileDTO> request = new HttpEntity<>(dto, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Log or handle specific HTTP errors as needed
            throw new RestClientException("Failed to update profile in Talon.One: " + ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * Evaluates a session in Talon.One to determine applicable rewards and discounts.
     *
     * @param dto the session data to evaluate
     * @return RewardsResponse containing rewards and discounts
     * @throws IllegalArgumentException if dto is null
     * @throws RestClientException if the Talon.One API call fails
     */
    public com.app.model.RewardsResponse evaluateSession(com.app.model.SessionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("SessionDTO must not be null");
        }
        String url = String.format("%s/v1/sessions", baseUrl);
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<com.app.model.SessionDTO> request = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<com.app.model.RewardsResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, com.app.model.RewardsResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new RestClientException("Failed to evaluate session in Talon.One: " + ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * Confirms the usage of loyalty points for a user in Talon.One.
     *
     * @param userId the unique identifier of the user
     * @param totalAmount the total amount for which loyalty points are being confirmed
     * @throws IllegalArgumentException if userId is null
     * @throws RestClientException if the Talon.One API call fails
     */
    public void confirmLoyalty(String userId, double totalAmount) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        String url = String.format("%s/v1/loyalty/%s/confirm", baseUrl, userId);
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Assuming the API expects a JSON body like: {"totalAmount": ...}
        String body = String.format("{\"totalAmount\": %.2f}", totalAmount);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, request, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new RestClientException("Failed to confirm loyalty in Talon.One: " + ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * Creates HTTP headers with the Authorization header for Talon.One API.
     * @return HttpHeaders with the API key set
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "ApiKey-v1 " + apiKey);
        return headers;
    }
}
