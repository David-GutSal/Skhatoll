<template>
  <div class="contenedor-narrador" :class="esDia ? 'dia' : 'noche'">
  <div class="contenido">

    <div class="cabecera">
      <div class="nombre-box" :class="esDia ? 'nombre-dia' : 'nombre-noche'">
        <i class="fa-solid fa-book-open-reader"></i>
        <span>Narrador: {{ nombre }}</span>
      </div>
      <IndicadorDiaNoche :esDia="esDia" @cambiarFase="cambiarFase" />

      <div class="carta-fase" :class="esDia ? 'carta-dia' : 'carta-noche'">
        <p class="carta-fase-titulo">{{ esDia ? ' EL DÍA' : 'LA NOCHE' }}</p>
        <img :src="esDia ? solImg : lunaImg" class="carta-fase-img" />
        <p class="carta-fase-texto">
          {{ esDia
            ? 'La cálida luz del Sol ahuyenta a las bestias pero no a las sospechas'
            : 'La clara luz de la luna ilumina a las bestias que acechan entre las sombras'
          }}
        </p>
      </div>
    
    </div>

    <PanelControlNarrador
      :esDia="esDia"
      :hayAlcalde="hayAlcalde"
      @votarLinchamiento="iniciarVotacionLinchamiento"
      @votarAlcalde="iniciarVotacionAlcalde"
      @finalizarVotacion="finalizarVotacion"
      @eventos="iniciarEventos"
      @verPersonajes="seccionActiva = seccionActiva === 'personajes' ? null : 'personajes'"
      @verReglas="seccionActiva = seccionActiva === 'reglas' ? null : 'reglas'"
    />

    <MesaJugadores
      :jugadores="jugadoresConRol"
      :esDia="esDia"
      :modoNarrador="true"
      @seleccionarJugador="activarTurnoJugador"
    />

    <div v-if="seccionActiva" class="seccion-info" :class="esDia ? 'seccion-dia' : 'seccion-noche'">
      <div v-if="seccionActiva === 'personajes'" class="seccion-contenido">
        <h2 class="seccion-titulo">Personajes</h2>
        <p class="seccion-placeholder">Lobo · Bruja · Vidente · Aldeano · Cupido · Cazador · Niño Salvaje · Niña</p>
        <p class="seccion-placeholder"><em>Contenido detallado próximamente...</em></p>
      </div>
      <div v-if="seccionActiva === 'reglas'" class="seccion-contenido">
        <h2 class="seccion-titulo">Reglas</h2>
        <p class="seccion-placeholder"><em>Contenido de reglas próximamente...</em></p>
      </div>
    </div>
  </div>

    <div class="footer-aldea" :class="esDia ? 'footer-dia' : 'footer-noche'"></div>

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
import solImg from '@/assets/imgs/sol.jpg'
import lunaImg from '@/assets/imgs/luna.jpg'

export default {
  name: 'NarradorView',
  components: { IndicadorDiaNoche, PanelControlNarrador, MesaJugadores },

  data() {
    return {
      esDia: true,
      stompClient: null,
      idSesionActual: null,
      seccionActiva: null,
      solImg,
      lunaImg,
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
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, { tipo: 'DIA' })
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
        await axiosInstance.put(`/partida/${this.codigoSala}/votacion/${this.idSesionActual}/cerrar`)
      } catch (error) {
        alert('Error al finalizar votación')
      }
    },

    iniciarEventos() {
      this.iniciarVotacionNoche()
    },

    async iniciarVotacionNoche() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, { tipo: 'Lobos' })
        this.idSesionActual = res.data
      } catch (error) {
        alert('Error al iniciar eventos nocturnos')
      }
    },

    async activarTurnoJugador(jugador) {
      try {
        await axiosInstance.put(`/partida/${this.codigoSala}/jugador/${jugador.idUsuario}/confirmar-muerte`)
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
  display: flex;
  flex-direction: column;
  gap: 0;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.dia {
  background-image: url('@/assets/imgs/fondodia.png');
}

.noche {
  background-image: url('@/assets/imgs/fondonoche.png');
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.nombre-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 12px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #cc0000;
}

.nombre-dia { background: #000; }
.nombre-noche { background: white; }

.carta-fase {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 15px;
  max-width: 300px;
}

.carta-dia { background: white; border: 10px solid #e4ba03; }
.carta-noche { background: #000; border: 10px solid #cc0000; }

.carta-fase-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 3rem;
  margin: 0;
}

.carta-dia .carta-fase-titulo { color: #e4ba03; }
.carta-noche .carta-fase-titulo { color: #cc0000; }

.carta-fase-img {
  width: 100%;
  aspect-ratio: 1/1;
  object-fit: cover;
  border-radius: 15px;
}

.carta-dia .carta-fase-img { border: 5px solid #e4ba03; }
.carta-noche .carta-fase-img { border: 5px solid #cc0000; }

.carta-fase-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  text-align: center;
  font-style: italic;
  line-height: 1.4;
  margin: 0;
}

.carta-dia .carta-fase-texto { color: #e4ba03; }
.carta-noche .carta-fase-texto { color: #cc0000; }

.contenedor-narrador > .mesa-wrapper,
.contenedor-narrador > :deep(.mesa-wrapper) {
  margin: 0 20px;
}

.seccion-info {
  margin: 20px;
  border-radius: 12px;
  padding: 24px;
}

.seccion-dia {
  background: rgba(0,0,0,0.7);
  border: 5px solid #e4ba03;
}

.seccion-noche {
  background: rgba(0,0,0,0.85);
  border: 5px solid #cc0000;
}

.seccion-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  color: white;
  font-size: 1.5rem;
  margin: 0 0 12px 0;
}

.seccion-placeholder {
  font-family: 'Raleway', Arial, sans-serif;
  color: #aaa;
  font-size: 0.9rem;
  line-height: 1.6;
}

.footer-aldea {
  margin-top: auto;
  width: 100%;
  height: 900px;
  background-size: cover;
  background-position: bottom;
  background-repeat: no-repeat;
}

.footer-dia {
  background-image: url('@/assets/imgs/footer-dia.png');
}

.footer-noche {
  background-image: url('@/assets/imgs/footer-noche.png');
}

.contenido {
  width: 90%;
  margin: 0 auto;
  padding-top: 70px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (max-width: 900px) {
  .contenido {
    width: 85%;
  }
}

@media (max-width: 600px) {
  .contenido {
    width: 95%;
    padding-top: 20px;
  }
}

@media (max-width: 768px) {
  .cabecera {
    flex-direction: column;
    align-items: center;
  }
  .carta-fase {
    max-width: 130px;
  }
}
</style>