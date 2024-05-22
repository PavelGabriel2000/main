package com.pavel.microservices.apigateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(
						p -> p.path("/get")
						.filters(
								f -> f
								.addRequestHeader("MyHeader", "MyURI")
								.addRequestParameter("MyParam", "MyValue")
								)
							.uri("http://httpbin.org:80")
							)
				.route(
						p -> p.path("/currency-exchange/**")
						.uri("lb://currency-exchange")
						)
				.route(
						p -> p.path("/currency-conv/**")
						.uri("lb://currency-conversion")
						)
				.route(
						p -> p.path("/currency-conv/feign/**")
						.uri("lb://currency-conversion")
						)
				.route(
						p -> p.path("/currency-conv-new/**")
						.filters(
								f -> f.rewritePath("/currency-conv-new/(?<segment>.*)", "/currency-conv/feign/${segment}")
								)
						.uri("lb://currency-conversion")
						)
				.build();
	}

}
