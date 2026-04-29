package com.skhatoll.backend.controller;

import com.skhatoll.backend.dto.sala.*;
import com.skhatoll.backend.service.interfaces.jugador.IJugadorService;
import com.skhatoll.backend.service.interfaces.sala.ISalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final ISalaService salaService;
    private final  IJugadorService jugadorService;

    // -------------------------------------------------------
    // POST /salas/crear
    // Crea una sala y devuelve el código
    // -------------------------------------------------------
    @PostMapping("/crear")
    public ResponseEntity<CrearSalaResponse> crearSala() {
            CrearSalaResponse response = salaService.crearSala();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------------------------------
    // POST /salas/unirse
    // Body: { "codigoSala": "ABC123" }
    // -------------------------------------------------------
    @PostMapping("/unirse")
    public ResponseEntity<String> unirse(@Valid @RequestBody UnirseRequest request) {
            salaService.unirse(request);
            return ResponseEntity.ok("Te has unido a la sala correctamente");
    }

    // -------------------------------------------------------
    // GET /salas/{codigo}/jugadores
    // Devuelve la lista de jugadores en la sala
    // -------------------------------------------------------
    @GetMapping("/{codigo}/jugadores")
    public ResponseEntity<List<JugadorDto>> getJugadores(@PathVariable String codigo) {
        return ResponseEntity.ok(jugadorService.getJugadores(codigo));
    }

    // -------------------------------------------------------
    // PUT /salas/{codigo}/narrador
    // Body: { "idUsuario": 1 }
    // Solo el creador puede llamar a este endpoint
    // -------------------------------------------------------
    @PutMapping("/{codigo}/narrador")
    public ResponseEntity<String> asignarNarrador(@PathVariable String codigo,
                                                  @Valid @RequestBody AsignarNarradorRequest request) {
            salaService.asignarNarrador(codigo, request);
            return ResponseEntity.ok("Narrador asignado correctamente");
    }

    // -------------------------------------------------------
    // POST /salas/{codigo}/iniciar
    // Solo el narrador puede llamar a este endpoint
    // -------------------------------------------------------
    @PostMapping("/{codigo}/iniciar")
    public ResponseEntity<String> iniciarPartida(@PathVariable String codigo) {
            salaService.iniciarPartida(codigo);
            return ResponseEntity.ok("Partida iniciada");
    }

    // -------------------------------------------------------
    // GET /salas/{codigo}/roles
    // Solo accesible para el narrador
    // -------------------------------------------------------
    @GetMapping("/{codigo}/roles")
    public ResponseEntity<List<JugadorRolDto>> getJugadoresConRol(@PathVariable String codigo) {
            List<JugadorRolDto> jugadores = salaService.getJugadoresConRol(codigo);
            return ResponseEntity.ok(jugadores);
    }

    @DeleteMapping("/{codigo}/salir")
    public ResponseEntity<String> salirDeSala(@PathVariable String codigo) {
        salaService.salirDeSala(codigo);
        return ResponseEntity.ok("Has salido de la sala");
    }
}
