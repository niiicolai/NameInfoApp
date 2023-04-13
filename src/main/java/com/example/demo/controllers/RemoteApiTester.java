package com.example.demo.controllers;

import com.example.demo.dto.Gender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RemoteApiTester implements CommandLineRunner {

    /*
    * A list of names to test the genderize.io API.
    */
    List<String> names = Arrays.asList(
        "lars",
        "peter",
        "sanne",
        "kim",
        "david",
        "maja"
    );

    /*
    * Get gender for a name.
    */
    Mono<Gender> getGenderForName(String name) {
        WebClient client = WebClient.create();
        Mono<Gender> gender = client
        .get()
        .uri("https://api.genderize.io?name=" + name)
        .retrieve()
        .bodyToMono(Gender.class);
        return gender;
    }

    /*
    * This method will call the slow endpoint and return a Mono<String> object.
    * The Mono<String> object will be fulfilled when the slow endpoint returns a response.
    */
    private Mono<String> callSlowEndpoint() {
        Mono<String> slowResponse = WebClient
        .create()
        .get()
        .uri("http://localhost:8080/random-string-slow")
        .retrieve()
        .bodyToMono(String.class)
        .doOnError(e -> System.out.println("Error: " + e.getMessage()));

        return slowResponse;
    }

    /*
    * This method will be called when the application starts.
    */
    @Override
    public void run(String... args) throws Exception {
        callSlowEndpointBlocking();
        callSlowEndpointNonBlocking();

        getGendersBlocking();
        getGendersNonBlocking();
    }

    /*
    * This method will call the slow endpoint three times, but it will block the thread
    * for three seconds each time. This is not a good practice.
    *
    * The time spent will be 9 seconds.
    *
    * The output will be something like:
    *
    * Time spent BLOCKING (ms): 9000,Random string,Random string,Random string
    */
    public void callSlowEndpointBlocking() {
        long start = System.currentTimeMillis();
        List<String> ramdomStrings = new ArrayList<>();

        Mono<String> slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block()); //Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block()); //Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block()); //Three seconds spent
        long end = System.currentTimeMillis();
        ramdomStrings.add(0, "Time spent BLOCKING (ms): " + (end - start));

        System.out.println(ramdomStrings.stream().collect(Collectors.joining(",")));
    }

    /*
    * This method will call the slow endpoint three times, but it will not block the thread
    * for three seconds each time. This is a good practice.
    *
    * The time spent will be 3 seconds.
    *
    * The output will be something like:
    *
    * Time spent NON-BLOCKING (ms): 3000,Random string,Random string,Random string
    */
    public void callSlowEndpointNonBlocking() {
        long start = System.currentTimeMillis();
        Mono<String> sr1 = callSlowEndpoint();
        Mono<String> sr2 = callSlowEndpoint();
        Mono<String> sr3 = callSlowEndpoint();

        var rs = Mono
        .zip(sr1, sr2, sr3)
        .map(t -> {
            List<String> randomStrings = new ArrayList<>();
            randomStrings.add(t.getT1());
            randomStrings.add(t.getT2());
            randomStrings.add(t.getT3());
            long end = System.currentTimeMillis();
            randomStrings.add(0, "Time spent NON-BLOCKING (ms): " + (end - start));
            return randomStrings;
        });
        List<String> randoms = rs.block(); //We only block when all the three Mono's has fulfilled
        System.out.println(randoms.stream().collect(Collectors.joining(",")));
    }

    /*
    * This method will call the genderize.io API six times, but it will block the thread
    * for three seconds each time. This is not a good practice.
    */
    public void getGendersBlocking() {
        long start = System.currentTimeMillis();
        names
            .stream()
            .map(name -> getGenderForName(name).block())
            .collect(Collectors.toList());
        long end = System.currentTimeMillis();
        System.out.println(
            "Time for six external requests, BLOCKING: " + (end - start)
        );
    }

    /*
    * This method will call the genderize.io API six times, but it will not block the thread
    * for three seconds each time. This is a good practice.
    */
    public void getGendersNonBlocking() {
        long start = System.currentTimeMillis();
        var genders = names
        .stream()
        .map(name -> getGenderForName(name))
        .collect(Collectors.toList());
        Flux<Gender> flux = Flux.merge(Flux.concat(genders));
        flux.collectList().block();
        long end = System.currentTimeMillis();
        System.out.println(
            "Time for six external requests, BLOCKING: " + (end - start)
        );
    }
}
