package com.grapeup.gateway.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

	final CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
	AtomicInteger successCounter = new AtomicInteger(0);
	AtomicInteger failureCounter = new AtomicInteger(0);
	AtomicInteger failureCounterForHost2 = new AtomicInteger(0);

	final static String GATEWAY_1_INSTANCE_URL = "http://localhost:9000";
	final static String GATEWAY_2_INSTANCE_URL = "http://localhost:9001";

	@Test
	public void shouldThrottleHalfOfHost2TrafficWhenHost1Send4TPSandHost2Send8TPSAndDefaultReplenishRateIs4TPS() {

		List<ScheduledExecutorService> schedulers = Stream
				.generate(() -> Executors.newScheduledThreadPool(2))
				.limit(12)
				.collect(Collectors.toList());

		schedulers.subList(0, 2).forEach(scheduler -> scheduler
				.scheduleAtFixedRate(getRequest(GATEWAY_1_INSTANCE_URL + "/teams/info", "host1"), 0, 1000, TimeUnit.MILLISECONDS));
		schedulers.subList(2, 4).forEach(scheduler -> scheduler
				.scheduleAtFixedRate(getRequest(GATEWAY_2_INSTANCE_URL + "/teams/info", "host1"), 0, 1000, TimeUnit.MILLISECONDS));
		schedulers.subList(4, 8).forEach(scheduler -> scheduler
				.scheduleAtFixedRate(getRequest(GATEWAY_1_INSTANCE_URL + "/teams/info", "host2"), 0, 1000, TimeUnit.MILLISECONDS));
		schedulers.subList(8, 12).forEach(scheduler -> scheduler
				.scheduleAtFixedRate(getRequest(GATEWAY_2_INSTANCE_URL + "/teams/info", "host2"), 0, 1000, TimeUnit.MILLISECONDS));

		try {
			cyclicBarrier.await();
			Thread.sleep(10000);
			for (int i = 0; i < schedulers.size(); i++) {
				schedulers.get(i).awaitTermination(1000, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}

		schedulers.forEach(scheduler -> scheduler.shutdownNow());
		System.out.println("Request passed: " + successCounter.get());
		System.out.println("Request failed: " + failureCounter.get());
		System.out.println("Request with host2 header failed: " + failureCounterForHost2.get());
		assertThat(failureCounterForHost2.get(), equalTo(failureCounter.get()));
	}

	public Runnable getRequest(String destinationUrl, String hostName) {
		return () -> {
			int statusCode = given()
					.when()
					.log().all()
					.header("Host", hostName)
					.get(destinationUrl)
					.then()
					.log().all()
					.extract().statusCode();
			if (statusCode == 200) {
				successCounter.getAndIncrement();
			} else {
				failureCounter.getAndIncrement();
				if(hostName.equals("host2")) {
					failureCounterForHost2.getAndIncrement();
				}
			}
		};
	}

}
