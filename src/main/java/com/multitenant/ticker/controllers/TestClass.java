package com.multitenant.ticker.controllers;

import com.multitenant.ticker.dto.AuthResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test")
public class TestClass {

    @GetMapping("hello")
    public ResponseEntity<AuthResponseDto> hello() {
        return new ResponseEntity<>(new AuthResponseDto("Hello from ticker"), HttpStatus.OK);
    }

}
