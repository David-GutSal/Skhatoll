import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'
import { useWebSocket } from './useWebSocket'

const players = ref([])
const gameActive = ref(null)
const isDay = ref(true)
const voteActive = ref(false)
const voteType = ref(null)
const currentTurn = ref(null)
const eventMessage = ref('')

export function usePartida() {
  const store = useStore()
  const { stompClient, publish } = useWebSocket()

  const roomCode = computed(() => {
    return store.getters['sala/codigoSala'] || sessionStorage.getItem('codigoSala')
  })

  const myData = computed(() => {
    const nombre = store.getters['auth/nombre']
    return players.value.find(j => j.nombre === nombre)
  })

  const soyNarrador = computed(() => {
    const nombreNarrador = store.getters['sala/nombreNarrador']
    return nombreNarrador === store.getters['auth/nombre']
  })

  const loadPlayers = async () => {
    const codigo = roomCode.value
    if (!codigo) return
    try {
      const res = await axiosInstance.get(`/salas/${codigo}/jugadores`)
      players.value = res.data
      store.dispatch('sala/setJugadores', res.data)
    } catch (error) {
      console.error('[usePartida] Error loading players:', error)
    }
  }

  const loadGameState = async () => {
    const codigo = roomCode.value
    if (!codigo) return
    try {
      const res = await axiosInstance.get(`/partida/${codigo}/estado`)
      gameActive.value = res.data
      isDay.value = res.data.esDia
      voteActive.value = res.data.votacionActiva
      voteType.value = res.data.tipoVotacion
      currentTurn.value = res.data.turnoActual
    } catch (error) {
      console.error('[usePartida] Error loading state:', error)
    }
  }

  const vote = async (targetId) => {
    const codigo = roomCode.value
    if (!codigo || !targetId) return
    try {
      await axiosInstance.post(`/partida/${codigo}/votar`, { idObjetivo: targetId })
      store.dispatch('toast/mostrar', { mensaje: 'Vote sent', tipo: 'success' })
    } catch (error) {
      store.dispatch('toast/mostrar', { mensaje: 'Error voting', tipo: 'error' })
    }
  }

  const subscribeToGameEvents = (onMessage) => {
    const codigo = roomCode.value
    if (!codigo || !stompClient.value?.connected) return null

    const inicioSubscription = stompClient.value.subscribe(`/topic/sala/${codigo}/inicio`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (onMessage) onMessage('start', payload)
    })

    const partidaSubscription = stompClient.value.subscribe(`/topic/partida/${codigo}`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (onMessage) onMessage('game', payload)
    })

    const turnoSubscription = stompClient.value.subscribe(`/topic/partida/${codigo}/turno`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (onMessage) onMessage('turn', payload)
    })

    return {
      unsubscribe: () => {
        inicioSubscription?.unsubscribe()
        partidaSubscription?.unsubscribe()
        turnoSubscription?.unsubscribe()
      }
    }
  }

  const getEventMessage = (tipo, payload) => {
    switch (tipo) {
      case 'DIA':
        return 'Public vote: Who is the werewolf?'
      case 'LOBOS':
        return 'The werewolves are choosing their prey!'
      case 'ALCALDE':
        return 'Vote to elect the mayor'
      case 'NOCHE':
        return 'The village sleeps'
      default:
        return payload?.mensaje || ''
    }
  }

  return {
    players,
    gameActive,
    isDay,
    voteActive,
    voteType,
    currentTurn,
    eventMessage,
    roomCode,
    myData,
    soyNarrador,
    loadPlayers,
    loadGameState,
    vote,
    subscribeToGameEvents,
    getEventMessage,
  }
}