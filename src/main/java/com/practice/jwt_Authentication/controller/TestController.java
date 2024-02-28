package com.practice.jwt_Authentication.controller;

import com.practice.jwt_Authentication.payloads.response.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<MessageResponse> test(){
        return ResponseEntity.ok(new MessageResponse("This is a test controller for Authentication"));
    }
}