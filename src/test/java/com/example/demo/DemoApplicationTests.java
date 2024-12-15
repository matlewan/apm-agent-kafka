package com.example.demo;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
@EmbeddedKafka
class DemoApplicationTests {

	@Test
	void contextLoads() throws Exception {
		ElasticApmAttacher.attach();
		new CountDownLatch(1).await();
	}

}
