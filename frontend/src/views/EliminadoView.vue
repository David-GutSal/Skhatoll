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
          <MesaJugadores :jugadores="jugadores" :esDia="esDia" :modoNarrador="false" />
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'

export default {
  name: 'EliminadoView',
  components: { MesaJugadores },

  data() {
    return {
      verPartida: false,
      esDia: true,
      stompClient: null,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala', 'jugadores', 'fase']),
  },

  created() {
    this.esDia = this.$store.getters['sala/fase'] !== 'NOCHE'
    this.conectarWebSocket()
  },

  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate()
      this.stompClient = null
    }
  },

  methods: {
    togglePartida() {
      this.verPartida = !this.verPartida
    },

    irInicio() {
      this.$store.dispatch('sala/salir')
      this.$router.push({ name: 'inicio' })
    },

    conectarWebSocket() {
      const token = this.$store.getters['auth/token']
      const cliente = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        connectHeaders: { Authorization: `Bearer ${token}` },
      })

      cliente.onConnect = () => {
        cliente.subscribe(`/topic/partida/${this.codigoSala}/fase`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.esDia = payload.fase === 'DIA'
          this.$store.dispatch('sala/setFase', payload.fase)
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/muerte`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.$store.dispatch('sala/marcarMuerto', payload.nombreJugador)
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/fin`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.$store.dispatch('sala/setResultado', {
            bandoGanador: payload.bandoGanador,
            mensaje: payload.mensaje,
          })
          this.$router.push({ name: 'resultados' })
        })
      }

      cliente.activate()
      this.stompClient = cliente
    },
  },
}
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
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.5rem;
  font-weight: bold;
  color: #cc0000;
  text-align: center;
  margin-bottom: 20px;
}

.imagen-eliminado {
  display: block;
  width: 100%;
  margin: 20px 0;
  border: 5px solid #cc0000;
  object-fit: cover;
}

.texto {
  font-family: 'Raleway', Arial, sans-serif;
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
  color: #cc0000;
  border: none;
  padding: 14px 32px;
  border-radius: 10px;
  font-family: 'Raleway', Arial, sans-serif;
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
