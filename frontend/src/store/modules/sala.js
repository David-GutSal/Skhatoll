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
    brujaPocionVidaUsada: false,
    brujaPocionMuerteUsada: false,
    mentorNinno: null,
  }),

  mutations: {
    SET_SALA(state, { codigoSala, esCreador }) {
      state.codigoSala = codigoSala
      state.esCreador = esCreador
    },
    RESET_SALA(state) {
      state.jugadores = []
      state.jugadoresConRol = []
      state.tipoVotacion = null
      state.enamorados = null
      state.semiMuertos = []
    },
    SET_JUGADORES(state, jugadores) {
      state.jugadores = jugadores
    },
    SET_JUGADORES_CON_ROL(state, jugadores) {
      state.jugadoresConRol = jugadores
    },
    SET_ROL(state, { nombreRol, descripcionRol, bando, nombreJugador }) {
      state.miRol = nombreRol
      state.miRolDescripcion = descripcionRol
      state.miBando = bando
      if (nombreJugador) {
        state.jugadoresConRol = state.jugadoresConRol.map((j) =>
          j.nombre === nombreJugador ? { ...j, nombreRol, bando } : j
        )
      }
    },
    SET_FASE(state, fase) {
      state.fase = fase
    },
    MARCAR_MUERTO(state, nombreJugador) {
      state.jugadores = state.jugadores.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: false, muerteConfirmada: true }
          : j
      )
      state.jugadoresConRol = state.jugadoresConRol.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: false, muerteConfirmada: true }
          : j
      )
    },
    MARCAR_SEMIMUERTO(state, nombreJugador) {
      if (!state.semiMuertos.includes(nombreJugador)) {
        state.semiMuertos = [...state.semiMuertos, nombreJugador]
      }
      state.jugadores = state.jugadores.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: false, muerteConfirmada: false }
          : j
      )
      state.jugadoresConRol = state.jugadoresConRol.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: false, muerteConfirmada: false }
          : j
      )
    },
    QUITAR_SEMIMUERTO(state, nombreJugador) {
      state.semiMuertos = state.semiMuertos.filter((n) => n !== nombreJugador)
      state.jugadores = state.jugadores.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: true, muerteConfirmada: false }
          : j
      )
      state.jugadoresConRol = state.jugadoresConRol.map((j) =>
        j.nombre === nombreJugador
          ? { ...j, estaVivo: true, muerteConfirmada: false }
          : j
      )
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
      state.brujaPocionVidaUsada = false
      state.brujaPocionMuerteUsada = false
      state.mentorNinno = null
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
    SET_BRUJA_POCION_VIDA(state) {
      state.brujaPocionVidaUsada = true
    },
    SET_BRUJA_POCION_MUERTE(state) {
      state.brujaPocionMuerteUsada = true
    },
    SET_MENTOR_NINNO(state, nombreMentor) {
      state.mentorNinno = nombreMentor
      state.narradorActual = nombreMentor
    },
  },

  actions: {
    crearSala({ commit }, codigoSala) {
      sessionStorage.setItem('codigoSala', codigoSala)
      sessionStorage.setItem('esCreador', 'true')
      commit('SET_SALA', { codigoSala, esCreador: true })
    },
    unirse({ commit }, codigoSala) {
      sessionStorage.setItem('codigoSala', codigoSala)
      sessionStorage.removeItem('esCreador')
      commit('SET_SALA', { codigoSala, esCreador: false })
    },
    setSala({ commit }, { codigoSala, esCreador }) {
      sessionStorage.setItem('codigoSala', codigoSala)
      if (esCreador) {
        sessionStorage.setItem('esCreador', 'true')
      } else {
        sessionStorage.removeItem('esCreador')
      }
      commit('SET_SALA', { codigoSala, esCreador })
    },
    restaurarEstado({ commit }) {
      const codigo = sessionStorage.getItem('codigoSala')
      const esCreador = sessionStorage.getItem('esCreador') === 'true'
      if (codigo) {
        commit('SET_SALA', { codigoSala: codigo, esCreador })
      }
      const miRol = sessionStorage.getItem('miRol')
      const miRolDescripcion = sessionStorage.getItem('miRolDescripcion')
      const miBando = sessionStorage.getItem('miBando')
      if (miRol) {
        commit('SET_ROL', { nombreRol: miRol, descripcionRol: miRolDescripcion, bando: miBando })
      }
    },
    salir({ commit }) {
      sessionStorage.removeItem('codigoSala')
      sessionStorage.removeItem('esCreador')
      sessionStorage.removeItem('miRol')
      sessionStorage.removeItem('miRolDescripcion')
      sessionStorage.removeItem('miBando')
      commit('CLEAR_SALA')
    },
    resetSala({ commit }) {
      commit('RESET_SALA')
    },
    setJugadores({ commit }, jugadores) {
      commit('SET_JUGADORES', jugadores)
    },
    setJugadoresConRol({ commit }, jugadores) {
      commit('SET_JUGADORES_CON_ROL', jugadores)
    },
    setRol({ commit }, rol) {
      sessionStorage.setItem('miRol', rol.nombreRol)
      sessionStorage.setItem('miRolDescripcion', rol.descripcionRol)
      sessionStorage.setItem('miBando', rol.bando)
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
    setBrujaPocionVida({ commit }) {
      commit('SET_BRUJA_POCION_VIDA')
    },
    setBrujaPocionMuerte({ commit }) {
      commit('SET_BRUJA_POCION_MUERTE')
    },
    setMentorNinno({ commit }, nombreMentor) {
      commit('SET_MENTOR_NINNO', nombreMentor)
      commit('SET_NARRADOR', nombreMentor)
    },
  },

  getters: {
    codigoSala: (state) => state.codigoSala,
    esCreador: (state) => state.esCreador,
    jugadores: (state) => state.jugadores,
    jugadoresConRol: (state) => state.jugadoresConRol,
    miRol: (state) => state.miRol,
    miRolDescripcion: (state) => state.miRolDescripcion,
    miBando: (state) => state.miBando,
    fase: (state) => state.fase,
    bandoGanador: (state) => state.bandoGanador,
    mensajeFin: (state) => state.mensajeFin,
    narradorActual: (state) => state.narradorActual,
    turnoActivo: (state) => state.turnoActivo,
    semiMuertos: (state) => state.semiMuertos,
    enamorados: (state) => state.enamorados,
    cupidoUsado: (state) => state.cupidoUsado,
    tipoVotacion: (state) => state.tipoVotacion,
    brujaPocionVidaUsada: (state) => state.brujaPocionVidaUsada,
    brujaPocionMuerteUsada: (state) => state.brujaPocionMuerteUsada,
    mentorNinno: (state) => state.mentorNinno,
  },
}
