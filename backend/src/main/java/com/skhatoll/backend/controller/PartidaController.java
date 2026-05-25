package com.skhatoll.backend.controller;

import com.skhatoll.backend.dto.partida.*;
import com.skhatoll.backend.entities.SesionVotacion;
import com.skhatoll.backend.service.interfaces.partida.IPartidaService;

import java.util.List;
import com.skhatoll.backend.service.interfaces.partida.IHabilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partida")
@RequiredArgsConstructor
public class PartidaController {

    private final IPartidaService partidaService;
    private final IHabilidadService habilidadService;

    @PutMapping("/{codigo}/fase")
    public ResponseEntity<String> cambiarFase(@PathVariable String codigo) {
        partidaService.cambiarFase(codigo);
        return ResponseEntity.ok("Fase cambiada correctamente");
    }

    @PostMapping("/{codigo}/votacion/abrir")
    public ResponseEntity<Integer> abrirVotacion(@PathVariable String codigo,
                                                 @Valid @RequestBody AbrirVotacionRequest request) {
        SesionVotacion sesion = partidaService.abrirVotacion(codigo, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sesion.getIdSesion());
    }

    @PutMapping("/{codigo}/votacion/{idSesion}/cerrar")
    public ResponseEntity<ResultadoVotacionDto> cerrarVotacion(@PathVariable String codigo,
                                            @PathVariable Integer idSesion) {
        ResultadoVotacionDto resultado = partidaService.cerrarVotacion(codigo, idSesion);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{codigo}/votar")
    public ResponseEntity<String> votar(@PathVariable String codigo,
                                   @Valid @RequestBody VotarRequest request) {
        partidaService.votar(codigo, request);
        return ResponseEntity.ok("Voto registrado");
    }

    @GetMapping("/{codigo}/sesion-activa")
    public ResponseEntity<SesionVotacion> getSesionActiva(@PathVariable String codigo) {
        SesionVotacion sesion = partidaService.getSesionActiva(codigo);
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/{codigo}/habilidad")
    public ResponseEntity<HabilidadResultadoDto> usarHabilidad(@PathVariable String codigo,
                                           @Valid @RequestBody HabilidadRequest request) {
        HabilidadResultadoDto resultado = habilidadService.usarHabilidad(codigo, request);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{codigo}/cerrar")
    public ResponseEntity<String> cerrarPartida(@PathVariable String codigo) {
        partidaService.cerrarPartida(codigo);
        return ResponseEntity.ok("Partida cerrada");
    }

    @PutMapping("/{codigo}/cancelar")
    public ResponseEntity<String> cancelarPartida(@PathVariable String codigo) {
        partidaService.cancelarPartida(codigo);
        return ResponseEntity.ok("Partida cancelada - Empate");
    }

    @PostMapping("/{codigo}/rendirse")
    public ResponseEntity<String> rendirse(@PathVariable String codigo) {
        partidaService.rendirse(codigo);
        return ResponseEntity.ok("Te has rendido");
    }

    @GetMapping("/{codigo}/estado")
    public ResponseEntity<EstadoPartidaDto> getEstadoPartida(@PathVariable String codigo) {
        EstadoPartidaDto estado = partidaService.getEstadoPartida(codigo);
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/{codigo}/lobos")
    public ResponseEntity<List<String>> getNombresLobos(@PathVariable String codigo) {
        List<String> lobos = partidaService.getNombresLobos(codigo);
        return ResponseEntity.ok(lobos);
    }

    @PutMapping("/{codigo}/jugador/{idUsuario}/rol")
    public ResponseEntity<String> actualizarRol(@PathVariable String codigo,
                                                @PathVariable Integer idUsuario,
                                                @RequestBody ActualizarRolRequest request) {
        partidaService.actualizarRol(codigo, idUsuario, request.nombreRol());
        return ResponseEntity.ok("Rol actualizado");
    }
}