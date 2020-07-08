package com.scw.reactive;

import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ExampleClientObject {

	private final WebClient client;

	public ExampleClientObject( final WebClient client ) {
		this.client = client;
	}

	public void doProcessingInvolvingServerCalls( final Consumer<String> consumer ) {
		// Note: The media type must match or we get a 404!
		Mono<ClientResponse> result = client.get().uri( "/test1" ).accept( MediaType.TEXT_PLAIN ).exchange();
		result.flatMap( res -> res.bodyToMono( String.class ) ).subscribe( v -> gotFlux( v, consumer ) );

	}

	// Note: The string is JSON, which can include errors from e.g. 404s!
	private void gotFlux( final String value, final Consumer<String> consumer ) {
		Mono<String> result = client.get()
			.uri( uriBuilder -> uriBuilder.path( "/test2" )
				.queryParam( "doExc", // use a different parameter name to avoid exception
							 "true" )
				.build() )
			.accept( MediaType.TEXT_PLAIN )
			.retrieve()
			// Can target status code exceptions with the following code, which does not propogate the error
			// TODO - Not sure what significance the "throw" has - it doesn't seem to lead to a result or further processing?!?!
			//			.onStatus( HttpStatus::is4xxClientError, clientResp -> {
			//			Mono<String> errorMsg = clientResp.bodyToMono( String.class );
			//			consumer.accept( "ERROR: " + errorMsg );
			//				return errorMsg.flatMap( msg -> {
			//				throw new RuntimeException( msg );
			//			} );
			//				} )
			.bodyToMono( String.class );

		// Using onErrorResume, get:
		// org.springframework.web.reactive.function.client.WebClientResponseException$BadRequest: 400 Bad Request from GET http://localhost:9990/test2?doExc=true from TEST1
		// Letting the exception be handled by subscribe, (comment out the call to onErrorResume) you get:
		// ERROR2: 400 Bad Request from GET http://localhost:9990/test2?doExc=true
		result.log()
			.onErrorResume( ex -> Mono
				.just( ex.toString() ) )
			.subscribe( v -> gotFlux2( v, value, consumer ), ex -> consumer.accept( "ERROR2: " + ex.getMessage() ) );
	}

	private void gotFlux2( final String value, final String value2, final Consumer<String> consumer ) {
		consumer.accept( value + " from " + value2 );
	}

}
