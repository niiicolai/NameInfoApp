package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.NameInfo;
import com.example.demo.services.NameInfoService;

@RestController
public class NameInfoController {

    /*
     * The name info service is used to get name info.
     */
    private NameInfoService nameInfoService;

    /*
     * This constructor is used by Spring to inject the name info service.
     */
    public NameInfoController(NameInfoService nameInfoService) {
        this.nameInfoService = nameInfoService;
    }
    
    /*
     * This method is called when a GET request is made to the /name-info endpoint.
     * 
     * It will return the name info for the name passed in the name parameter.
     * 
     * @param name The name to get info for.
     * 
     * @return The name info.
     */
    @GetMapping(value = "/name-info")
    public NameInfo nameInfo(@RequestParam String name) {
        return nameInfoService.getNameInfo(name);
    }
}
