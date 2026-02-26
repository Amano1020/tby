package com.tby.api.service;

import com.tby.api.repository.OrderRepository;
import com.tby.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        orderRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
