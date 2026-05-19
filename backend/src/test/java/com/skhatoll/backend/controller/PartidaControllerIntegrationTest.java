package com.skhatoll.backend.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Tests de Integración - Partida Controller")
class PartidaControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Nested
    @DisplayName("Cambio de Fase")
    class CambioFaseTests {

        @Test
        @DisplayName("Debe fallar sin autenticación")
        void cambiarFase_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(put("/partida/{codigo}/fase", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Votación")
    class VotacionTests {

        @Test
        @DisplayName("Debe fallar sin autenticación al abrir")
        void abrirVotacion_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/votacion/abrir", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tipo\":\"DIA\"}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar con tipo inválido")
        void abrirVotacion_deberiaFallarConTipoInvalido() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/votacion/abrir", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tipo\":\"INVALIDO\"}"))
                    .andExpect(result -> status().is5xxServerError().match(result));
        }

        @Test
        @DisplayName("Debe fallar sin autenticación al votar")
        void votar_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/votar", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idObjetivo\":1}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar sin autenticación al cerrar votación")
        void cerrarVotacion_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(put("/partida/{codigo}/votacion/1/cerrar", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar sin autenticación al confirmar muerte")
        void confirmarMuerte_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(put("/partida/{codigo}/jugador/1/confirmar-muerte", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar sin autenticación al obtener sesión activa")
        void getSesionActiva_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(get("/partida/{codigo}/sesion-activa", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Habilidades")
    class HabilidadTests {

        @Test
        @DisplayName("Debe fallar si la sala no existe")
        void usarHabilidad_deberiaFallarSiSalaNoExiste() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/habilidad", "CODIGO_INEXISTENTE")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombreHabilidad\":\"vision\",\"objetivos\":[1]}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar con habilidad inválida")
        void usarHabilidad_deberiaFallarConHabilidadInvalida() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/habilidad", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombreHabilidad\":\"habilidad_invalida\",\"objetivos\":[1]}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar con objetivos vacíos")
        void usarHabilidad_deberiaFallarConObjetivosVacios() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/habilidad", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombreHabilidad\":\"vision\",\"objetivos\":[]}"))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Debe fallar sin autenticación")
        void usarHabilidad_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(post("/partida/{codigo}/habilidad", "TEST123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nombreHabilidad\":\"vision\",\"objetivos\":[1]}"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Estado de Partida")
    class EstadoPartidaTests {

        @Test
        @DisplayName("Debe fallar sin autenticación")
        void getEstadoPartida_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(get("/partida/{codigo}/estado", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Cerrar Partida")
    class CerrarPartidaTests {

        @Test
        @DisplayName("Debe fallar sin autenticación")
        void cerrarPartida_deberiaFallarSinAutenticacion() throws Exception {
            mockMvc.perform(put("/partida/{codigo}/cerrar", "TEST123"))
                    .andExpect(status().is4xxClientError());
        }
    }
}