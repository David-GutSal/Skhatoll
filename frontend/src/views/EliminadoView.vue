<template>
  <div class="eliminado-pagina">
    <div class="eliminado-contenedor">
      <div class="recuadro">
        <h1 class="titulo">
          Has sido eliminado
          <i class="fa-solid fa-skull-crossbones"></i>
        </h1>

        <img src="@/assets/imgs/eliminados.png" alt="Eliminado" class="imagen-eliminado" />

        <p class="texto">
          No importa si fue una copa envenenada, un disparo certero, o te convertiste en pienso para
          lobos... Lo que importa es que has sido eliminado de la partida...
        </p>
        <p class="texto">
          Regresa al inicio para jugar una nueva partida o espera a ver los resultados de esta. ¡A
          ver si hay suerte y al menos gana tu equipo!
        </p>
      </div>

      <div class="botones">
        <button class="btn-eliminado" @click="irInicio">Volver a Inicio</button>
        <button class="btn-eliminado" @click="togglePartida">Ver Partida</button>
      </div>

      <transition name="desplegable">
        <div v-if="verPartida" class="mesa-desplegable">
          <MesaJugadores :jugadores="jugadores" :esDia="esDia" :modoNarrador="true" />
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'

const router = useRouter()
const store = useStore()

const verPartida = ref(false)
const esDia = ref(true)
const stompClient = ref(null)

const codigoSala = computed(() => store.getters['sala/codigoSala'] || sessionStorage.getItem('codigoSala'))
const jugadores = computed(() => store.getters['sala/jugadores'])

const togglePartida = () => {
  verPartida.value = !verPartida.value
}

const irInicio = () => {
  store.dispatch('sala/salir')
  router.push({ name: 'inicio' })
}

const conectarWebSocket = () => {
  const token = store.getters['auth/token']
  const cliente = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    connectHeaders: { Authorization: `Bearer ${token}` },
  })

  cliente.onConnect = () => {
    cliente.subscribe(`/topic/partida/${codigoSala.value}/fase`, (msg) => {
      const payload = JSON.parse(msg.body)
      esDia.value = payload.fase === 'DIA'
      store.dispatch('sala/setFase', payload.fase)
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/muerte`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (payload.muerteConfirmada) {
        store.dispatch('sala/marcarMuerto', payload.nombreJugador)
      } else {
        store.dispatch('sala/marcarSemimuerto', payload.nombreJugador)
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/fin`, (msg) => {
      const payload = JSON.parse(msg.body)
      store.dispatch('sala/setResultado', {
        bandoGanador: payload.bandoGanador,
        mensaje: payload.mensaje,
      })
      router.push({ name: 'resultados' })
    })
  }

  cliente.activate()
  stompClient.value = cliente
}

esDia.value = store.getters['sala/fase'] !== 'NOCHE'

onMounted(async () => {
  store.dispatch('sala/restaurarEstado')
  await new Promise((r) => setTimeout(r, 50))
  conectarWebSocket()
})

onUnmounted(() => {
  if (stompClient.value) {
    stompClient.value.deactivate()
    stompClient.value = null
  }
})
</script>

<style scoped>
.eliminado-pagina {
  min-height: 100vh;
  background-image: url('@/assets/imgs/fondo.png');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 60px 0 60px;
}

.eliminado-contenedor {
  width: 70%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}

.recuadro {
  width: 100%;
  background: #2a2a2a;
  border-radius: 15px;
  border: 10px solid #000;
  padding: 45px;
  box-sizing: border-box;
}

.titulo {
  font-family: var(--font-cinzel);
  font-size: 2.5rem;
  font-weight: bold;
  color: var(--color-rojo);
  text-align: center;
  margin-bottom: 20px;
}

.imagen-eliminado {
  display: block;
  width: 100%;
  margin: 20px 0;
  border: 5px solid var(--color-rojo);
  object-fit: cover;
}

.texto {
  font-family: var(--font-raleway);
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-align: left;
  line-height: 1.4;
  margin: 4px auto;
}

.botones {
  display: flex;
  gap: 24px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-eliminado {
  background: #000;
  color: var(--color-rojo);
  border: none;
  padding: 14px 32px;
  border-radius: 10px;
  font-family: var(--font-raleway);
  font-size: 1.5rem;
  font-weight: 700;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    transform 0.15s ease;
}

.btn-eliminado:hover {
  background: white;
  color: #000;
}

.btn-eliminado:active {
  transform: scale(0.96);
}

.mesa-desplegable {
  width: 100%;
}

.desplegable-enter-active,
.desplegable-leave-active {
  transition: opacity 0.3s ease;
}

.desplegable-enter-from,
.desplegable-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .eliminado-contenedor {
    width: 90%;
  }
  .recuadro {
    padding: 24px;
  }
  .titulo {
    font-size: 1.8rem;
  }
  .texto {
    font-size: 1rem;
  }
  .btn-eliminado {
    font-size: 1.1rem;
    padding: 12px 20px;
  }
}

@media (max-width: 480px) {
  .eliminado-contenedor {
    width: 95%;
  }
  .botones {
    flex-direction: column;
    align-items: stretch;
  }
  .btn-eliminado {
    text-align: center;
  }
}
</style>
