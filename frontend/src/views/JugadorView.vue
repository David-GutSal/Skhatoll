<template>
  <div class="contenedor-jugador" :class="esDia ? 'dia' : 'noche'">
    <div class="contenido">
      <CabeceraJugador
        :nombreJugador="nombre"
        :esDia="esDia"
        :esNarrador="false"
        :nombreNarrador="nombreNarrador"
        :alcaldeNombre="alcaldeNombre"
      />

      <div
        v-if="mensajeEvento"
        class="cuadro-evento"
        :class="esDia ? 'evento-dia' : 'evento-noche'"
      >
        <i v-if="tipoVotacionLocal === 'ALCALDE'" class="fa-solid fa-medal"></i>
        <i v-else-if="tipoVotacionLocal === 'DIA'" class="fa-solid fa-gavel"></i>
        <i v-else-if="tipoVotacionLocal === 'LOBOS'" class="fa-solid fa-skull"></i>
        <i v-else class="fa-solid fa-bell"></i>
        {{ mensajeEvento }}
      </div>

      <PanelVotacionesJugador
        :esDia="esDia"
        :votacionActiva="votacionActiva"
        :tipoVotacion="tipoVotacionLocal"
        :jugadorSeleccionado="jugadorSeleccionado"
        @votarAlcalde="votarAlcalde"
        @votarCulpable="votarCulpable"
      />

      <div v-if="!esDia && esMiTurno" class="cuadro-turno">
        <div class="cuadro-turno-texto">
          <i class="fa-solid fa-moon"></i>
          ¡Es tu turno! Selecciona un jugador y activa tu poder
        </div>
        <button class="btn-ir-poderes" @click="scrollAPoderes">
          <i class="fa-solid fa-arrow-down"></i>
          Ver mis poderes
        </button>
      </div>

      <div class="mesa-wrapper-outer" :class="{ 'mesa-turno-activo': !esDia && esMiTurno }">
        <MesaJugadores
          :jugadores="jugadoresVisibles"
          :esDia="esDia"
          :modoNarrador="false"
          :jugadorSeleccionado="jugadorSeleccionado"
          @seleccionarJugador="seleccionarJugador"
          :jugadorEnvenenado="jugadorEnvenenado"
        />
      </div>

      <BotonMiRol />

      <ZonaPoderes
        ref="zonaPoderes"
        :miRol="miRol"
        :jugadorSeleccionado="jugadorSeleccionado"
        :esMiTurno="esMiTurno"
        :esDia="esDia"
        @devorar="devorarJugador"
        @premonicion="usarPremonicion"
        @flechazo="manejarFlechazo"
        @finalizarTurno="finalizarTurno"
        @envenenar="manejarEnvenenar"
        @vidaUsada="manejarVidaUsada"
        @disparo="manejarDisparo"
      />
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
      // ✅ renombrado a tipoVotacionLocal para evitar conflicto con el getter del store
      tipoVotacionLocal: null,
      esMiTurno: false,
      jugadorSeleccionado: null,
      stompClient: null,
      mensajeEvento: null,
      alcaldeNombre: null,
      jugadorEnvenenado: null,
      soyElCazadorMuerto: false,
    }
  },

  computed: {
    ...mapGetters('auth', ['nombre']),
    ...mapGetters('sala', ['codigoSala', 'jugadores', 'miRol', 'enamorados']),

    nombreNarrador() {
      const narrador = this.jugadores.find((j) => j.esNarrador === true)
      return narrador ? narrador.nombre : 'Esperando narrador...'
    },

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
        this.tipoVotacionLocal = sesion.data.tipo
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
    scrollAPoderes() {
      this.$refs.zonaPoderes.$el.scrollIntoView({ behavior: 'smooth' })
    },

    seleccionarJugador(j) {
      if (this.enamorados) {
        const miNombre = this.nombre
        const { jugador1, jugador2 } = this.enamorados
        const soyEnamorado = jugador1 === miNombre || jugador2 === miNombre
        if (soyEnamorado) {
          const nombrePareja = jugador1 === miNombre ? jugador2 : jugador1
          if (j.nombre === nombrePareja) return
        }
      }

      if (!this.votacionActiva && !this.esMiTurno) return
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
    },

    manejarFlechazo(pareja) {
      this.stompClient.publish({
        destination: `/topic/partida/${this.codigoSala}/turno`,
        body: JSON.stringify({
          tipo: 'FLECHAZO',
          jugador1: pareja.jugador1.nombre,
          jugador2: pareja.jugador2.nombre,
        }),
      })
      this.finalizarTurno()
    },

    manejarEnvenenar(jugador) {
      this.jugadorEnvenenado = jugador
    },

    manejarVidaUsada(nombreJugador) {
      // Publicar por STOMP para que el narrador también actualice su store
      this.stompClient?.publish({
        destination: `/topic/partida/${this.codigoSala}/turno`,
        body: JSON.stringify({
          tipo: 'BRUJA_VIDA',
          nombreJugador,
        }),
      })
    },

    finalizarTurno() {
      this.esMiTurno = false
      this.jugadorSeleccionado = null

      this.stompClient.publish({
        destination: `/topic/partida/${this.codigoSala}/turno`,
        body: JSON.stringify({
          tipo: 'TURNO_FINALIZADO',
          nombreJugador: this.nombre,
        }),
      })

      if (this.soyElCazadorMuerto) {
        setTimeout(() => {
          this.$router.push({ name: 'eliminado' })
        }, 1500)
      }
    },

    manejarDisparo(jugador) {
  // El backend ya emite WS /muerte para la víctima del disparo
  // Solo necesitamos loguear; el finalizarTurno lo emite PoderCazador tras 2.5s
  console.log('🔫 Cazador disparó a:', jugador.nombre)
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

          if (payload.fase === 'DIA') {
            this.$store.dispatch('sala/reiniciarVotos')
            this.$store.dispatch('sala/setTipoVotacion', null)
            this.tipoVotacionLocal = null
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/muerte`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.$store.dispatch('sala/marcarMuerto', payload.nombreJugador)
          this.$store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)

          if (payload.nombreJugador === this.nombre) {
            if ((this.miRol || '').toLowerCase() === 'cazador') {
              this.soyElCazadorMuerto = true
              this.mensajeEvento =
                '¡Has sido eliminado! El Narrador activará tu poder de Cazador en breve...'
              return
            }
            this.$router.push({ name: 'eliminado' })
            return
          }

          const enamorados = this.$store.getters['sala/enamorados']
          if (enamorados) {
            const { jugador1, jugador2 } = enamorados
            if (payload.nombreJugador === jugador1 || payload.nombreJugador === jugador2) {
              const nombrePareja = payload.nombreJugador === jugador1 ? jugador2 : jugador1
              const jugadorPareja = this.jugadores.find((j) => j.nombre === nombrePareja)
              if (jugadorPareja && jugadorPareja.estaVivo) {
                this.stompClient.publish({
                  destination: `/topic/partida/${this.codigoSala}/turno`,
                  body: JSON.stringify({
                    tipo: 'MUERTE_ENAMORADO',
                    nombreJugador: nombrePareja,
                  }),
                })
              }
            }
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/votos`, (msg) => {
          const payload = JSON.parse(msg.body)
          const tipoVotacionActual = this.$store.getters['sala/tipoVotacion']

          if (tipoVotacionActual === 'LOBOS') {
            if (this.miRol && this.miRol.toLowerCase() === 'lobo') {
              this.$store.dispatch('sala/actualizarVotos', payload.votos)
            }
            return
          }

          this.$store.dispatch('sala/actualizarVotos', payload.votos)
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/alcalde`, (msg) => {
          const payload = JSON.parse(msg.body)
          if (payload.tipo === 'ALCALDE_ELEGIDO') {
            this.$store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
            // ✅ Guardar nombre del alcalde y resetear votos
            this.alcaldeNombre = payload.nombreAlcalde
            this.$store.dispatch('sala/reiniciarVotos')
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/votacion`, (msg) => {
          const payload = JSON.parse(msg.body)

          if (payload.tipo === 'VOTACION_ABIERTA' || payload.tipo === 'VOTACION_CERRADA') {
            this.votacionActiva = payload.abierta

            if (payload.abierta) {
              this.tipoVotacionLocal = payload.tipoVotacion
              this.$store.dispatch('sala/setTipoVotacion', payload.tipoVotacion)

              if (payload.tipoVotacion === 'ALCALDE') {
                this.mensajeEvento = 'ELECCIONES ABIERTAS'
              } else if (payload.tipoVotacion === 'DIA') {
                this.mensajeEvento = 'VOTACIÓN DE LINCHAMIENTO'
              } else if (payload.tipoVotacion === 'LOBOS') {
                this.mensajeEvento = 'LOS LOBOS DECIDEN'
              }

              setTimeout(() => {
                this.mensajeEvento = null
              }, 30000)
            } else {
              this.tipoVotacionLocal = null
              this.$store.dispatch('sala/setTipoVotacion', null)
            }
            return
          }

          // Resultado de votación de lobos — marcar semimuerto
          if (payload.tipo === 'LOBOS' && payload.nombreEliminado) {
            this.$store.dispatch('sala/marcarSemimuerto', payload.nombreEliminado)
            this.mensajeEvento = `Los lobos han devorado a ${payload.nombreEliminado} esta noche...`
            setTimeout(() => {
              this.mensajeEvento = null
            }, 8000)
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/turno`, (msg) => {
          const payload = JSON.parse(msg.body)

          if (payload.tipo === 'EVENTOS_INICIADOS') {
            this.mensajeEvento =
              '¡Llegó la noche! Presta atención, puede que el narrador te llame para que utilices tus poderes'
            setTimeout(() => {
              this.mensajeEvento = null
            }, 10000)
            return
          }

          if (payload.tipo === 'EVENTOS_FINALIZADOS') {
            this.mensajeEvento = null
            this.esMiTurno = false
            this.jugadorSeleccionado = null
            return
          }

          if (payload.tipo === 'TURNO_FINALIZADO') return
          if (payload.tipo === 'MUERTE_ENAMORADO') return

          if (payload.tipo === 'FLECHAZO') {
            const soyEnamorado =
              payload.jugador1 === this.nombre || payload.jugador2 === this.nombre

            if (soyEnamorado) {
              const nombrePareja =
                payload.jugador1 === this.nombre ? payload.jugador2 : payload.jugador1

              this.$store.dispatch('sala/setEnamorados', {
                jugador1: payload.jugador1,
                jugador2: payload.jugador2,
              })

              this.mensajeEvento = `¡Estás enamorado de ${nombrePareja}!`
              setTimeout(() => {
                this.mensajeEvento = null
              }, 8000)
            }
            return
          }

          if (payload.tipo === 'TURNO_LOBOS') {
            const soyLobo = payload.nombresLobos?.includes(this.nombre)
            this.esMiTurno = soyLobo
            if (soyLobo) {
              this.mensajeEvento =
                '¡Es hora de cazar! Decide junto a los tuyos a quién devoráis esta noche'
              setTimeout(() => {
                this.mensajeEvento = null
              }, 30000)
            }
            return
          }

          if (payload.tipo === 'TURNO_JUGADOR') {
            this.esMiTurno = payload.nombreJugador === this.nombre
            if (this.esMiTurno) {
              this.mensajeEvento = `Es tu turno, ${this.nombre}. Activa tu poder.`
              setTimeout(() => {
                this.mensajeEvento = null
              }, 30000)
            } else {
              this.esMiTurno = false
            }
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

.mesa-turno-activo :deep(.mesa-wrapper) {
  border-color: #e4ba03 !important;
  box-shadow:
    0 0 12px rgba(228, 186, 3, 0.6),
    0 0 28px rgba(228, 186, 3, 0.3);
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
  animation: aparecer 0.4s ease;
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

.cuadro-turno {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 20px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.85);
  border: 2px solid #e4ba03;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  animation: aparecer 0.4s ease;
}

.cuadro-turno-texto {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-ir-poderes {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid #e4ba03;
  background: transparent;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-ir-poderes:hover {
  background: #e4ba03;
  color: #000;
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
  .cuadro-turno {
    flex-direction: column;
    align-items: flex-start;
  }
  .cuadro-alcalde {
    flex-direction: column;
    align-items: flex-start;
  }
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
