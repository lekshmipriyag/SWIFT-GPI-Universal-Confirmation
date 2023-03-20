package com.swift.updategpitracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swiftUpdatingGPITracker/version1/")
public class GPIController {

    @RequestMapping("welcome")
    public String welcome() {
        return "Microservice for Updating GPI Tracker";
    }

}
