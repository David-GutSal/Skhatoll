package com.skhatoll.backend.service.impl.partida;

import com.skhatoll.backend.dto.partida.HabilidadResultadoDto;
import com.skhatoll.backend.entities.*;
import com.skhatoll.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de HabilidadService")
class HabilidadServiceTest {

    @Mock private SalaRepository salaRepository;
    @Mock private SalaUsuarioRepository salaUsuarioRepository;
    @Mock private SesionVotacionRepository sesionVotacionRepository;
    @Mock private HabilidadSalaRepository habilidadSalaRepository;
    @Mock private EnamoradosRepository enamoradosRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PartidaSocketService partidaSocketService;

    @InjectMocks
    private HabilidadService habilidadService;

    private Sala sala;
    private Usuario usuarioVivo;
    private Usuario usuarioEliminado;
    private SalaUsuario salaUsuarioVivo;
    private SalaUsuario salaUsuarioEliminado;
    private Rol rolVidente;
    private Rol rolBruja;
    private Rol rolCupido;
    private Rol rolCazador;
    private Rol rolNinoSalvaje;
    private Rol rolNina;
    private Rol rolLobo;
    private Rol rolAldeano;

    @BeforeEach
    void setUp() {
        usuarioVivo = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .build();

        usuarioEliminado = Usuario.builder()
                .idUsuario(2)
                .nombre("María")
                .build();

        rolVidente = Rol.builder()
                .idRol(1)
                .nombre("Vidente")
                .bando(Rol.Bando.aldea)
                .build();

        rolBruja = Rol.builder()
                .idRol(2)
                .nombre("Bruja")
                .bando(Rol.Bando.aldea)
                .build();

        rolCupido = Rol.builder()
                .idRol(3)
                .nombre("Cupido")
                .bando(Rol.Bando.aldea)
                .build();

        rolCazador = Rol.builder()
                .idRol(4)
                .nombre("Cazador")
                .bando(Rol.Bando.aldea)
                .build();

        rolNinoSalvaje = Rol.builder()
                .idRol(5)
                .nombre("Niño salvaje")
                .bando(Rol.Bando.aldea)
                .build();

        rolNina = Rol.builder()
                .idRol(6)
                .nombre("Niña")
                .bando(Rol.Bando.aldea)
                .build();

        rolLobo = Rol.builder()
                .idRol(7)
                .nombre("Lobo")
                .bando(Rol.Bando.lobo)
                .build();

        rolAldeano = Rol.builder()
                .idRol(8)
                .nombre("Aldeano")
                .bando(Rol.Bando.aldea)
                .build();

        sala = Sala.builder()
                .idSala(1)
                .codigoSala("TEST123")
                .estadoSala(Sala.EstadoSala.INICIADA)
                .estadoDia(Sala.EstadoDia.NOCHE)
                .narrador(usuarioVivo)
                .build();

        salaUsuarioVivo = SalaUsuario.builder()
                .idSalaUsuario(1)
                .sala(sala)
                .usuario(usuarioVivo)
                .rol(rolVidente)
                .estaVivo(true)
                .muerteConfirmada(false)
                .build();

        salaUsuarioEliminado = SalaUsuario.builder()
                .idSalaUsuario(2)
                .sala(sala)
                .usuario(usuarioEliminado)
                .rol(rolAldeano)
                .estaVivo(false)
                .muerteConfirmada(true)
                .build();
    }

    @Nested
    @DisplayName("Poción de Vida")
    class PocionVidaTests {

        @Test
        @DisplayName("Debe revivir a un jugador semi-muerto")
        void usarPocionVida_deberiaRevivirJugador() {
            salaUsuarioEliminado.setEstaVivo(false);
            salaUsuarioEliminado.setMuerteConfirmada(false);

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioEliminado.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioEliminado));
            when(salaUsuarioRepository.save(any(SalaUsuario.class))).thenReturn(salaUsuarioEliminado);

            HabilidadResultadoDto result = habilidadService.usarPocionVida("TEST123", sala, List.of(usuarioEliminado.getIdUsuario()));

