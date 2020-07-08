package com.scw.reactive;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Entry {

	private static int totalValues = 0;


	public static void main( final String[] args ) {
		if ( args.length > 0 ) {
			final Collection<Boolean> valuesRead = new ArrayList<>();
			// Using object with client call
			// TODO: Use real data structures and JSON
			WebClient client = WebClient.create( "http://localhost:9990" );
			ExampleClientObject obj = new ExampleClientObject( client );
			obj.doProcessingInvolvingServerCalls( ( v ) -> {
				System.out.println( "RESULT: " + v );
				valuesRead.add( true );
			} );
			while ( valuesRead.isEmpty() ) {
				try {
					Thread.sleep( 100 );
				}
				catch ( InterruptedException l_ex ) {
				}
			}
		}
		else {
			runTest();
		}
		System.exit( 0 );
	}

	public static void runTest() {
		WebClient client = WebClient.create( "http://localhost:9990" );

		// Call the first method
		Mono<ClientResponse> result = client.get().uri( "/hello" ).accept( MediaType.TEXT_PLAIN ).exchange();

		// Test only
		// "When you call Mono.block() [or any other "extractor" methods] you throw away all the benefits of the Reactive Streams"
		System.out.println( ">> result = " + result.flatMap( res -> res.bodyToMono( String.class ) ).block() );

		// Call the second method
		result = client.get().uri( "/helloflux" ).accept( MediaType.APPLICATION_JSON ).exchange();

		System.out.println( ">> result = " + result.flatMap( res -> res.bodyToMono( String.class ) ).subscribe( Entry::gotFlux ) );

		// Call the third method
		// Note that Flux is an alternative to java.util.Stream, and we cannot use them interchangably
		// Alternative to TEXT_EVENT_STREAM  is APPLICATION_STREAM_JSON
		// Retrieve is shortcut for exchange
		Flux<String> resultFlux = client.get().uri( "/hellofluxasync" ).accept( MediaType.TEXT_EVENT_STREAM ).retrieve().bodyToFlux( String.class );

		// There are many options to subscribe, using threads and/or batching return values
		// .. e.g. look at https://spring.io/blog/2016/06/13/notes-on-reactive-programming-part-ii-writing-some-code
		// using subscribeOn() is a better alternative as it avoids accidentally blocking the thread (by requiring a thread model)
		// ..in fact you can go further and use publishOn() to identify the thread group to do the publishing on
		resultFlux.subscribe( Entry::gotFlux );

		while ( totalValues < 10 ) {
			try {
				Thread.sleep( 100 );
			}
			catch ( InterruptedException l_ex ) {
			}
		}
	}

	public static void gotFlux( final String value ) {
		System.out.println( ">> result = " + value );
		totalValues++;
	}
}
