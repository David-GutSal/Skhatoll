<template>
  <div class="contenedor-narrador" :class="{ noche: !esDia }">

    <div class="cabecera">
      <div class="narrador-box">
        <i class="fa-solid fa-book-open-reader"></i> Narrador: {{ nombre }}
      </div>
      <IndicadorDiaNoche :esDia="esDia" @cambiarFase="cambiarFase" />
    </div>

    <PanelControlNarrador
      :esDia="esDia"
      :hayAlcalde="hayAlcalde"
      @votarLinchamiento="iniciarVotacionLinchamiento"
      @votarAlcalde="iniciarVotacionAlcalde"
      @finalizarVotacion="finalizarVotacion"
      @eventos="iniciarEventos"
    />

    <!-- El narrador ve la mesa con roles visibles -->
    <MesaJugadores
      :jugadores="jugadoresConRol"
      :esDia="esDia"
      :modoNarrador="true"
      @seleccionarJugador="activarTurnoJugador"
    />

  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { mapGetters } from 'vuex'
import IndicadorDiaNoche from '@/components/juego/IndicadorDiaNoche.vue'
import PanelControlNarrador from '@/components/juego/PanelControlNarrador.vue'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'

export default {
  name: 'NarradorView',

  components: {
    IndicadorDiaNoche,
    PanelControlNarrador,
    MesaJugadores,
  },

  data() {
    return {
      esDia: true,
      stompClient: null,
      idSesionActual: null,
    }
  },

  computed: {
    ...mapGetters('auth', ['nombre']),
    ...mapGetters('sala', ['codigoSala', 'jugadoresConRol']),
    hayAlcalde() {
      return this.jugadoresConRol.some((j) => j.alcalde)
    },
  },

  async created() {
    // El narrador carga la lista con roles visibles
    try {
      const res = await axiosInstance.get(`/salas/${this.codigoSala}/roles`)
      this.$store.dispatch('sala/setJugadoresConRol', res.data)
    } catch (error) {
      alert('Error al cargar jugadores')
    }
    this.conectarWebSocket()
  },

  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate()
      this.stompClient = null
    }
  },

  methods: {
    async cambiarFase(fase) {
      try {
        this.esDia = fase === 'dia'
        await axiosInstance.put(`/partida/${this.codigoSala}/fase`)
      } catch (error) {
        alert(
          error.response?.status === 409
            ? 'Cierra la votación antes de cambiar la fase'
            : 'Error al cambiar la fase',
        )
      }
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
        cliente.subscribe(`/topic/partida/${this.codigoSala}/votos`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.$store.dispatch('sala/actualizarVotos', payload.votos)
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

    async iniciarVotacionLinchamiento() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
          tipo: 'DIA',
        })
        this.idSesionActual = res.data
      } catch (error) {
        alert('Error al iniciar votación')
      }
    },

    iniciarVotacionAlcalde() {
      alert('Funcionalidad de alcalde no disponible aún')
    },

    async finalizarVotacion() {
      try {
        await axiosInstance.put(
          `/partida/${this.codigoSala}/votacion/${this.idSesionActual}/cerrar`,
        )
      } catch (error) {
        alert('Error al finalizar votación')
      }
    },

    iniciarEventos() {
      this.iniciarVotacionNoche()
    },

    async iniciarVotacionNoche() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
          tipo: 'Lobos',
        })
        this.idSesionActual = res.data
      } catch (error) {
        alert('Error al iniciar eventos nocturnos')
      }
    },

    async activarTurnoJugador(jugador) {
      try {
        await axiosInstance.put(
          `/partida/${this.codigoSala}/jugador/${jugador.idUsuario}/confirmar-muerte`,
        )
      } catch (error) {
        alert('Error al confirmar muerte')
      }
    },
  },
}
</script>

<style scoped>
.contenedor-narrador {
  min-height: 100vh;
  padding: 20px;
  background-image: url('@/assets/imgs/fondo.png');
  background-size: cover;
}

.contenedor-narrador.noche {
  background-image: url('@/assets/imgs/fondonoche.png');
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
}

.narrador-box {
  background: black;
  color: #cc0000;
  padding: 12px 20px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.05em;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.noche .narrador-box {
  background: white;
  color: #8b0000;
}
</style>