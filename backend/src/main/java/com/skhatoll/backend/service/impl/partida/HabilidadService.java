package com.skhatoll.backend.service.impl.partida;

import com.skhatoll.backend.dto.partida.HabilidadResultadoDto;
import com.skhatoll.backend.dto.partida.HabilidadRequest;
import com.skhatoll.backend.dto.partida.MuerteConfirmadaDto;
import com.skhatoll.backend.entities.*;
import com.skhatoll.backend.repository.*;
import com.skhatoll.backend.service.interfaces.partida.IHabilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.skhatoll.backend.util.constants.ErrorMessages.*;
import static com.skhatoll.backend.util.constants.GameConstants.*;

import com.skhatoll.backend.dto.partida.FinPartidaDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabilidadService implements IHabilidadService {

    private final SalaRepository salaRepository;
    private final SalaUsuarioRepository salaUsuarioRepository;
    private final SesionVotacionRepository sesionVotacionRepository;
    private final HabilidadSalaRepository habilidadSalaRepository;
    private final EnamoradosRepository enamoradosRepository;
    private final UsuarioRepository usuarioRepository;
    private final PartidaSocketService partidaSocketService;

    private Usuario getUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        String nombre = authentication.getName();
        return usuarioRepository.findByNombre(nombre).orElseThrow(() -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO + ": " + nombre));
    }

    private Sala getSalaIniciada(String codigoSala) {
        Sala sala = salaRepository.findByCodigoSala(codigoSala).orElseThrow(() -> new IllegalArgumentException(SALA_NO_ENCONTRADA));
        if (sala.getEstadoSala() != Sala.EstadoSala.INICIADA) {
            throw new IllegalStateException(PARTIDA_NO_INICIADA);
        }
        return sala;
    }

    @Override
    @Transactional
    public HabilidadResultadoDto usarHabilidad(String codigoSala, HabilidadRequest request) {
        log.info("Usuario {} intentando usar habilidad {} en sala {}",
                getUsuarioAutenticado().getNombre(), request.nombreHabilidad(), codigoSala);

        Usuario solicitante = getUsuarioAutenticado();
        Sala sala = getSalaIniciada(codigoSala);

        SalaUsuario salaUsuario = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), solicitante.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(JUGADOR_NO_EN_SALA));

        String nombreHabilidad = request.nombreHabilidad();

        if (HAB_DISPARO.equals(nombreHabilidad)) {
            log.debug("Ejecutando habilidad DISPARO para {}", solicitante.getNombre());
            return usarDisparo(codigoSala, sala, salaUsuario, request.objetivos());
        }

        if (!salaUsuario.getEstaVivo()) {
            log.warn("Jugador {} intentado usar habilidad {} pero está eliminado", solicitante.getNombre(), nombreHabilidad);
            throw new IllegalStateException("Los jugadores eliminados no pueden usar habilidades");
        }

        SesionVotacion sesion = sesionVotacionRepository.findBySala_IdSalaAndAbiertaTrue(sala.getIdSala())
                .orElseThrow(() -> new IllegalStateException(VOTACION_NO_ENCONTRADA));

        if (sesion.getTipo() != SesionVotacion.TipoVotacion.HABILIDAD) {
            log.warn("Sesión de votación no es de tipo HABILIDAD para habilidad {}", nombreHabilidad);
            throw new IllegalStateException("La sesión activa no es de tipo HABILIDAD");
        }

        if (HAB_VISION.equals(nombreHabilidad)) {
            log.debug("Ejecutando habilidad VISION para {}", solicitante.getNombre());
            return usarVision(sala, salaUsuario, request.objetivos());
        }

        if (HAB_ESPIAR.equals(nombreHabilidad)) {
            log.debug("Ejecutando habilidad ESPIAR para {}", solicitante.getNombre());
            return usarEspiar(sala, salaUsuario);
        }

        HabilidadSala habilidad = habilidadSalaRepository.findByIdSalaUsuario_IdSalaUsuarioAndNombre(salaUsuario.getIdSalaUsuario(), nombreHabilidad)
                .orElseThrow(() -> new IllegalArgumentException("No tienes la habilidad: " + nombreHabilidad));

        if (habilidad.getUsada()) {
            log.warn("Habilidad {} ya fue usada por {}", nombreHabilidad, solicitante.getNombre());
            throw new IllegalStateException(HABILIDAD_YA_USADA);
        }

        HabilidadResultadoDto resultado = switch (nombreHabilidad) {
            case HAB_POCION_VIDA -> {
                log.debug("Ejecutando habilidad POCION_VIDA para {}", solicitante.getNombre());
                yield usarPocionVida(codigoSala, sala, request.objetivos());
            }
            case HAB_POCION_MUERTE -> {
                log.debug("Ejecutando habilidad POCION_MUERTE para {}", solicitante.getNombre());
                yield usarPocionMuerte(codigoSala, sala, request.objetivos());
            }
            case HAB_FLECHAZO -> {
                log.debug("Ejecutando habilidad FLECHAZO para {}", solicitante.getNombre());
                yield usarFlechazo(sala, salaUsuario, request.objetivos());
            }
            case HAB_MODELO -> {
                log.debug("Ejecutando habilidad MODELO para {}", solicitante.getNombre());
                yield usarModelo(sala, salaUsuario, request.objetivos());
            }
            default -> throw new IllegalArgumentException("Habilidad desconocida: " + nombreHabilidad);
        };

        habilidad.setUsada(true);
        habilidadSalaRepository.save(habilidad);

        log.info("Habilidad {} usada exitosamente por {} en sala {}", nombreHabilidad, solicitante.getNombre(), codigoSala);
        return resultado;
    }

    HabilidadResultadoDto usarPocionVida(String codigoSala, Sala sala, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La poción de vida requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (objetivo.getMuerteConfirmada()) {
            throw new IllegalStateException("No puedes revivir a un jugador cuya muerte ya fue confirmada");
        }
        if (objetivo.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está vivo");
        }

        objetivo.setEstaVivo(true);
        objetivo.setMuerteConfirmada(false);
        salaUsuarioRepository.save(objetivo);

        String nombreRol = objetivo.getRol() != null ? objetivo.getRol().getNombre() : "aldeano";
        String bando = objetivo.getRol() != null ? objetivo.getRol().getBando().name() : "aldea";
        partidaSocketService.notificarMuerte(codigoSala, new MuerteConfirmadaDto(objetivo.getUsuario().getNombre(), nombreRol, bando, false, MuerteConfirmadaDto.TIPO_REVIVIR));

        return new HabilidadResultadoDto(HAB_POCION_VIDA, List.of(objetivo.getUsuario().getNombre()), RES_REVIVIDO, null);
    }

    HabilidadResultadoDto usarPocionMuerte(String codigoSala, Sala sala, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La poción de muerte requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("El jugador ya está eliminado");
        }

        objetivo.setEstaVivo(false);
        objetivo.setMuerteConfirmada(true);
        salaUsuarioRepository.save(objetivo);

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(objetivo.getUsuario().getNombre(), objetivo.getRol().getNombre(), objetivo.getRol().getBando().name(), true, MuerteConfirmadaDto.TIPO_CONFIRMAR);
        partidaSocketService.notificarMuerte(codigoSala, muerte);
        comprobarFinPartida(codigoSala, sala);

        return new HabilidadResultadoDto(HAB_POCION_MUERTE, List.of(objetivo.getUsuario().getNombre()), RES_ELIMINADO, null);
    }

    void comprobarFinPartida(String codigoSala, Sala sala) {
        List<SalaUsuario> jugadoresVivos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream()
                .filter(su -> su.getEstaVivo() && !su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario()))
                .toList();

        long lobosVivos = jugadoresVivos.stream()
                .filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo)
                .count();

        long aldeaViva = jugadoresVivos.stream()
                .filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.aldea)
                .count();

        String bandoGanador = null;
        String mensaje = null;

        if (lobosVivos == 0) {
            bandoGanador = BANDO_ALDEA;
            mensaje = "¡La aldea ha eliminado a todos los hombres lobo!";
        } else if (lobosVivos >= aldeaViva) {
            bandoGanador = BANDO_LOBO;
            mensaje = "¡Los hombres lobo dominan la aldea!";
        }

        if (bandoGanador != null) {
            sala.setEstadoSala(Sala.EstadoSala.CERRADA);
            salaRepository.save(sala);
            partidaSocketService.notificarFinPartida(codigoSala, new FinPartidaDto(bandoGanador, mensaje));
        }
    }

    HabilidadResultadoDto usarDisparo(String codigoSala, Sala sala, SalaUsuario cazador, List<Integer> objetivos) {
        if (cazador.getEstaVivo()) {
            throw new IllegalStateException("El cazador solo puede disparar cuando ha sido eliminado");
        }

        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("El disparo requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("El objetivo ya está eliminado");
        }

        objetivo.setEstaVivo(false);
        objetivo.setMuerteConfirmada(true);
        salaUsuarioRepository.save(objetivo);

        MuerteConfirmadaDto muerte = new MuerteConfirmadaDto(objetivo.getUsuario().getNombre(), objetivo.getRol().getNombre(), objetivo.getRol().getBando().name(), true, MuerteConfirmadaDto.TIPO_CONFIRMAR);
        partidaSocketService.notificarMuerte(codigoSala, muerte);
        comprobarFinPartida(codigoSala, sala);

        return new HabilidadResultadoDto(HAB_DISPARO, List.of(objetivo.getUsuario().getNombre()), RES_ELIMINADO, null);
    }

    HabilidadResultadoDto usarFlechazo(Sala sala, SalaUsuario cupido, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 2) {
            throw new IllegalArgumentException("El flechazo requiere exactamente dos objetivos");
        }

        Usuario u1 = usuarioRepository.findById(objetivos.get(0)).orElseThrow(() -> new IllegalArgumentException("Jugador 1 no encontrado"));
        Usuario u2 = usuarioRepository.findById(objetivos.get(1)).orElseThrow(() -> new IllegalArgumentException("Jugador 2 no encontrado"));

        salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), u1.getIdUsuario())
                .filter(SalaUsuario::getEstaVivo)
                .orElseThrow(() -> new IllegalStateException("El jugador 1 no está vivo en la sala"));

        salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), u2.getIdUsuario())
                .filter(SalaUsuario::getEstaVivo)
                .orElseThrow(() -> new IllegalStateException("El jugador 2 no está vivo en la sala"));

        Enamorados enamorados = Enamorados.builder()
                .idSala(sala.getIdSala())
                .sala(sala)
                .usuario1(u1)
                .usuario2(u2)
                .build();

        enamoradosRepository.save(enamorados);

        return new HabilidadResultadoDto(HAB_FLECHAZO, List.of(u1.getNombre(), u2.getNombre()), RES_ENAMORADOS, null);
    }

    HabilidadResultadoDto usarVision(Sala sala, SalaUsuario vidente, List<Integer> objetivos) {
        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("La visión requiere exactamente un objetivo");
        }

        SalaUsuario objetivo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!objetivo.getEstaVivo()) {
            throw new IllegalStateException("No puedes usar la visión sobre un jugador eliminado");
        }

        if (objetivo.getRol() == null) {
            throw new IllegalStateException("El jugador no tiene rol asignado");
        }

        var detalle = Map.of("nombreRol", objetivo.getRol().getNombre(), "bando", objetivo.getRol().getBando().name());
        return new HabilidadResultadoDto(HAB_VISION, List.of(objetivo.getUsuario().getNombre()), RES_ROL_REVELADO, detalle);
    }

    HabilidadResultadoDto usarEspiar(Sala sala, SalaUsuario nina) {
        if (nina.getRol() == null || !ROL_NINA.equals(nina.getRol().getNombre())) {
            throw new IllegalStateException("Solo la Niña puede usar esta habilidad");
        }

        List<SalaUsuario> jugadoresVivos = salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()).stream()
                .filter(su -> su.getEstaVivo() && !su.getUsuario().getIdUsuario().equals(sala.getNarrador().getIdUsuario()) && !su.getUsuario().getIdUsuario().equals(nina.getUsuario().getIdUsuario()))
                .toList();

        List<String> lobos = jugadoresVivos.stream()
                .filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.lobo)
                .map(su -> su.getUsuario().getNombre())
                .collect(Collectors.toList());

        int cantidadRuido = Math.max(1, lobos.size());

        List<String> aldeanos = jugadoresVivos.stream()
                .filter(su -> su.getRol() != null && su.getRol().getBando() == Rol.Bando.aldea)
                .map(su -> su.getUsuario().getNombre())
                .collect(Collectors.toList());

        Collections.shuffle(aldeanos);

        List<String> resultado = new ArrayList<>(lobos);
        aldeanos.stream().limit(cantidadRuido).forEach(resultado::add);
        Collections.shuffle(resultado);

        return new HabilidadResultadoDto(HAB_ESPIAR, resultado, RES_LISTA_SOSPECHOSOS, null);
    }

    HabilidadResultadoDto usarModelo(Sala sala, SalaUsuario ninoSalvaje, List<Integer> objetivos) {
        if (ninoSalvaje.getRol() == null || !ROL_NINO_SALVAJE.equals(ninoSalvaje.getRol().getNombre())) {
            throw new IllegalStateException("Solo el Niño Salvaje puede usar esta habilidad");
        }

        if (objetivos == null || objetivos.size() != 1) {
            throw new IllegalArgumentException("Debes elegir exactamente un modelo");
        }

        if (ninoSalvaje.getIdModelo() != null) {
            throw new IllegalStateException("Ya tienes un modelo asignado");
        }

        if (ninoSalvaje.getUsuario().getIdUsuario().equals(objetivos.getFirst())) {
            throw new IllegalStateException("No puedes elegirte a ti mismo como modelo");
        }

        SalaUsuario modelo = salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), objetivos.getFirst())
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado en la sala"));

        if (!modelo.getEstaVivo()) {
            throw new IllegalStateException("No puedes elegir un jugador eliminado como modelo");
        }

        ninoSalvaje.setIdModelo(modelo.getUsuario().getIdUsuario());
        salaUsuarioRepository.save(ninoSalvaje);

        return new HabilidadResultadoDto(HAB_MODELO, List.of(modelo.getUsuario().getNombre()), RES_MODELO_ASIGNADO, null);
    }
}