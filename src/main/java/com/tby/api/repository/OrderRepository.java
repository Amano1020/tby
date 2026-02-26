package com.tby.api.repository;

import com.tby.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Order o WHERE o.userId = :userId")
    void deleteByUserId(Long userId);
}
