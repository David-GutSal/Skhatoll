// GameEvents unificado — ahora usa Vuex en vez de gameState
// Úsalo en cualquier componente: import { GameEvents } from '@/store/GameEvents'

import store from '@/store'

export const GameEvents = {

  cambiarFase(fase) {
    store.dispatch('sala/setFase', fase)
  },

  procesarVotacion(votos) {
    store.dispatch('sala/actualizarVotos', votos)
  },

  marcarMuerto(nombreJugador) {
    store.dispatch('sala/marcarMuerto', nombreJugador)
  },

  designarAlcalde(nombreJugador) {
    store.dispatch('sala/designarAlcalde', nombreJugador)
  },

  turnoNocturno(nombreJugador) {
    store.dispatch('sala/turnoNocturno', nombreJugador)
  },

  reiniciarVotos() {
    store.dispatch('sala/reiniciarVotos')
  },

  // Mantiene compatibilidad con el sistema de simulación anterior
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