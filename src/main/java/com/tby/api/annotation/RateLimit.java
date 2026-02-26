package com.tby.api.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Unique key prefix for the rate limiter.
     * e.g., "order_create"
     */
    String key() default "";

    /**
     * How many requests are allowed per window.
     * Default: 5 requests.
     */
    long count() default 5;

    /**
     * Time window in seconds.
     * Default: 1 second.
     */
    long time() default 1;
}
