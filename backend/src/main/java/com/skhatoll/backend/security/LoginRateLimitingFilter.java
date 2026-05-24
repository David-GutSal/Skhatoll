package com.skhatoll.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    static final int MAX_ATTEMPTS = 5;
    static final Duration WINDOW = Duration.ofMinutes(1);
    static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final Map<String, AttemptTracker> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        AttemptTracker tracker = attempts.computeIfAbsent(ip, k -> new AttemptTracker());
        long now = System.currentTimeMillis();

        synchronized (tracker) {
            tracker.cleanup(now);

            if (tracker.isBlocked(now)) {
                log.warn("Rate limit excedido para IP {} en /auth/login", ip);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Demasiadas solicitudes. Intenta de nuevo en 1 minuto\"}");
                return;
            }

            tracker.record(now);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST")
                && "/auth/login".equals(request.getRequestURI());
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    static class AttemptTracker {
        private final ArrayList<Long> timestamps = new ArrayList<>();
        private long blockUntil = 0;

        void cleanup(long now) {
            long cutoff = now - WINDOW.toMillis();
            timestamps.removeIf(t -> t < cutoff);
        }

        boolean isBlocked(long now) {
            return now < blockUntil || timestamps.size() >= MAX_ATTEMPTS;
        }

        void record(long now) {
            timestamps.add(now);
            if (timestamps.size() >= MAX_ATTEMPTS) {
                blockUntil = now + BLOCK_DURATION.toMillis();
            }
        }
    }
}
