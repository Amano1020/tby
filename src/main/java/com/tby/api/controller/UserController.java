package com.tby.api.controller;

import com.tby.api.annotation.RateLimit;
import com.tby.api.dto.ApiResponse;
import com.tby.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @RateLimit(key = "user_delete", time = 1, count = 10)
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
