package com.scw.reactive.spring;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RequestHandler {

	public Mono<ServerResponse> hello( final ServerRequest request ) {
		return ServerResponse.ok().contentType( MediaType.TEXT_PLAIN ).body( BodyInserters.fromValue( "Hello, Spring!" ) );
	}

	public Mono<ServerResponse> helloMany( final ServerRequest request ) {
		Flux<String> retVals = Flux.fromArray( new String[] { "Hello", "There", "More" } );
		return ServerResponse.ok().contentType( MediaType.APPLICATION_JSON ).body( retVals, String.class );
	}

	public Mono<ServerResponse> helloManyAsync( final ServerRequest request ) {
		Flux<String> retVals = Flux.fromArray( new String[] { "Hello", "There", "More" } );
		return ServerResponse.ok().contentType( MediaType.APPLICATION_JSON ).body( retVals, String.class );
	}

	public Mono<ServerResponse> test1( final ServerRequest request ) {
		return ServerResponse.ok().contentType( MediaType.TEXT_PLAIN ).body( BodyInserters.fromValue( "TEST1" ) );
	}

	public Mono<ServerResponse> test2( final ServerRequest request ) {
		Mono<String> response = request.queryParam( "doExc" ).isEmpty() ? Mono.just( "TEST2" ) : Mono.error( new Exception( "TEST2 ERROR" ) );
		return response.flatMap( data -> ServerResponse.ok().contentType( MediaType.TEXT_PLAIN ).bodyValue( data ) )
			.onErrorResume( error -> ServerResponse.badRequest().build() );
	}
}
