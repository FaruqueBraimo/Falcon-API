package com.vodacom.falcon.controller;


import com.vodacom.falcon.model.request.UserRegistrationRequest;
import com.vodacom.falcon.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "*")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("falcon/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        service.createUser(userRegistrationRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
