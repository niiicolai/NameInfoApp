package com.example.demo.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.Age;
import com.example.demo.dto.Country;
import com.example.demo.dto.Gender;
import com.example.demo.dto.NameInfo;

import reactor.core.publisher.Mono;

/*
 * This is a service that uses the other services to get the name info.
 */
@Service
public class NameInfoService {
    
    /*
     * The genderize service is used to get gender info based on a name.
     */
    private GenderizeService genderizeService;

    /*
     * The nationalize service is used to get nationality info based on a name.
     */
    private NationalizeService nationalizeService;

    /*
     * The agify service is used to get age info based on a name.
     */
    private AgifyService agifyService;

    /*
     * This constructor is used by Spring to inject the other services.
     */
    public NameInfoService (GenderizeService genderizeService, NationalizeService nationalizeService, AgifyService agifyService) {
        this.genderizeService = genderizeService;
        this.nationalizeService = nationalizeService;
        this.agifyService = agifyService;
    }

    /*
     * This method uses the other services to get the name info.
     * 
     * @param name The name to get info for.
     * @return The name info.
     * 
     * @throws ResponseStatusException If the name is null or blank.
     */
    public NameInfo getNameInfo(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required.");
        }

        return createNameInfoParallel(name);
    }

    /*
     * This method creates a name info object by calling 
     * the other services in parallel.
     * 
     * @param name The name to get info for.
     * @return The name info.
     */
    private NameInfo createNameInfoParallel(String name) {
        long start = System.currentTimeMillis();
        NameInfo nameInfo = new NameInfo();
        nameInfo.setName(name);

        Mono.zip(
            genderizeService.getGenderForName(name), 
            nationalizeService.getNationalityForName(name), 
            agifyService.getAgeForName(name)
        )
        .flatMap(tuple -> {
            Gender gender = tuple.getT1();
            Country country = tuple.getT2().getCountryWithHighestProbability();
            Age age = tuple.getT3();

            nameInfo.setGender(gender.getGender());
            nameInfo.setGenderProbability(gender.getProbability());
            nameInfo.setCountry(country.getCountry_id());
            nameInfo.setCountryProbability(country.getProbability());
            nameInfo.setAge(age.getAge());
            nameInfo.setAgeCount(age.getCount());

            return Mono.just(nameInfo);
        }).block();

        long end = System.currentTimeMillis();
        nameInfo.setExecutionTime(end - start);

        return nameInfo;
    }

    /*
     * This method creates a name info object by calling
     * the other services sequentially.
     * 
     * @param name The name to get info for.
     * @return The name info.
     */
    public NameInfo createNameInfoSequential(String name) {
        long start = System.currentTimeMillis();
        NameInfo nameInfo = new NameInfo();
        nameInfo.setName(name);

        Gender gender = genderizeService.getGenderForName(name).block();
        Country country = nationalizeService.getNationalityForName(name).block().getCountryWithHighestProbability();
        Age age = agifyService.getAgeForName(name).block();

        nameInfo.setGender(gender.getGender());
        nameInfo.setGenderProbability(gender.getProbability());
        nameInfo.setCountry(country.getCountry_id());
        nameInfo.setCountryProbability(country.getProbability());
        nameInfo.setAge(age.getAge());
        nameInfo.setAgeCount(age.getCount());

        long end = System.currentTimeMillis();
        nameInfo.setExecutionTime(end - start);

        return nameInfo;
    }
}
