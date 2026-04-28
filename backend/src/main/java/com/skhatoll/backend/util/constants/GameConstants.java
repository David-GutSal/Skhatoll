package com.skhatoll.backend.util.constants;

public final class GameConstants {

    private GameConstants() {}

    // Nombre de bandos
    public static final String BANDO_LOBO       = "lobo";
    public static final String BANDO_ALDEA      = "aldea";

    // Nombres de roles
    public static final String ROL_LOBO          = "Lobo";
    public static final String ROL_ALDEANO       = "Aldeano";
    public static final String ROL_VIDENTE       = "Vidente";
    public static final String ROL_CAZADOR       = "Cazador";
    public static final String ROL_BRUJA         = "Bruja";
    public static final String ROL_NINO_SALVAJE  = "Niño salvaje";
    public static final String ROL_CUPIDO        = "Cupido";
    public static final String ROL_NINA          = "Niña";
    public static final String ROL_SIN_ROL          = "Sin rol";

    // Nombres de habilidades
    public static final String HAB_POCION_VIDA   = "pocion_vida";
    public static final String HAB_POCION_MUERTE = "pocion_muerte";
    public static final String HAB_DISPARO       = "disparo";
    public static final String HAB_FLECHAZO      = "flechazo";
    public static final String HAB_VISION        = "vision";
    public static final String HAB_ESPIAR        = "espiar";
    public static final String HAB_MODELO        = "modelo";

    // Resultados de habilidades
    public static final String RES_REVIVIDO         = "REVIVIDO";
    public static final String RES_ELIMINADO        = "ELIMINADO";
    public static final String RES_ENAMORADOS       = "ENAMORADOS";
    public static final String RES_ROL_REVELADO     = "ROL_REVELADO";
    public static final String RES_LISTA_SOSPECHOSOS = "LISTA_SOSPECHOSOS";
    public static final String RES_MODELO_ASIGNADO = "MODELO_ASIGNADO";

    // Tipos de eventos WebSocket
    public static final String WS_EVENTO_JUGADOR_UNIDO   = "JUGADOR_UNIDO";
    public static final String WS_EVENTO_PARTIDA_INICIADA = "PARTIDA_INICIADA";
    public static final String WS_EVENTO_ROL_ASIGNADO    = "ROL_ASIGNADO";
    public static final String WS_EVENTO_ROL_CAMBIADO    = "ROL_CAMBIADO";
    public static final String WS_EVENTO_CAMBIO_FASE     = "CAMBIO_FASE";
    public static final String WS_EVENTO_VOTACION_ABIERTA = "VOTACION_ABIERTA";
    public static final String WS_EVENTO_VOTACION_CERRADA = "VOTACION_CERRADA";
    public static final String WS_EVENTO_VOTOS_ACTUALIZADOS = "VOTOS_ACTUALIZADOS";
    public static final String WS_EVENTO_ALCALDE_ELEGIDO = "ALCALDE_ELEGIDO";
    public static final String WS_EVENTO_COMPANEROS_LOBOS = "COMPANEROS_LOBOS";
    public static final String WS_EVENTO_JUGADOR_SALIO = "JUGADOR_SALIO";

    // Canales WebSocket
    public static final String WS_SALA          = "/topic/sala/%s";
    public static final String WS_SALA_INICIO   = "/topic/sala/%s/inicio";
    public static final String WS_FASE          = "/topic/partida/%s/fase";
    public static final String WS_VOTACION      = "/topic/partida/%s/votacion";
    public static final String WS_VOTOS         = "/topic/partida/%s/votos";
    public static final String WS_MUERTE        = "/topic/partida/%s/muerte";
    public static final String WS_FIN           = "/topic/partida/%s/fin";
    public static final String WS_ALCALDE       = "/topic/partida/%s/alcalde";
    public static final String WS_QUEUE_ROL     = "/queue/rol";
    public static final String WS_QUEUE_LOBOS   = "/queue/lobos";


}
