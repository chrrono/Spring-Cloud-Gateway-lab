package com.grapeup.gateway.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.PrincipalNameKeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	RouteLocator routeLocatorToPeopleOpsService(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder
				.routes()
				.route(routeSpec -> routeSpec
						.path("/employee/**")
						.filters(gatewayFilterSpec -> gatewayFilterSpec
								.rewritePath("/employee/(?<id>.*)", "/employee/${id}"))
						.uri("lb://peopleops"))
				.build();
	}

	@Bean
	RouteLocator routeWithLoadBalancingToProjectEndpoint (RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder
				.routes()
				.route(routeSpec -> routeSpec
						.path("/project")
						.filters(gatewayFilterSpec -> gatewayFilterSpec
								.setPath("/project"))
						.uri("lb://production"))
				.build();
	}

	@Bean
	RouteLocator routeWithRateLimitingToTeamsEndpoint (RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder
				.routes()
				.route(routeSpec -> routeSpec
						.path("/teams/info")
						.filters(f -> f
								.requestRateLimiter(rlc -> rlc
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(remoteHostNameKeyResolver()))
								.rewritePath("/teams/info", "/teams"))
						.uri("lb://production"))
				.build();
	}

	@Bean
	RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(4,8);
	}

	@Bean
	KeyResolver remoteHostNameKeyResolver() {
		return exchange -> Mono.just(exchange
				.getRequest()
				.getHeaders()
				.getHost()
				.getHostName());
	}

//	@Bean
//	RouteLocator routeLocatorToPeopleOpsService(RouteLocatorBuilder routeLocatorBuilder) {
//		return routeLocatorBuilder
//				.routes()
//				.route(routeSpec -> routeSpec
//						.path("/employees")
//						.filters(gatewayFilterSpec -> gatewayFilterSpec
//								.rewritePath("/employees", "/employees/v2"))
//						.uri("lb://peopleops"))
//				.build();
//	}

//	@Bean
//	RouteLocator routeToMarketingService(RouteLocatorBuilder routeLocatorBuilder) {
//		return routeLocatorBuilder
//				.routes()
//				.route(routeSpec )
//				.build();
//	}

}
