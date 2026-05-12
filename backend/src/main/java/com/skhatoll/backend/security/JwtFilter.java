package com.skhatoll.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.trace("Request sin Authorization header: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String requestPath = request.getRequestURI();

        log.debug("Procesando request: {} {}", request.getMethod(), requestPath);

        final String nombre;
        try {
            nombre = jwtUtil.extraerNombre(token);
            log.debug("Token valido para usuario: {}", nombre);
        } catch (Exception e) {
            log.warn("Token invalido para request {}: {}", requestPath, e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (nombre != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(nombre);

            if (jwtUtil.esValido(token, userDetails.getUsername())) {

                log.debug("Autenticando usuario {} para request {}", nombre, requestPath);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("Token expirado o invalido para usuario {} en request {}", nombre, requestPath);
            }
        }

        filterChain.doFilter(request, response);
    }
}