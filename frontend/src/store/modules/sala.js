export default {
  namespaced: true,

  state: () => ({
    codigoSala: null,
    esCreador: false,
    jugadores: [],
    jugadoresConRol: [],
    miRol: null,
    miRolDescripcion: null,
    miBando: null,
    bandoGanador: null,
    mensajeFin: null,
    fase: 'DIA',
    narradorActual: null,
    turnoActivo: null,
    semiMuertos: [],
    enamorados: null,
    cupidoUsado: false,
    tipoVotacion: null,
  }),

  mutations: {
    SET_SALA(state, { codigoSala, esCreador }) {
      state.codigoSala = codigoSala
      state.esCreador = esCreador
    },
    SET_JUGADORES(state, jugadores) {
      state.jugadores = jugadores
    },
    SET_JUGADORES_CON_ROL(state, jugadores) {
      state.jugadoresConRol = jugadores
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
      const j1 = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (j1) {
        j1.estaVivo = false
        j1.muerteConfirmada = true
      }
      const j2 = state.jugadoresConRol.find((j) => j.nombre === nombreJugador)
      if (j2) {
        j2.estaVivo = false
        j2.muerteConfirmada = true
      }
    },
    MARCAR_SEMIMUERTO(state, nombreJugador) {
      if (!state.semiMuertos.includes(nombreJugador)) {
        state.semiMuertos.push(nombreJugador)
      }
      const j1 = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (j1) {
        j1.estaVivo = false
        j1.muerteConfirmada = false
      }
      const j2 = state.jugadoresConRol.find((j) => j.nombre === nombreJugador)
      if (j2) {
        j2.estaVivo = false
        j2.muerteConfirmada = false
      }
    },
    QUITAR_SEMIMUERTO(state, nombreJugador) {
      state.semiMuertos = state.semiMuertos.filter((n) => n !== nombreJugador)
      const j1 = state.jugadores.find((j) => j.nombre === nombreJugador)
      if (j1) {
        j1.estaVivo = true
        j1.muerteConfirmada = false
      }
      const j2 = state.jugadoresConRol.find((j) => j.nombre === nombreJugador)
      if (j2) {
        j2.estaVivo = true
        j2.muerteConfirmada = false
      }
    },
    ACTUALIZAR_VOTOS(state, votos) {
      const actualizar = (lista) => {
        lista.forEach((j) => (j.votos = 0))
        if (Array.isArray(votos)) {
          votos.forEach((v) => {
            const obj = lista.find((j) => j.nombre === v.nombreObjetivo)
            if (obj) obj.votos = (obj.votos || 0) + 1
          })
        } else {
          Object.keys(votos).forEach((nombre) => {
            const obj = lista.find((j) => j.nombre === nombre)
            if (obj) obj.votos = votos[nombre]
          })
        }
      }
      actualizar(state.jugadores)
      actualizar(state.jugadoresConRol)
    },
    DESIGNAR_ALCALDE(state, nombreJugador) {
      const actualizar = (lista) => {
        lista.forEach((j) => (j.alcalde = false))
        const j = lista.find((j) => j.nombre === nombreJugador)
        if (j) j.alcalde = true
      }
      actualizar(state.jugadores)
      actualizar(state.jugadoresConRol)
    },
    TURNO_NOCTURNO(state, nombreJugador) {
      const actualizar = (lista) => {
        lista.forEach((j) => (j.esTurno = false))
        const j = lista.find((j) => j.nombre === nombreJugador)
        if (j) j.esTurno = true
      }
      actualizar(state.jugadores)
      actualizar(state.jugadoresConRol)
    },
    REINICIAR_VOTOS(state) {
      state.jugadores.forEach((j) => (j.votos = 0))
      state.jugadoresConRol.forEach((j) => (j.votos = 0))
    },
    SET_RESULTADO(state, { bandoGanador, mensaje }) {
      state.bandoGanador = bandoGanador
      state.mensajeFin = mensaje
    },
    SET_MI_BANDO(state, bando) {
      state.miBando = bando
    },
    CLEAR_SALA(state) {
      state.codigoSala = null
      state.esCreador = false
      state.jugadores = []
      state.jugadoresConRol = []
      state.miRol = null
      state.miRolDescripcion = null
      state.miBando = null
      state.fase = 'DIA'
      state.turnoActivo = null
      state.semiMuertos = []
      state.enamorados = null
      state.cupidoUsado = false
      state.tipoVotacion = null
    },
    SET_NARRADOR(state, nombreNarrador) {
      state.narradorActual = nombreNarrador
    },
    SET_TURNO_ACTIVO(state, jugador) {
      state.turnoActivo = jugador
    },
    SET_ENAMORADOS(state, { jugador1, jugador2 }) {
      state.enamorados = { jugador1, jugador2 }
    },
    SET_CUPIDO_USADO(state) {
      state.cupidoUsado = true
    },
    SET_TIPO_VOTACION(state, tipo) {
      state.tipoVotacion = tipo
    },
  },

  actions: {
    crearSala({ commit }, codigoSala) {
      localStorage.setItem('codigoSala', codigoSala)
      commit('SET_SALA', { codigoSala, esCreador: true })
    },
    unirse({ commit }, codigoSala) {
      localStorage.setItem('codigoSala', codigoSala)
      commit('SET_SALA', { codigoSala, esCreador: false })
    },
    setJugadores({ commit }, jugadores) {
      commit('SET_JUGADORES', jugadores)
    },
    setJugadoresConRol({ commit }, jugadores) {
      commit('SET_JUGADORES_CON_ROL', jugadores)
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
      localStorage.setItem('codigoSala')
      commit('CLEAR_SALA')
    },
    setNarrador({ commit }, nombreNarrador) {
      commit('SET_NARRADOR', nombreNarrador)
    },
    setTurnoActivo({ commit }, jugador) {
      commit('SET_TURNO_ACTIVO', jugador)
    },
    marcarSemimuerto({ commit }, nombreJugador) {
      commit('MARCAR_SEMIMUERTO', nombreJugador)
    },
    quitarSemimuerto({ commit }, nombreJugador) {
      commit('QUITAR_SEMIMUERTO', nombreJugador)
    },
    setEnamorados({ commit }, pareja) {
      commit('SET_ENAMORADOS', pareja)
    },
    setCupidoUsado({ commit }) {
      commit('SET_CUPIDO_USADO')
    },
    setTipoVotacion({ commit }, tipo) {
      commit('SET_TIPO_VOTACION', tipo)
    },
  },

  getters: {
    codigoSala:       (state) => state.codigoSala,
    esCreador:        (state) => state.esCreador,
    jugadores:        (state) => state.jugadores,
    jugadoresConRol:  (state) => state.jugadoresConRol,
    miRol:            (state) => state.miRol,
    miRolDescripcion: (state) => state.miRolDescripcion,
    miBando:          (state) => state.miBando,
    fase:             (state) => state.fase,
    bandoGanador:     (state) => state.bandoGanador,
    mensajeFin:       (state) => state.mensajeFin,
    narradorActual:   (state) => state.narradorActual,
    turnoActivo:      (state) => state.turnoActivo,
    semiMuertos:      (state) => state.semiMuertos,
    enamorados:       (state) => state.enamorados,
    cupidoUsado:      (state) => state.cupidoUsado,
    tipoVotacion:     (state) => state.tipoVotacion,
  },
}