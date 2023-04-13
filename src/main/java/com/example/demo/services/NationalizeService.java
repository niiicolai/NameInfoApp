package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.Nationality;

import reactor.core.publisher.Mono;

@Service
public class NationalizeService {
    
    /*
     * Get nationality for a name.
     * 
     * @param name The name to get info for.
     * @return The nationality info
     */
    Mono<Nationality> getNationalityForName(String name) {
        WebClient client = WebClient.create();
        Mono<Nationality> nationality = client
            .get()
            .uri("https://api.nationalize.io?name=" + name)
            .retrieve()
            .bodyToMono(Nationality.class);
        return nationality;
    }
}
