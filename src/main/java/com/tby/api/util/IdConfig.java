package com.tby.api.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdConfig {

    @Bean
    public Snowflake snowflake() {
        // workerId and datacenterId should be configured via properties in a real
        // cluster
        return IdUtil.getSnowflake(1, 1);
    }
}
