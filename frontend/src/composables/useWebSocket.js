import { ref, shallowRef } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useStore } from 'vuex'

const stompClient = shallowRef(null)
const isConnected = ref(false)
const connectionError = ref(null)

export function useWebSocket() {
  const store = useStore()

  const connect = (codigoSala) => {
    const token = store.getters['auth/token']

    if (!token || !codigoSala) {
      if (import.meta.env.DEV) {
        console.warn('[useWebSocket] Missing token or room code')
      }
      return Promise.reject(new Error('Missing token or room code'))
    }

    if (stompClient.value?.connected) {
      if (import.meta.env.DEV) {
        console.log('[useWebSocket] Already connected')
      }
      return Promise.resolve(stompClient.value)
    }

    return new Promise((resolve, reject) => {
      const client = new Client({
        webSocketFactory: () => new SockJS('/ws'),
        connectHeaders: { Authorization: `Bearer ${token}` },
      })

      client.onConnect = (frame) => {
        if (import.meta.env.DEV) {
          console.log('[useWebSocket] Connected, room:', codigoSala)
        }
        isConnected.value = true
        connectionError.value = null
        stompClient.value = client
        resolve(client)
      }

      client.onStompError = (frame) => {
        if (import.meta.env.DEV) {
          console.error('[useWebSocket] STOMP error:', frame)
        }
        connectionError.value = frame
        reject(frame)
      }

      client.onDisconnect = () => {
        if (import.meta.env.DEV) {
          console.log('[useWebSocket] Disconnected')
        }
        isConnected.value = false
      }

      client.activate()
    })
  }

  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.deactivate()
      stompClient.value = null
      isConnected.value = false
    }
  }

  const subscribe = (destination, callback) => {
    if (!stompClient.value?.connected) {
      if (import.meta.env.DEV) {
        console.warn('[useWebSocket] Not connected to subscribe:', destination)
      }
      return null
    }
    return stompClient.value.subscribe(destination, (msg) => {
      const payload = JSON.parse(msg.body)
      callback(payload)
    })
  }

  const publish = (destination, payload) => {
    if (!stompClient.value?.connected) {
      if (import.meta.env.DEV) {
        console.warn('[useWebSocket] Not connected to publish:', destination)
      }
      return
    }
    stompClient.value.publish({
      destination: destination,
      body: JSON.stringify(payload),
    })
  }

  return {
    stompClient,
    isConnected,
    connectionError,
    connect,
    disconnect,
    subscribe,
    publish,
  }
}