package com.scw.reactive.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RequestRouter {

	@Bean
	public RouterFunction<ServerResponse> route( final RequestHandler handler ) {
		return RouterFunctions.route( RequestPredicates.GET( "/hello" ).and( RequestPredicates.accept( MediaType.TEXT_PLAIN ) ),
									  handler::hello )
			.andRoute( RequestPredicates.GET( "/helloflux" ).and( RequestPredicates.accept( MediaType.APPLICATION_JSON ) ), handler::helloMany )
			.andRoute( RequestPredicates.GET( "/test1" ).and( RequestPredicates.accept( MediaType.TEXT_PLAIN ) ), handler::test1 )
			.andRoute( RequestPredicates.GET( "/test2" ).and( RequestPredicates.accept( MediaType.TEXT_PLAIN ) ), handler::test2 );
	}
}
