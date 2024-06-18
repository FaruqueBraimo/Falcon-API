package com.vodacom.falcon.service;


import com.vodacom.falcon.config.security.PasswordEncoderProvider;
import com.vodacom.falcon.model.User;
import com.vodacom.falcon.model.request.UserRegistrationRequest;
import com.vodacom.falcon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final PasswordEncoderProvider encoderProvider;
    private final UserRepository userRepository;

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
}
