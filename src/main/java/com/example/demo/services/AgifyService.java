package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.Age;

import reactor.core.publisher.Mono;

@Service
public class AgifyService {
    
    /*
     * Get age for a name.
     * 
     * @param name The name to get info for.
     * @return The age info
     */
    Mono<Age> getAgeForName(String name) {
        WebClient client = WebClient.create();
        Mono<Age> age = client
            .get()
            .uri("https://api.agify.io?name=" + name)
            .retrieve()
            .bodyToMono(Age.class);
        return age;
    }
}
