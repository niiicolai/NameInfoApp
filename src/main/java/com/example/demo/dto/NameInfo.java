package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NameInfo {
    
    String name;
    String gender;
    double genderProbability;
    int age;
    int ageCount;
    String country;
    double countryProbability;
    double executionTime;
}
