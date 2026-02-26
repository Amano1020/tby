package com.tby.api.controller;

import com.tby.api.annotation.RateLimit;
import com.tby.api.dto.ApiResponse;
import com.tby.api.dto.ProductResponse;
import com.tby.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @RateLimit(key = "product_get", time = 1, count = 50)
    @GetMapping("/{product_id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable("product_id") Long productId) {
        return ApiResponse.success(productService.getProductById(productId));
    }
}
