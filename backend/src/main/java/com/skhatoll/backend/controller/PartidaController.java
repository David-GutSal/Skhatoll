package com.skhatoll.backend.controller;

import com.skhatoll.backend.dto.partida.*;
import com.skhatoll.backend.entities.SesionVotacion;
import com.skhatoll.backend.service.interfaces.partida.IPartidaService;
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

    // -------------------------------------------------------
    // PUT /partida/{codigo}/fase
    // Alterna entre DÍA y NOCHE
    // -------------------------------------------------------
    @PutMapping("/{codigo}/fase")
    public ResponseEntity<String> cambiarFase(@PathVariable String codigo) {

        partidaService.cambiarFase(codigo);
        return ResponseEntity.ok("Fase cambiada correctamente");

    }

    // -------------------------------------------------------
    // POST /partida/{codigo}/votacion/abrir
    // Body: { "tipo": "DIA" | "LOBOS" | "HABILIDAD" }
    // -------------------------------------------------------
    @PostMapping("/{codigo}/votacion/abrir")
    public ResponseEntity<Integer> abrirVotacion(@PathVariable String codigo,
                                                 @Valid @RequestBody AbrirVotacionRequest request) {
        SesionVotacion sesion = partidaService.abrirVotacion(codigo, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sesion.getIdSesion());
    }

    // -------------------------------------------------------
    // PUT /partida/{codigo}/votacion/{idSesion}/cerrar
    // -------------------------------------------------------
    @PutMapping("/{codigo}/votacion/{idSesion}/cerrar")
    public ResponseEntity<ResultadoVotacionDto> cerrarVotacion(@PathVariable String codigo,
                                            @PathVariable Integer idSesion) {
        ResultadoVotacionDto resultado = partidaService.cerrarVotacion(codigo, idSesion);
        return ResponseEntity.ok(resultado);
    }

    // -------------------------------------------------------
    // POST /partida/{codigo}/votar
    // Body: { "idObjetivo": 1 }
    // -------------------------------------------------------
    @PostMapping("/{codigo}/votar")
    public ResponseEntity<String> votar(@PathVariable String codigo,
                                   @Valid @RequestBody VotarRequest request) {
        partidaService.votar(codigo, request);
        return ResponseEntity.ok("Voto registrado");
    }

    // -------------------------------------------------------
    // PUT /partida/{codigo}/jugador/{idUsuario}/confirmar-muerte
    // -------------------------------------------------------
    @PutMapping("/{codigo}/jugador/{idUsuario}/confirmar-muerte")
    public ResponseEntity<String> confirmarMuerte(@PathVariable String codigo,
                                             @PathVariable Integer idUsuario) {
        partidaService.confirmarMuerte(codigo, idUsuario);
        return ResponseEntity.ok("Muerte confirmada");
    }

    // -------------------------------------------------------
    // GET /partida/{codigo}/sesion-activa
    // Devuelve: { idSesion, tipo, ronda, abierta, fechaInicio }
    // -------------------------------------------------------
    @GetMapping("/{codigo}/sesion-activa")
    public ResponseEntity<SesionVotacion> getSesionActiva(@PathVariable String codigo) {
        SesionVotacion sesion = partidaService.getSesionActiva(codigo);
        return ResponseEntity.ok(sesion);
    }

    @PostMapping("/{codigo}/habilidad")
    public ResponseEntity<HabilidadResultadoDto> usarHabilidad(@PathVariable String codigo,
                                           @Valid @RequestBody HabilidadRequest request) {
        HabilidadResultadoDto resultado = partidaService.usarHabilidad(codigo, request);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{codigo}/cerrar")
    public ResponseEntity<String> cerrarPartida(@PathVariable String codigo) {
        partidaService.cerrarPartida(codigo);
        return ResponseEntity.ok("Partida cerrada");
    }

    @GetMapping("/{codigo}/estado")
    public ResponseEntity<EstadoPartidaDto> getEstadoPartida(@PathVariable String codigo) {
        EstadoPartidaDto estado = partidaService.getEstadoPartida(codigo);
        return ResponseEntity.ok(estado);
    }
}
