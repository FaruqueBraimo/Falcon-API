package com.vodacom.falcon.config;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.TooManyListenersException;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(1)))
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(true);

        String ip = request.getRemoteAddr();
        Bucket bucket = (Bucket) session.getAttribute("throttler-" + ip);

        if (bucket == null) {
            bucket = createNewBucket();
            session.setAttribute("throttler-" + ip, bucket);
        }
        if (bucket.tryConsume(1)) {
            return true;
        }
        String message = "Getting too many requests. Too prevent abusive usage, please try again later ";
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), message);
        throw new TooManyListenersException(message);
    }
}
