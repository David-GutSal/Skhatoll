export default {
  namespaced: true,

  state: () => ({
    codigoSala: null,
    esCreador: false,
    jugadores: [],
    miRol: null,
    miRolDescripcion: null,
    miBando: null,
    bandoGanador: null,
    mensajeFin: null,
    fase: 'DIA',
  }),

  mutations: {
    SET_SALA(state, { codigoSala, esCreador }) {
      state.codigoSala = codigoSala
      state.esCreador = esCreador
    },
    SET_JUGADORES(state, jugadores) {
      state.jugadores = jugadores
    },
    SET_ROL(state, { nombreRol, descripcionRol, bando }) {
      state.miRol = nombreRol
      state.miRolDescripcion = descripcionRol
      state.miBando = bando
    },
    SET_FASE(state, fase) {
      state.fase = fase
    },
    MARCAR_MUERTO(state, nombreJugador) {
      const jugador = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (jugador) jugador.estaVivo = false
    },
    ACTUALIZAR_VOTOS(state, votos) {
      // Resetear votos
      state.jugadores.forEach((j) => (j.votos = 0))
      
      if (Array.isArray(votos)) {
        votos.forEach((v) => {
          const objetivo = state.jugadores.find((j) => j.nombre === v.nombreObjetivo)
          if (objetivo) objetivo.votos = (objetivo.votos || 0) + 1
        })
      } else {
        Object.keys(votos).forEach((nombre) => {
          const jugador = state.jugadores.find((j) => j.nombre === nombre)
          if (jugador) jugador.votos = votos[nombre]
        })
      }
    },
    DESIGNAR_ALCALDE(state, nombreJugador) {
      state.jugadores.forEach((j) => (j.alcalde = false))
      const jugador = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (jugador) jugador.alcalde = true
    },
    TURNO_NOCTURNO(state, nombreJugador) {
      state.jugadores.forEach((j) => (j.esTurno = false))
      const jugador = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (jugador) jugador.esTurno = true
    },
    REINICIAR_VOTOS(state) {
      state.jugadores.forEach((j) => (j.votos = 0))
    },
    SET_RESULTADO(state, { bandoGanador, mensaje }) {
      state.bandoGanador = bandoGanador
      state.mensajeFin = mensaje
    },
    CLEAR_SALA(state) {
      state.codigoSala = null
      state.esCreador = false
      state.jugadores = []
      state.miRol = null
      state.miRolDescripcion = null
      state.miBando = null
      state.fase = 'DIA'
    },
  },

  actions: {
    crearSala({ commit }, codigoSala) {
      commit('SET_SALA', { codigoSala, esCreador: true })
    },
    unirse({ commit }, codigoSala) {
      commit('SET_SALA', { codigoSala, esCreador: false })
    },
    setJugadores({ commit }, jugadores) {
      commit('SET_JUGADORES', jugadores)
    },
    setRol({ commit }, rol) {
      commit('SET_ROL', rol)
    },
    setFase({ commit }, fase) {
      if (fase !== 'DIA' && fase !== 'NOCHE') return
      commit('SET_FASE', fase)
    },
    marcarMuerto({ commit }, nombreJugador) {
      commit('MARCAR_MUERTO', nombreJugador)
    },
    actualizarVotos({ commit }, votos) {
      commit('ACTUALIZAR_VOTOS', votos)
    },
    designarAlcalde({ commit }, nombreJugador) {
      commit('DESIGNAR_ALCALDE', nombreJugador)
    },
    turnoNocturno({ commit }, nombreJugador) {
      commit('TURNO_NOCTURNO', nombreJugador)
    },
    reiniciarVotos({ commit }) {
      commit('REINICIAR_VOTOS')
    },
    setResultado({ commit }, resultado) {
      commit('SET_RESULTADO', resultado)
    },
    salir({ commit }) {
      commit('CLEAR_SALA')
    },
  },

  getters: {
    codigoSala:       (state) => state.codigoSala,
    esCreador:        (state) => state.esCreador,
    jugadores:        (state) => state.jugadores,
    miRol:            (state) => state.miRol,
    miRolDescripcion: (state) => state.miRolDescripcion,
    miBando:          (state) => state.miBando,
    fase:             (state) => state.fase,
    bandoGanador:     (state) => state.bandoGanador,
    mensajeFin:       (state) => state.mensajeFin,
  },
}