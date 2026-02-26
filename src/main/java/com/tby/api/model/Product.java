package com.tby.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_category", columnList = "categoryId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private Long productId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
