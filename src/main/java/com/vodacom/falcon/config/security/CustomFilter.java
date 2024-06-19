package com.vodacom.falcon.config.security;

import com.vodacom.falcon.model.User;
import com.vodacom.falcon.repository.UserRepository;
import com.vodacom.falcon.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.vodacom.falcon.util.FalconDefaults.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final TokenService tokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (token != null) {
            var username = tokenService.validateToken(token);
            User user = userRepository.findByUsername(username);

            if (user != null) {
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }


    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header == null) return null;
        return header.replace("Bearer ", "");
    }
}
