package com.app.repository;

import com.app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Order entity.
 * Provides CRUD operations and query methods for Order data.
 * 
 * <p>
 * This interface extends JpaRepository, enabling standard data access methods
 * such as save, findById, findAll, deleteById, etc., for the Order entity.
 * No custom query methods are defined to keep the interface clean and maintainable.
 * </p>
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Default query methods provided by JpaRepository are sufficient.
}
