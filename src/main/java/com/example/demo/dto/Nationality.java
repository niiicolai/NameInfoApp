package com.example.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Nationality {

    /*
     * The variable should obviously be named "countries" but the API returns it as
     * "country" so we have to use that name.
     */
    List<Country> country;

    public Country getCountryWithHighestProbability() {
        return country.stream().max((c1, c2) -> Double.compare(c1.probability, c2.probability)).get();
    }
}
