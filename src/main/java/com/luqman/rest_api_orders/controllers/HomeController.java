package com.luqman.rest_api_orders.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Home", description = "API entry point and info")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Minimal API home page")
     public String home(){
        return "Welcome to the Orders API";
    }
}
