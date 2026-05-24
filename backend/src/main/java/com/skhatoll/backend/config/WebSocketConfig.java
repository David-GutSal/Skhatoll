package com.skhatoll.backend.config;

import com.skhatoll.backend.entities.Sala;
import com.skhatoll.backend.repository.SalaRepository;
import com.skhatoll.backend.repository.SalaUsuarioRepository;
import com.skhatoll.backend.repository.UsuarioRepository;
import com.skhatoll.backend.security.JwtUtil;
import com.skhatoll.backend.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    private static final Pattern SALA_CODE_PATTERN = Pattern.compile("/(?:topic|queue)/(?:sala|partida)/([^/]+)");

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String allowedOrigin = System.getenv("ALLOWED_ORIGIN");
        if (allowedOrigin != null && !allowedOrigin.isBlank()) {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("http://localhost:5173", allowedOrigin)
                    .withSockJS();
        } else {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("http://localhost:5173")
                    .withSockJS();
        }
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/");
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor == null) return message;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    return handleConnect(accessor, message);
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    return handleSubscribe(accessor, message);
                }

                return message;
            }

            private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> originalMessage) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.warn("WS CONNECT sin token de autorización");
                    return null;
                }

                String token = authHeader.substring(7);

                try {
                    String nombre = jwtUtil.extraerNombre(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(nombre);

                    if (jwtUtil.esValido(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(auth);
                        log.debug("WS CONNECT exitoso para usuario {}", nombre);
                    } else {
                        log.warn("WS CONNECT: token expirado o inválido para usuario {}", nombre);
                        return null;
                    }
                } catch (Exception e) {
                    log.error("WS CONNECT: error validando token: {}", e.getMessage());
                    return null;
                }

                return originalMessage;
            }

            private Message<?> handleSubscribe(StompHeaderAccessor accessor, Message<?> originalMessage) {
                String destination = accessor.getDestination();
                if (destination == null) return originalMessage;

                Matcher matcher = SALA_CODE_PATTERN.matcher(destination);
                if (!matcher.find()) return originalMessage;

                String codigoSala = matcher.group(1);

                if (accessor.getUser() == null) {
                    log.warn("WS SUBSCRIBE a {} sin autenticar", destination);
                    return null;
                }

                String nombreUsuario = accessor.getUser().getName();

                var salaOpt = salaRepository.findByCodigoSala(codigoSala);
                if (salaOpt.isEmpty()) {
                    log.warn("WS SUBSCRIBE: sala {} no encontrada", codigoSala);
                    return null;
                }

                Sala sala = salaOpt.get();
                var userOpt = usuarioRepository.findByNombre(nombreUsuario);
                if (userOpt.isEmpty()) {
                    log.warn("WS SUBSCRIBE: usuario {} no encontrado", nombreUsuario);
                    return null;
                }

                Integer idUsuario = userOpt.get().getIdUsuario();

                boolean esNarrador = sala.getNarrador() != null
                        && sala.getNarrador().getIdUsuario().equals(idUsuario);
                boolean estaEnSala = salaUsuarioRepository
                        .existsBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), idUsuario);

                if (!esNarrador && !estaEnSala) {
                    log.warn("WS SUBSCRIBE: usuario {} no pertenece a sala {}", nombreUsuario, codigoSala);
                    return null;
                }

                return originalMessage;
            }
        });
    }
}
