package com.tby.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_user", columnList = "userId"),
        @Index(name = "idx_product", columnList = "productId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer orderAmount;
}
