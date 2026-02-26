package com.tby.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {
    @Id
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal taxRate;
}
