package com.tby.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@SpringBootTest
class ApiApplicationTests {

	@MockitoBean
	private RocketMQTemplate rocketMQTemplate;

	@Test
	void contextLoads() {
	}

}
