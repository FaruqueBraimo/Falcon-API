package com.vodacom.falcon.service;


import com.vodacom.falcon.config.security.PasswordEncoderProvider;
import com.vodacom.falcon.model.User;
import com.vodacom.falcon.model.request.AuthLoginRequest;
import com.vodacom.falcon.model.request.UserRegistrationRequest;
import com.vodacom.falcon.model.response.TokenResponse;
import com.vodacom.falcon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final PasswordEncoderProvider encoderProvider;

    private final UserRepository userRepository;
    private final ApplicationContext context;
    private final TokenService tokenService;

    public void createUser(UserRegistrationRequest auth) {
        User existingUser = userRepository.findByUsername(auth.username());
        if (existingUser != null) {
            throw new Error("User already exists! Please login");
        }

        User user = User.builder()
                .username(auth.username())
                .password(encoderProvider.passwordEncoder().encode(auth.password()))
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

    }

    public TokenResponse login(AuthLoginRequest login) {
        AuthenticationManager authenticationManager = context.getBean(AuthenticationManager.class);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(login.username(), login.password());
        Authentication auth = authenticationManager.authenticate(authToken);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return new TokenResponse(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
