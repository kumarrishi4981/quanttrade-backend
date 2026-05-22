package com.quanttrade.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "📈 QuantTrade REST API is up and running! Visit the frontend at: https://quanttrade-frontend.vercel.app";
    }
}
