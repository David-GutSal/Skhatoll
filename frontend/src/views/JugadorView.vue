<template>
  <div class="contenedor-jugador" :class="esDia ? 'dia' : 'noche'">
    <div class="contenido">
      <CabeceraJugador
        :nombreJugador="nombre"
        :esDia="esDia"
        :esNarrador="false"
        :nombreNarrador="nombreNarrador"
      />

      <PanelVotacionesJugador
        :esDia="esDia"
        :votacionActiva="votacionActiva"
        :tipoVotacion="tipoVotacion"
        :jugadorSeleccionado="jugadorSeleccionado"
        @votarAlcalde="votarAlcalde"
        @votarCulpable="votarCulpable"
      />

      <div class="mesa-wrapper-outer">
        <MesaJugadores
          :jugadores="jugadoresVisibles"
          :esDia="esDia"
          :modoNarrador="false"
          @seleccionarJugador="seleccionarJugador"
        />
      </div>

      <div
        v-if="mensajeEvento"
        class="cuadro-evento"
        :class="esDia ? 'evento-dia' : 'evento-noche'"
      >
        <i class="fa-solid fa-bell"></i>
        {{ mensajeEvento }}
      </div>

      <div v-if="!esDia && esMiTurno" class="cuadro-evento evento-noche">
        <i class="fa-solid fa-moon"></i>
        Es tu turno — activa tu poder
      </div>

      <BotonMiRol />

      <ZonaPoderes
        :miRol="miRol"
        :jugadorSeleccionado="jugadorSeleccionado"
        :esMiTurno="esMiTurno"
        @devorar="devorarJugador"
        @premonicion="usarPremonicion"
      />

      <button class="boton-salir" @click="salirPartida">
        <i class="fa-solid fa-door-open"></i> Salir de la partida
      </button>
    </div>

    <div class="footer-aldea" :class="esDia ? 'footer-dia' : 'footer-noche'"></div>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { mapGetters } from 'vuex'
import CabeceraJugador from '@/components/juego/CabeceraJugador.vue'
import PanelVotacionesJugador from '@/components/juego/PanelVotacionesJugador.vue'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'
import BotonMiRol from '@/components/juego/BotonMiRol.vue'
import ZonaPoderes from '@/components/juego/ZonaPoderes.vue'