            assertNotNull(result);
            assertEquals("pocion_vida", result.nombreHabilidad());
            assertTrue(result.objetivos().contains("María"));
            assertEquals("REVIVIDO", result.resultado());
            verify(salaUsuarioRepository, times(1)).save(any(SalaUsuario.class));
        }

        @Test
        @DisplayName("Debe fallar si el jugador no está en la sala")
        void usarPocionVida_deberiaFallarSiJugadorNoEncontrado() {
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarPocionVida("TEST123", sala, List.of(999)));
        }

        @Test
        @DisplayName("Debe fallar si la muerte ya fue confirmada")
        void usarPocionVida_deberiaFallarSiMuerteConfirmada() {
            salaUsuarioEliminado.setMuerteConfirmada(true);
            salaUsuarioEliminado.setEstaVivo(false);

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioEliminado.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioEliminado));

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarPocionVida("TEST123", sala, List.of(usuarioEliminado.getIdUsuario())));
        }

        @Test
        @DisplayName("Debe fallar si no se proporciona exactamente un objetivo")
        void usarPocionVida_deberiaFallarSinObjetivoValido() {
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarPocionVida("TEST123", sala, List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarPocionVida("TEST123", sala, List.of(1, 2)));
        }
    }

    @Nested
    @DisplayName("Poción de Muerte")
    class PocionMuerteTests {

        @Test
        @DisplayName("Debe eliminar a un jugador vivo")
        void usarPocionMuerte_deberiaEliminarJugador() {
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioVivo.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioVivo));
            when(salaUsuarioRepository.save(any(SalaUsuario.class))).thenReturn(salaUsuarioVivo);
            doNothing().when(partidaSocketService).notificarMuerte(any(), any());

            HabilidadResultadoDto result = habilidadService.usarPocionMuerte(sala.getCodigoSala(), sala, List.of(usuarioVivo.getIdUsuario()));

            assertNotNull(result);
            assertEquals("pocion_muerte", result.nombreHabilidad());
            assertTrue(result.objetivos().contains("Juan"));
            assertEquals("ELIMINADO", result.resultado());
            verify(partidaSocketService).notificarMuerte(eq(sala.getCodigoSala()), any());
        }

        @Test
        @DisplayName("Debe fallar si el jugador ya está eliminado")
        void usarPocionMuerte_deberiaFallarSiJugadorEliminado() {
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioEliminado.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioEliminado));
            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarPocionMuerte(sala.getCodigoSala(), sala, List.of(usuarioEliminado.getIdUsuario())));
        }

        @Test
        @DisplayName("Debe fallar si no se proporciona exactamente un objetivo")
        void usarPocionMuerte_deberiaFallarSinObjetivoValido() {
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarPocionMuerte(sala.getCodigoSala(), sala, List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarPocionMuerte(sala.getCodigoSala(), sala, List.of(1, 2)));
        }
    }

    @Nested
    @DisplayName("Flechazo (Cupido)")
    class FlechazoTests {

        @Test
        @DisplayName("Debe enamorar a dos jugadores vivos")
        void usarFlechazo_deberiaEnamorarDosJugadores() {
            Usuario jugador2 = Usuario.builder().idUsuario(3).nombre("Pedro").build();
            Usuario jugador3 = Usuario.builder().idUsuario(4).nombre("Lucía").build();

            SalaUsuario salaUsuario2 = SalaUsuario.builder()
                    .idSalaUsuario(3)
                    .sala(sala)
                    .usuario(jugador2)
                    .rol(rolLobo)
                    .estaVivo(true)
                    .muerteConfirmada(false)
                    .build();

            SalaUsuario salaUsuario3 = SalaUsuario.builder()
                    .idSalaUsuario(4)
                    .sala(sala)
                    .usuario(jugador3)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .muerteConfirmada(false)
                    .build();

            SalaUsuario cupido = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCupido)
                    .estaVivo(true)
                    .muerteConfirmada(false)
                    .build();

            when(usuarioRepository.findById(3)).thenReturn(Optional.of(jugador2));
            when(usuarioRepository.findById(4)).thenReturn(Optional.of(jugador3));
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), 3))
                    .thenReturn(Optional.of(salaUsuario2));
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), 4))
                    .thenReturn(Optional.of(salaUsuario3));
            when(enamoradosRepository.save(any(Enamorados.class))).thenReturn(null);

            HabilidadResultadoDto result = habilidadService.usarFlechazo(sala, cupido, List.of(3, 4));

            assertNotNull(result);
            assertEquals("flechazo", result.nombreHabilidad());
            assertEquals(2, result.objetivos().size());
            assertTrue(result.objetivos().contains("Pedro"));
            assertTrue(result.objetivos().contains("Lucía"));
            assertEquals("ENAMORADOS", result.resultado());
            verify(enamoradosRepository).save(any(Enamorados.class));
        }

        @Test
        @DisplayName("Debe fallar si solo se proporciona un objetivo")
        void usarFlechazo_deberiaFallarConUnObjetivo() {
            SalaUsuario cupido = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCupido)
                    .estaVivo(true)
                    .build();

            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarFlechazo(sala, cupido, List.of(3)));
        }

        @Test
        @DisplayName("Debe fallar si cupido intenta unirse a sí mismo")
        void usarFlechazo_deberiaFallarSiCupidoSeEnamoraASiMismo() {
            SalaUsuario cupido = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCupido)
                    .estaVivo(true)
                    .build();

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarFlechazo(sala, cupido, List.of(1, 2)));
        }
    }

    @Nested
    @DisplayName("Visión (Vidente)")
    class VisionTests {

        @Test
        @DisplayName("Debe revelar el rol de un jugador vivo")
        void usarVision_deberiaRevelarRol() {
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioVivo.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioVivo));

            HabilidadResultadoDto result = habilidadService.usarVision(sala, salaUsuarioVivo, List.of(usuarioVivo.getIdUsuario()));

            assertNotNull(result);
            assertEquals("vision", result.nombreHabilidad());
            assertEquals("Juan", result.objetivos().get(0));
            assertEquals("ROL_REVELADO", result.resultado());
            assertNotNull(result.detalle());
            assertEquals("Vidente", ((java.util.Map<?, ?>) result.detalle()).get("nombreRol"));
            assertEquals("aldea", ((java.util.Map<?, ?>) result.detalle()).get("bando"));
        }

        @Test
        @DisplayName("Debe fallar si el objetivo está eliminado")
        void usarVision_deberiaFallarSiObjetivoEliminado() {
            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioEliminado.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioEliminado));
            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarVision(sala, salaUsuarioVivo, List.of(usuarioEliminado.getIdUsuario())));
        }

        @Test
        @DisplayName("Debe fallar si no se proporciona exactamente un objetivo")
        void usarVision_deberiaFallarSinObjetivoValido() {
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarVision(sala, salaUsuarioVivo, List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarVision(sala, salaUsuarioVivo, List.of(1, 2)));
        }
    }

    @Nested
    @DisplayName("Espiar (Niña)")
    class EspiarTests {

        @Test
        @DisplayName("Debe devolver lista mezclada de lobos y aldeanos")
        void usarEspiar_deberiaDevolverListaMezclada() {
            Usuario jugador2 = Usuario.builder().idUsuario(3).nombre("Pedro").build();
            Usuario jugador3 = Usuario.builder().idUsuario(4).nombre("Lucía").build();

            SalaUsuario nina = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolNina)
                    .estaVivo(true)
                    .build();

            SalaUsuario salaUsuario2 = SalaUsuario.builder()
                    .idSalaUsuario(6)
                    .sala(sala)
                    .usuario(jugador2)
                    .rol(rolLobo)
                    .estaVivo(true)
                    .build();

            SalaUsuario salaUsuario3 = SalaUsuario.builder()
                    .idSalaUsuario(7)
                    .sala(sala)
                    .usuario(jugador3)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()))
                    .thenReturn(List.of(nina, salaUsuario2, salaUsuario3));

            HabilidadResultadoDto result = habilidadService.usarEspiar(sala, nina);

            assertNotNull(result);
            assertEquals("espiar", result.nombreHabilidad());
            assertEquals("LISTA_SOSPECHOSOS", result.resultado());
            assertEquals(2, result.objetivos().size());
            assertTrue(result.objetivos().contains("Pedro"));
        }

        @Test
        @DisplayName("Debe fallar si quien espía no es la Niña")
        void usarEspiar_deberiaFallarSiNoEsNina() {
            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarEspiar(sala, salaUsuarioVivo));
        }
    }

    @Nested
    @DisplayName("Modelo (Niño Salvaje)")
    class ModeloTests {

        @Test
        @DisplayName("Debe asignar modelo a un jugador vivo")
        void usarModelo_deberiaAsignarModelo() {
            Rol ninoSalvajeRol = Rol.builder()
                    .idRol(5)
                    .nombre("Niño salvaje")
                    .bando(Rol.Bando.aldea)
                    .build();

            Usuario modeloUsuario = Usuario.builder().idUsuario(3).nombre("Pedro").build();

            SalaUsuario ninoSalvaje = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(ninoSalvajeRol)
                    .estaVivo(true)
                    .build();

            SalaUsuario modelo = SalaUsuario.builder()
                    .idSalaUsuario(6)
                    .sala(sala)
                    .usuario(modeloUsuario)
                    .rol(rolLobo)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), 3))
                    .thenReturn(Optional.of(modelo));
            when(salaUsuarioRepository.save(any(SalaUsuario.class))).thenReturn(ninoSalvaje);

            HabilidadResultadoDto result = habilidadService.usarModelo(sala, ninoSalvaje, List.of(3));

            assertNotNull(result);
            assertEquals("modelo", result.nombreHabilidad());
            assertTrue(result.objetivos().contains("Pedro"));
            assertEquals("MODELO_ASIGNADO", result.resultado());
        }

        @Test
        @DisplayName("Debe fallar si ya tiene modelo asignado")
        void usarModelo_deberiaFallarSiYaTieneModelo() {
            SalaUsuario ninoSalvaje = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolNinoSalvaje)
                    .estaVivo(true)
                    .idModelo(3)
                    .build();

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarModelo(sala, ninoSalvaje, List.of(4)));
        }

        @Test
        @DisplayName("Debe fallar si se elige a sí mismo como modelo")
        void usarModelo_deberiaFallarSiEligeASiMismo() {
            SalaUsuario ninoSalvaje = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolNinoSalvaje)
                    .estaVivo(true)
                    .build();

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarModelo(sala, ninoSalvaje, List.of(1)));
        }

        @Test
        @DisplayName("Debe fallar si el modelo está eliminado")
        void usarModelo_deberiaFallarSiModeloEliminado() {
            SalaUsuario ninoSalvaje = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolNinoSalvaje)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), 2))
                    .thenReturn(Optional.of(salaUsuarioEliminado));

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarModelo(sala, ninoSalvaje, List.of(2)));
        }
    }

    @Nested
    @DisplayName("Disparo (Cazador)")
    class DisparoTests {

        @Test
        @DisplayName("Debe eliminar al objetivo cuando el cazador está eliminado")
        void usarDisparo_deberiaEliminarObjetivo() {
            Usuario objetivoVivo = Usuario.builder().idUsuario(3).nombre("Pedro").build();
            SalaUsuario objetivoVivoSU = SalaUsuario.builder()
                    .idSalaUsuario(10)
                    .sala(sala)
                    .usuario(objetivoVivo)
                    .rol(rolLobo)
                    .estaVivo(true)
                    .muerteConfirmada(false)
                    .build();

            SalaUsuario cazador = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCazador)
                    .estaVivo(false)
                    .muerteConfirmada(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), 3))
                    .thenReturn(Optional.of(objetivoVivoSU));
            when(salaUsuarioRepository.save(any(SalaUsuario.class))).thenReturn(objetivoVivoSU);
            doNothing().when(partidaSocketService).notificarMuerte(any(), any());

            HabilidadResultadoDto result = habilidadService.usarDisparo(sala.getCodigoSala(), sala, cazador, List.of(3));

            assertNotNull(result);
            assertEquals("disparo", result.nombreHabilidad());
            assertTrue(result.objetivos().contains("Pedro"));
            assertEquals("ELIMINADO", result.resultado());
        }

        @Test
        @DisplayName("Debe fallar si el cazador está vivo")
        void usarDisparo_deberiaFallarSiCazadorVivo() {
            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarDisparo(sala.getCodigoSala(), sala, salaUsuarioVivo, List.of(2)));
        }

        @Test
        @DisplayName("Debe fallar si el objetivo ya está eliminado")
        void usarDisparo_deberiaFallarSiObjetivoEliminado() {
            SalaUsuario cazador = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCazador)
                    .estaVivo(false)
                    .muerteConfirmada(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSalaAndUsuario_IdUsuario(sala.getIdSala(), usuarioEliminado.getIdUsuario()))
                    .thenReturn(Optional.of(salaUsuarioEliminado));

            assertThrows(IllegalStateException.class, () ->
                    habilidadService.usarDisparo(sala.getCodigoSala(), sala, cazador, List.of(2)));
        }

        @Test
        @DisplayName("Debe fallar si no se proporciona exactamente un objetivo")
        void usarDisparo_deberiaFallarSinObjetivoValido() {
            SalaUsuario cazador = SalaUsuario.builder()
                    .idSalaUsuario(5)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolCazador)
                    .estaVivo(false)
                    .muerteConfirmada(true)
                    .build();

            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarDisparo(sala.getCodigoSala(), sala, cazador, List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    habilidadService.usarDisparo(sala.getCodigoSala(), sala, cazador, List.of(1, 2)));
        }
    }

    @Nested
    @DisplayName("Comprobar Fin de Partida")
    class FinPartidaTests {

        @Test
        @DisplayName("Debe detectar victoria de aldea cuando no hay lobos vivos")
        void comprobarFinPartida_deberiaDetectarVictoriaAldea() {
            Usuario narrador = Usuario.builder().idUsuario(10).nombre("Narrador").build();
            sala.setNarrador(narrador);

            SalaUsuario jugador1 = SalaUsuario.builder()
                    .idSalaUsuario(10)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()))
                    .thenReturn(List.of(jugador1));

            habilidadService.comprobarFinPartida(sala.getCodigoSala(), sala);

            verify(salaRepository).save(sala);
            verify(partidaSocketService).notificarFinPartida(eq(sala.getCodigoSala()), any());
        }

        @Test
        @DisplayName("Debe detectar victoria de lobos cuando lobos >= aldea")
        void comprobarFinPartida_deberiaDetectarVictoriaLobos() {
            Usuario narrador = Usuario.builder().idUsuario(10).nombre("Narrador").build();
            sala.setNarrador(narrador);

            SalaUsuario aldea = SalaUsuario.builder()
                    .idSalaUsuario(10)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .build();

            SalaUsuario lobo = SalaUsuario.builder()
                    .idSalaUsuario(11)
                    .sala(sala)
                    .usuario(usuarioEliminado)
                    .rol(rolLobo)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()))
                    .thenReturn(List.of(aldea, lobo));

            habilidadService.comprobarFinPartida(sala.getCodigoSala(), sala);

            verify(salaRepository).save(sala);
            verify(partidaSocketService).notificarFinPartida(eq(sala.getCodigoSala()), any());
        }

        @Test
        @DisplayName("No debe terminar partida si hay lobos y aldea equilibrados")
        void comprobarFinPartida_noDebeTerminarSiEquilibrio() {
            Usuario narrador = Usuario.builder().idUsuario(10).nombre("Narrador").build();
            sala.setNarrador(narrador);

            SalaUsuario aldea1 = SalaUsuario.builder()
                    .idSalaUsuario(10)
                    .sala(sala)
                    .usuario(usuarioVivo)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .build();

            SalaUsuario aldea2 = SalaUsuario.builder()
                    .idSalaUsuario(11)
                    .sala(sala)
                    .usuario(usuarioEliminado)
                    .rol(rolAldeano)
                    .estaVivo(true)
                    .build();

            SalaUsuario lobo = SalaUsuario.builder()
                    .idSalaUsuario(12)
                    .sala(sala)
                    .usuario(Usuario.builder().idUsuario(5).nombre("Lobo").build())
                    .rol(rolLobo)
                    .estaVivo(true)
                    .build();

            when(salaUsuarioRepository.findBySala_IdSala(sala.getIdSala()))
                    .thenReturn(List.of(aldea1, aldea2, lobo));

            habilidadService.comprobarFinPartida(sala.getCodigoSala(), sala);

            verify(salaRepository, never()).save(any());
            verify(partidaSocketService, never()).notificarFinPartida(any(), any());
        }
    }
}