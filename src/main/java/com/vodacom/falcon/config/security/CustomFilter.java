package com.vodacom.falcon.config.security;

import com.vodacom.falcon.model.User;
import com.vodacom.falcon.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static com.vodacom.falcon.util.FalconDefaults.AUTHORIZATION;
import static com.vodacom.falcon.util.FalconDefaults.BASIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final PasswordEncoderProvider encoderProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isBasicAuth(request)) {
            String base64 = this.getHeader(request)
                    .replace(BASIC, "");
            String[] credentials = new String(Base64.getDecoder()
                    .decode(base64))
                    .split(":");

            String username = credentials[0];
            String pass = credentials[1];

            User user = userRepository.findByUsername(username);

            if (Objects.isNull(user)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("User not found!");
                return;
            }

            if (encoderProvider.passwordEncoder().matches(user.getPassword(), pass)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Wrong Password");
                return;
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isBasicAuth(HttpServletRequest request) {
        String header = getHeader(request);
        return header != null && header.startsWith(BASIC);
    }

    private String getHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }
}