export default {
  name: 'JugadorView',
  components: {
    CabeceraJugador,
    PanelVotacionesJugador,
    MesaJugadores,
    BotonMiRol,
    ZonaPoderes,
  },

  data() {
    return {
      esDia: true,
      votacionActiva: false,
      tipoVotacion: null,
      esMiTurno: false,
      jugadorSeleccionado: null,
      stompClient: null,
      mensajeEvento: null,
    }
  },

  computed: {
    ...mapGetters('auth', ['nombre']),
    ...mapGetters('sala', ['codigoSala', 'jugadores', 'miRol']),
//añadido 1
    nombreNarrador() {
      const narrador = this.jugadores.find((j) => j.esNarrador === true)
      return narrador ? narrador.nombre : 'Esperando narrador...'
    },
//añadido 2
    soyNarrador() {
      return this.jugadores.some((j) => j.esNarrador === true && j.nombre === this.nombre)
    },

    jugadoresVisibles() {
      if (!this.esDia && this.miRol && this.miRol.toLowerCase() === 'lobo') {
        return this.jugadores.filter((j) => j.nombre !== this.nombre)
      }
      return this.jugadores
    },
  },

  async created() {
    try {
      const res = await axiosInstance.get(`/salas/${this.codigoSala}/jugadores`)
      this.$store.dispatch('sala/setJugadores', res.data)
    } catch (error) {
      alert('Error al cargar jugadores')
    }

    try {
      const sesion = await axiosInstance.get(`/partida/${this.codigoSala}/sesion-activa`)
      if (sesion.data?.abierta) {
        this.votacionActiva = true
        this.tipoVotacion = sesion.data.tipo
      }
    } catch {
      // No hay sesión activa, es normal
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
    async salirPartida() {
      if (!confirm('¿Seguro que quieres salir de la partida?')) return
      if (this.stompClient) {
        this.stompClient.deactivate()
        this.stompClient = null
      }
      await this.$store.dispatch('sala/salir')
      this.$router.push({ name: 'sala' })
    },

    seleccionarJugador(j) {
      this.jugadorSeleccionado = j
    },

    async votarAlcalde() {
      if (!this.jugadorSeleccionado) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/votar`, {
          idObjetivo: this.jugadorSeleccionado.idUsuario,
        })
      } catch (error) {
        alert('Error al votar')
      }
    },

    async votarCulpable() {
      if (!this.jugadorSeleccionado) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/votar`, {
          idObjetivo: this.jugadorSeleccionado.idUsuario,
        })
      } catch (error) {
        alert('Error al votar')
      }
    },

    async devorarJugador() {
      if (!this.jugadorSeleccionado) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/votar`, {
          idObjetivo: this.jugadorSeleccionado.idUsuario,
        })
      } catch (error) {
        alert('Error al devorar')
      }
    },

    usarPremonicion() {
      if (!this.jugadorSeleccionado) return
      alert('Has usado tu premonición sobre: ' + this.jugadorSeleccionado.nombre)
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
        cliente.subscribe(`/topic/partida/${this.codigoSala}/alcalde`, (msg) => {
          const payload = JSON.parse(msg.body)

          console.log('👑 ALCALDE RECIBIDO:', payload)

          if (payload.tipo === 'ALCALDE_ELEGIDO') {
            this.$store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
          }
        })
        cliente.subscribe(`/topic/partida/${this.codigoSala}/votacion`, (msg) => {
          const payload = JSON.parse(msg.body)

          console.log('📩 VOTACION JUGADOR:', payload)

          this.votacionActiva = payload.abierta ?? false

          if (payload.abierta) {
            this.tipoVotacion = payload.tipoVotacion

            // 🔥 MENSAJE VISUAL SEGÚN TIPO
            if (payload.tipoVotacion === 'ALCALDE') {
              this.mensajeEvento = 'ELECCIONES ABIERTAS'
            } else if (payload.tipoVotacion === 'DIA') {
              this.mensajeEvento = 'VOTACIÓN DE LINCHAMIENTO'
            } else if (payload.tipoVotacion === 'LOBOS') {
              this.mensajeEvento = 'LOS LOBOS DECIDEN'
            }

            // ⏱️ Se borra solo después
            setTimeout(() => {
              this.mensajeEvento = null
            }, 30000)
          } else {
            this.tipoVotacion = null
          }
        })
        cliente.subscribe(`/topic/partida/${this.codigoSala}/turno`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.esMiTurno = payload.nombreJugador === this.nombre
          if (this.esMiTurno) {
            this.mensajeEvento = `Es tu turno, ${this.nombre}. Activa tu poder.`
          }
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
.contenedor-jugador {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.boton-salir {
  background: #3a3a3a;
  color: #ccc;
  border: 2px solid #555;
  padding: 10px 22px;
  border-radius: 8px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  align-self: flex-end;
  transition: background 0.2s ease;
}
.boton-salir:hover {
  background: #8b0000;
  border-color: #8b0000;
  color: white;
}

.dia {
  background-image: url('@/assets/imgs/fondodia.png');
}
.noche {
  background-image: url('@/assets/imgs/fondonoche.png');
}

.contenido {
  width: 90%;
  margin: 0 auto;
  padding-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

.mesa-wrapper-outer :deep(.mesa-wrapper) {
  border-width: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
}

.cuadro-evento {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-radius: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
}

.evento-dia {
  background: rgba(0, 0, 0, 0.6);
  border: 2px solid #e4ba03;
  color: #e4ba03;
}

.evento-noche {
  background: rgba(0, 0, 0, 0.7);
  border: 2px solid #cc0000;
  color: #cc0000;
}

.footer-aldea {
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

.cuadro-evento {
  animation: aparecer 0.4s ease;
}

@keyframes aparecer {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
