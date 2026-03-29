// Ahora usa GameEvents que centraliza todo en Vuex
import { GameEvents } from '@/store/GameEvents'

export function enviarEvento(tipo, data) {
  // plantilla envío backend — pendiente de implementar con stompClient
  /*
  stompClient.send(
    "/app/evento",
    {},
    JSON.stringify({ tipo, ...data })
  )
  */
}

export function procesarEvento(payload) {
  switch (payload.tipo) {
    case 'FASE':
      GameEvents.cambiarFase(payload.fase)
      break

    case 'VOTACION':
      GameEvents.procesarVotacion(payload.votos)
      break

    case 'MUERTE':
      GameEvents.marcarMuerto(payload.jugador)
      break

    case 'ALCALDE':
      GameEvents.designarAlcalde(payload.jugador)
      break

    case 'TURNO':
      GameEvents.turnoNocturno(payload.jugador)
      break

    case 'REINICIAR_VOTOS':
      GameEvents.reiniciarVotos()
      break

    default:
      console.warn('Evento desconocido:', payload.tipo)
  }
}