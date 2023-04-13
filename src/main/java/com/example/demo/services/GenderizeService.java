package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.Gender;

import reactor.core.publisher.Mono;

@Service
public class GenderizeService {
    
    /*
     * Get gender for a name.
     * 
     * @param name The name to get info for.
     * @return The gender info
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
}
