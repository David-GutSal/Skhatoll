
import { gameState } from './gameState'


export const GameEvents = {
  cambiarFase(fase) {
    if (fase !== 'DIA' && fase !== 'NOCHE') return
    gameState.fase = fase
    console.log(`Fase cambiada a: ${fase}`)
  },

  procesarVotacion(votos) {
    Object.keys(votos).forEach((nombre) => {
      const jugador = gameState.jugadores.find((j) => j.nombre === nombre)
      if (jugador) jugador.votos = votos[nombre]
    })
    console.log(
      'Votos actualizados:',
      gameState.jugadores.map((j) => ({ nombre: j.nombre, votos: j.votos })),
    )
  },

  marcarMuerto(nombreJugador) {
    const jugador = gameState.jugadores.find((j) => j.nombre === nombreJugador)
    if (jugador) {
      jugador.estaVivo = false
      console.log(`Jugador ${nombreJugador} ha muerto`)
    }
  },

  designarAlcalde(nombreJugador) {
    gameState.jugadores.forEach((j) => (j.alcalde = false))
    const jugador = gameState.jugadores.find((j) => j.nombre === nombreJugador)
    if (jugador) jugador.alcalde = true
    console.log(`Jugador ${nombreJugador} es el nuevo alcalde`)
  },

  turnoNocturno(nombreJugador) {
    gameState.jugadores.forEach((j) => (j.esTurno = false))
    const jugador = gameState.jugadores.find((j) => j.nombre === nombreJugador)
    if (jugador) jugador.esTurno = true
    console.log(`Es el turno de ${nombreJugador} en la noche`)
  },

  reiniciarVotos() {
    gameState.jugadores.forEach((j) => (j.votos = 0))
    console.log('Votos reiniciados')
  },

  eventoSimulado(tipo, payload) {
    switch (tipo) {
      case 'FASE':
        this.cambiarFase(payload.fase)
        break
      case 'VOTACION':
        this.procesarVotacion(payload.votos)
        break
      case 'MUERTE':
        this.marcarMuerto(payload.jugador)
        break
      case 'ALCALDE':
        this.designarAlcalde(payload.jugador)
        break
      case 'TURNO':
        this.turnoNocturno(payload.jugador)
        break
      case 'REINICIAR_VOTOS':
        this.reiniciarVotos()
        break
      default:
        console.warn('Evento desconocido:', tipo)
    }
  },
}
