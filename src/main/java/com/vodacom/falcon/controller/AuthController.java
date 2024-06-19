package com.vodacom.falcon.controller;


import com.vodacom.falcon.model.request.AuthLoginRequest;
import com.vodacom.falcon.model.request.UserRegistrationRequest;
import com.vodacom.falcon.model.response.TokenResponse;
import com.vodacom.falcon.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "*")

@Slf4j
@RequestMapping("falcon/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/signUp")
    @Operation(summary = "Sign Up", description = "For user registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")})
    })
    public ResponseEntity<Void> signUp(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        service.createUser(userRegistrationRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/signIn")
    @Operation(summary = "Sign In", description = "For user login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "application/json")})
    })
    public ResponseEntity<TokenResponse> signIn(@RequestBody @Valid AuthLoginRequest authLoginRequest) {
        service.login(authLoginRequest);
        return ResponseEntity.ok(service.login(authLoginRequest));
    }
}
