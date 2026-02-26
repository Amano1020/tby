package com.tby.api.controller;

import com.tby.api.dto.OrderRequest;
import com.tby.api.dto.OrderResponse;
import com.tby.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.tby.api.dto.ApiResponse;
import com.tby.api.annotation.RateLimit;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @RateLimit(key = "order_create", time = 1, count = 5)
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }

    @RateLimit(key = "order_patch", time = 1, count = 10)
    @PatchMapping("/{order_id}")
    public ApiResponse<OrderResponse> patchOrder(@PathVariable("order_id") Long orderId,
            @RequestBody OrderRequest request) {
        return ApiResponse.success(orderService.patchOrder(orderId, request));
    }

    @RateLimit(key = "order_delete", time = 1, count = 10)
    @DeleteMapping("/{order_id}")
    public ApiResponse<Void> deleteOrder(@PathVariable("order_id") Long orderId, @RequestParam("userId") Long userId) {
        orderService.deleteOrder(orderId, userId);
        return ApiResponse.success();
    }

    @RateLimit(key = "order_get", time = 1, count = 50)
    @GetMapping("/{userId}")
    public ApiResponse<List<OrderResponse>> getOrdersByUser(@PathVariable("userId") Long userId) {
        return ApiResponse.success(orderService.getOrdersByUser(userId));
    }
}
