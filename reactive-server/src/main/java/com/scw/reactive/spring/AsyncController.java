package com.scw.reactive.spring;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

// It appears we cannot use the router/handler mechanism as that returns a mono?!
@RestController
public class AsyncController {

	@GetMapping( value = "/hellofluxasync", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
	public Flux<String> doFlux() {
		return Flux.interval( Duration.ofSeconds( 1 ) ).map( sequence -> "Flux - " + sequence );
	}
}
