package com.tby.api.aspect;

import com.tby.api.annotation.RateLimit;
import com.tby.api.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.tby.api.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String prefix = rateLimit.key().isEmpty() ? method.getName() : rateLimit.key();
        String clientIp = getClientIp();

        // Final Key: rate_limit:order_create:192.168.1.1
        String limitKey = "rate_limit:" + prefix + ":" + clientIp;

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limitKey);

        // Initialize the rate limiter if it doesn't exist
        // RateType.OVERALL means the limit applies to all instances (distributed)
        rateLimiter.trySetRate(RateType.OVERALL, rateLimit.count(), rateLimit.time(), RateIntervalUnit.SECONDS);

        // Try to acquire 1 permit
        boolean canAccess = rateLimiter.tryAcquire(1);

        if (!canAccess) {
            log.warn("Rate limit exceeded for key: {}", limitKey);
            throw new RateLimitException("Too many requests. Please try again later.");
        }

        return joinPoint.proceed();
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.error("Failed to get client IP", e);
        }
        return "unknown";
    }
}
