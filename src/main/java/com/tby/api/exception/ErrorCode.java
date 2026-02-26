package com.tby.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // System Errors (1xxxx)
    SYSTEM_ERROR(10000, "An internal server error occurred"),
    PARAM_VALIDATION_ERROR(10001, "Parameter validation failed"),
    RATE_LIMIT_EXCEEDED(10002, "Too many requests. Please try again later."),

    // User Errors (2xxxx)
    USER_NOT_FOUND(20001, "User not found"),

    // Product/Category Errors (3xxxx)
    PRODUCT_NOT_FOUND(30001, "Product not found"),
    CATEGORY_NOT_FOUND(30002, "Category not found"),

    // Order Errors (4xxxx)
    ORDER_NOT_FOUND(40001, "Order not found"),
    DUPLICATE_ORDER(40002, "Duplicate request detected"),
    UNAUTHORIZED_ACCESS(40003, "You do not have permission to access/modify this resource");

    private final int code;
    private final String message;
}
