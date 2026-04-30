<template>
  <div class="contenedor-narrador" :class="esDia ? 'dia' : 'noche'">
    <div class="contenido">
      <div class="cabecera">
        <div class="columna-izquierda">
          <div class="nombre-box" :class="esDia ? 'nombre-dia' : 'nombre-noche'">
            <i class="fa-solid fa-book-open-reader"></i>
            <span>Narrador: {{ nombre }}</span>
          </div>
          <div class="cuadro-alcalde">
            <i class="fa-solid fa-medal"></i>
            <span>
              Nuestro alcalde es:
              <strong>{{ textoAlcalde }}</strong>
            </span>
          </div>
        </div>
        <IndicadorDiaNoche :esDia="esDia" @cambiarFase="cambiarFase" />
        <div class="carta-fase" :class="esDia ? 'carta-dia' : 'carta-noche'">
          <p class="carta-fase-titulo">{{ esDia ? 'EL DÍA' : 'LA NOCHE' }}</p>
          <img :src="esDia ? solImg : lunaImg" class="carta-fase-img" />
          <p class="carta-fase-texto">
            {{
              esDia
                ? 'La cálida luz del Sol ahuyenta a las bestias pero no a las sospechas'
                : 'La clara luz de la luna ilumina a las bestias que acechan entre las sombras'
            }}
          </p>
        </div>
      </div>

      <div v-if="avisoSesion" class="aviso-sesion">
        <i class="fa-solid fa-triangle-exclamation"></i>
        {{ avisoSesion }}
      </div>

      <PanelControlNarrador
        :esDia="esDia"
        :hayAlcalde="hayAlcalde"
        :sesionActiva="!!idSesionActual"
        :sesionActualTipo="sesionActualTipo"
        @votarLinchamiento="iniciarVotacionLinchamiento"
        @votarAlcalde="iniciarVotacionAlcalde"
        @finalizarVotacion="finalizarVotacion"
        @eventos="iniciarEventos"
        @verPersonajes="seccionActiva = seccionActiva === 'personajes' ? null : 'personajes'"
        @verReglas="seccionActiva = seccionActiva === 'reglas' ? null : 'reglas'"
      />

      <div class="mesa-wrapper-outer">
        <MesaJugadores
          :jugadores="jugadoresConRolConEnamorados"
          :esDia="esDia"
          :modoNarrador="true"
          :modoEventos="modoEventos"
          :jugadorSeleccionado="jugadorSeleccionado"
          @seleccionarJugador="activarTurnoJugador"
        />
      </div>

      <div
        v-if="seccionActiva"
        class="seccion-info"
        :class="esDia ? 'seccion-dia' : 'seccion-noche'"
      >
        <div v-if="seccionActiva === 'personajes'" class="seccion-contenido">
          <ListaPersonajes />
        </div>
        <div v-if="seccionActiva === 'reglas'" class="seccion-contenido">
          <ListaReglas />
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
import ListaPersonajes from '@/components/juego/ListaPersonajes.vue'
import ListaReglas from '@/components/juego/ListaReglas.vue'

export default {
  name: 'NarradorView',
  components: {
    IndicadorDiaNoche,
    PanelControlNarrador,
    MesaJugadores,
    ListaPersonajes,
    ListaReglas,
  },

  data() {
    return {
      esDia: true,
      stompClient: null,
      idSesionActual: null,
      seccionActiva: null,
      solImg,
      lunaImg,
      modoEventos: false,
      jugadorSeleccionado: null,
      sesionActualTipo: null,
      avisoSesion: null,
      jugadoresYaActuadosEstaNoche: [],
      eventosYaUsadosEstaNoche: false,
    }
  },

  computed: {
    ...mapGetters('auth', ['nombre']),
    ...mapGetters('sala', [
      'codigoSala',
      'jugadoresConRol',
      'enamorados',
      'cupidoUsado',
      'semiMuertos',
    ]),

    hayAlcalde() {
      return this.jugadoresConRol.some((j) => j.alcalde)
    },

    jugadoresConRolConEnamorados() {
      if (!this.enamorados) return this.jugadoresConRol
      return this.jugadoresConRol.map((j) => ({
        ...j,
        esEnamorado: j.nombre === this.enamorados.jugador1 || j.nombre === this.enamorados.jugador2,
      }))
    },

    textoAlcalde() {
      const alcalde = this.jugadoresConRol.find((j) => j.alcalde)
      return alcalde ? alcalde.nombre : 'Elecciones pendientes'
    },
  },

  async created() {
    if (!this.codigoSala) {
      const codigoGuardado = localStorage.getItem('codigoSala')
      if (codigoGuardado) {
        this.$store.dispatch('sala/unirse', codigoGuardado)
      } else {
        alert('No hay sala activa')
        this.$router.push('/')
        return
      }
    }

    try {
      const res = await axiosInstance.get(`/salas/${this.codigoSala}/roles`)
      this.$store.dispatch('sala/setJugadoresConRol', res.data)
    } catch (error) {
      alert('Error al cargar jugadores')
    }

    try {
      const res = await axiosInstance.get(`/partida/${this.codigoSala}/sesion-activa`)
      if (res.data?.abierta) {
        this.idSesionActual = res.data.idSesion
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
    async cambiarFase(fase) {
      try {
        const esNoche = fase === 'noche'
        this.esDia = !esNoche

        if (esNoche) {
          this.eventosYaUsadosEstaNoche = false
          this.jugadoresYaActuadosEstaNoche = []
        } else {
          // Al amanecer — confirmar muertes de semimuertos pendientes
          await this.confirmarMuertesSemimuertos()
          // Resetear votos al cambiar a día
          this.$store.dispatch('sala/reiniciarVotos')
          this.$store.dispatch('sala/setTipoVotacion', null)
        }

        await axiosInstance.put(`/partida/${this.codigoSala}/fase`)
      } catch (error) {
        alert(
          error.response?.status === 409
            ? 'Cierra la votación antes de cambiar la fase'
            : 'Error al cambiar la fase',
        )
      }
    },

    // Confirma la muerte definitiva de todos los semimuertos al amanecer
    async confirmarMuertesSemimuertos() {
      const semimuertos = [...this.semiMuertos]
      console.log('🌅 Confirmando muertes al amanecer:', semimuertos)
      for (const nombre of semimuertos) {
        const jugador = this.jugadoresConRol.find((j) => j.nombre === nombre)
        if (jugador) {
          try {
            await axiosInstance.put(
              `/partida/${this.codigoSala}/jugador/${jugador.idUsuario}/confirmar-muerte`,
            )
            console.log('✅ Muerte confirmada:', nombre)
          } catch (error) {
            console.error(
              '❌ Error confirmando muerte de',
              nombre,
              ':',
              error.response?.status,
              error.response?.data,
            )
          }
        }
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

          if (payload.fase === 'NOCHE') {
            this.eventosYaUsadosEstaNoche = false
            this.jugadoresYaActuadosEstaNoche = []
          }

          if (payload.fase === 'DIA') {
            // Resetear votos al amanecer
            this.$store.dispatch('sala/reiniciarVotos')
            this.$store.dispatch('sala/setTipoVotacion', null)
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/muerte`, (msg) => {
          const payload = JSON.parse(msg.body)
          this.$store.dispatch('sala/marcarMuerto', payload.nombreJugador)
          // Quitar de semimuertos si estaba
          this.$store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/alcalde`, (msg) => {
          const payload = JSON.parse(msg.body)
          if (payload.tipo === 'ALCALDE_ELEGIDO') {
            this.$store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
            this.$store.dispatch('sala/reiniciarVotos')
          }
        })

        // El narrador siempre actualiza votos (ve todo)
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

        cliente.subscribe(`/topic/partida/${this.codigoSala}/votacion`, (msg) => {
          const payload = JSON.parse(msg.body)
          console.log('📩 VOTACION PAYLOAD COMPLETO:', JSON.stringify(payload))

          if (payload.tipo === 'VOTACION_ABIERTA') {
            this.idSesionActual = payload.idSesion
            this.$store.dispatch('sala/setTipoVotacion', payload.tipoVotacion)
          }

          if (payload.tipo === 'VOTACION_CERRADA') {
            this.idSesionActual = null
            this.$store.dispatch('sala/setTipoVotacion', null)
          }

          // Resultado de votación de lobos — marcar semimuerto
          if (payload.tipo === 'LOBOS' && payload.nombreEliminado) {
            this.$store.dispatch('sala/marcarSemimuerto', payload.nombreEliminado)
            this.avisoSesion = `Los lobos han devorado a ${payload.nombreEliminado} esta noche`
            setTimeout(() => {
              this.avisoSesion = null
            }, 6000)
          }
        })

        cliente.subscribe(`/topic/partida/${this.codigoSala}/turno`, (msg) => {
          const payload = JSON.parse(msg.body)

          if (payload.tipo === 'TURNO_FINALIZADO') {
            if (
              this.jugadorSeleccionado &&
              !this.jugadoresYaActuadosEstaNoche.includes(this.jugadorSeleccionado.nombre)
            ) {
              this.jugadoresYaActuadosEstaNoche.push(this.jugadorSeleccionado.nombre)
            }

            this.avisoSesion = `${payload.nombreJugador} ha finalizado su turno`
            setTimeout(() => {
              this.avisoSesion = null
            }, 4000)

            if (this.idSesionActual) {
              axiosInstance
                .put(`/partida/${this.codigoSala}/votacion/${this.idSesionActual}/cerrar`)
                .then(() => {
                  this.idSesionActual = null
                  this.sesionActualTipo = null
                  this.jugadorSeleccionado = null
                })
                .catch(() => {})
            }
          }

          if (payload.tipo === 'BRUJA_VIDA') {
            this.$store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)
            this.avisoSesion = `La bruja ha salvado a ${payload.nombreJugador} esta noche`
            setTimeout(() => {
              this.avisoSesion = null
            }, 5000)
            return
          }

          if (payload.tipo === 'FLECHAZO') {
            this.$store.dispatch('sala/setEnamorados', {
              jugador1: payload.jugador1,
              jugador2: payload.jugador2,
            })
            this.$store.dispatch('sala/setCupidoUsado')
          }

          if (payload.tipo === 'MUERTE_ENAMORADO') {
            const jugador = this.jugadoresConRol.find((j) => j.nombre === payload.nombreJugador)
            if (jugador) {
              axiosInstance
                .put(`/partida/${this.codigoSala}/jugador/${jugador.idUsuario}/confirmar-muerte`)
                .catch(() => {})
            }
          }
          if (payload.tipo === 'MENTOR_ELEGIDO') {
            const mentor = this.jugadoresConRol.find((j) => j.nombre === payload.nombreMentor)
            if (mentor) mentor.esMentor = true

            this.avisoSesion = `El Niño Salvaje ha elegido a ${payload.nombreMentor} como su mentor`
            setTimeout(() => {
              this.avisoSesion = null
            }, 5000)
            return
          }
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
        this.votacionActiva = true
        this.tipoVotacion = 'DIA'
      } catch (error) {
        alert(
          error.response?.status === 409
            ? 'Ya hay una votación abierta'
            : 'Error al iniciar votación',
        )
      }
    },

    async iniciarVotacionAlcalde() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
          tipo: 'ALCALDE',
        })
        this.idSesionActual = res.data
      } catch (error) {
        alert(
          error.response?.status === 409
            ? 'Ya hay una votación abierta'
            : 'Error al iniciar elecciones',
        )
      }
    },

    async finalizarVotacion() {
      if (!this.idSesionActual) {
        alert('No hay ninguna votación activa')
        return
      }
      try {
        await axiosInstance.put(
          `/partida/${this.codigoSala}/votacion/${this.idSesionActual}/cerrar`,
        )
        this.idSesionActual = null
        this.$store.dispatch('sala/reiniciarVotos')
        this.$store.dispatch('sala/setTipoVotacion', null)
      } catch (error) {
        alert('Error al finalizar votación')
      }
    },

    iniciarEventos() {
      if (!this.modoEventos && this.eventosYaUsadosEstaNoche) {
        this.avisoSesion = 'Los eventos nocturnos ya han terminado esta noche'
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
      }

      this.modoEventos = !this.modoEventos

      if (this.modoEventos) {
        this.jugadoresYaActuadosEstaNoche = []
      } else {
        this.eventosYaUsadosEstaNoche = true
        this.jugadorSeleccionado = null
        this.avisoSesion = null
        this.$store.dispatch('sala/setTurnoActivo', null)
      }

      this.stompClient.publish({
        destination: `/topic/partida/${this.codigoSala}/turno`,
        body: JSON.stringify({
          tipo: this.modoEventos ? 'EVENTOS_INICIADOS' : 'EVENTOS_FINALIZADOS',
          nombreJugador: null,
        }),
      })
    },

    async activarTurnoJugador(jugador) {
      if (!this.modoEventos) return

      // Permitir activar al Cazador aunque esté muerto
      const esCazadorMuerto =
        (jugador.nombreRol || '').toLowerCase() === 'cazador' && !jugador.estaVivo

      if (!jugador.estaVivo && !esCazadorMuerto) {
        // No activar jugadores muertos que no sean el Cazador
        return
      }

      if (jugador.nombreRol === 'Cupido' && this.cupidoUsado) {
        this.avisoSesion = '¡Cupido ya usó sus poderes, no puede volver a usarlos en esta partida!'
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
      }

      if (
        jugador.nombreRol === 'Vidente' &&
        this.jugadoresYaActuadosEstaNoche.includes(jugador.nombre)
      ) {
        this.avisoSesion = '¡La Vidente ya usó su poder esta noche!'
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
      }

      if (
        jugador.nombreRol === 'Niña' &&
        this.jugadoresYaActuadosEstaNoche.includes(jugador.nombre)
      ) {
        this.avisoSesion = '¡La Niña ya usó su poder esta noche!'
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
      }

      if (this.idSesionActual) {
        this.avisoSesion = `Cierra el turno de ${this.jugadorSeleccionado?.nombre} antes de activar otro jugador`
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
      }

      this.jugadorSeleccionado = jugador
      this.$store.dispatch('sala/setTurnoActivo', jugador)

      const esLobo = jugador.nombreRol === 'Lobo'

      if (esLobo) {
        try {
          const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
            tipo: 'LOBOS',
          })
          this.idSesionActual = res.data
          this.sesionActualTipo = 'LOBOS'

          const lobosVivos = this.jugadoresConRol.filter(
            (j) => j.nombreRol === 'Lobo' && j.estaVivo,
          )
          this.stompClient.publish({
            destination: `/topic/partida/${this.codigoSala}/turno`,
            body: JSON.stringify({
              tipo: 'TURNO_LOBOS',
              nombresLobos: lobosVivos.map((j) => j.nombre),
            }),
          })
        } catch (error) {
          alert(
            error.response?.status === 409
              ? 'Ya hay una votación abierta'
              : 'Error al iniciar turno de lobos',
          )
        }
      } else {
        try {
          const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
            tipo: 'HABILIDAD',
          })
          this.idSesionActual = res.data
          this.sesionActualTipo = 'HABILIDAD'

          this.stompClient.publish({
            destination: `/topic/partida/${this.codigoSala}/turno`,
            body: JSON.stringify({
              tipo: 'TURNO_JUGADOR',
              nombreJugador: jugador.nombre,
            }),
          })
        } catch (error) {
          alert(
            error.response?.status === 409
              ? 'Ya hay una votación abierta'
              : 'Error al iniciar turno',
          )
        }
      }
      if (
        jugador.nombreRol === 'Niño Salvaje' &&
        this.jugadoresYaActuadosEstaNoche.includes(jugador.nombre)
      ) {
        this.avisoSesion = '¡El Niño Salvaje ya eligió su mentor!'
        setTimeout(() => {
          this.avisoSesion = null
        }, 4000)
        return
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
  align-self: flex-start;
}

.nombre-dia {
  background: #000;
}
.nombre-noche {
  background: white;
}

.carta-fase {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 15px;
  width: clamp(140px, 22vw, 270px);
  flex-shrink: 0;
  min-width: 210px;
  margin-left: 23px;
}

.carta-dia {
  background: white;
  border: 8px solid #e4ba03;
}
.carta-noche {
  background: #000;
  border: 8px solid #cc0000;
}

.carta-fase-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(1.2rem, 3vw, 2.5rem);
  margin: 0;
  text-align: center;
}

.carta-dia .carta-fase-titulo {
  color: #e4ba03;
}
.carta-noche .carta-fase-titulo {
  color: #cc0000;
}

.carta-fase-img {
  width: 100%;
  aspect-ratio: 1/1;
  object-fit: cover;
  border-radius: 10px;
}

.carta-dia .carta-fase-img {
  border: 5px solid #e4ba03;
}
.carta-noche .carta-fase-img {
  border: 5px solid #cc0000;
}

.carta-fase-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(0.85rem, 1.2vw, 1.2rem);
  text-align: center;
  line-height: 1.4;
  margin: 0;
  font-style: italic;
}

.carta-dia .carta-fase-texto {
  color: #e4ba03;
}
.carta-noche .carta-fase-texto {
  color: #cc0000;
}

.mesa-wrapper-outer :deep(.mesa-wrapper) {
  border-width: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
}

.seccion-info {
  border-radius: 12px;
  padding: 24px;
}

.seccion-dia {
  background: rgba(0, 0, 0, 0.7);
  border: 5px solid #e4ba03;
}

.seccion-noche {
  background: rgba(0, 0, 0, 0.85);
  border: 5px solid #cc0000;
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

@media (max-width: 768px) {
  .cabecera {
    flex-direction: column;
    align-items: center;
  }
  .carta-fase {
    width: 60%;
    max-width: 200px;
  }
}

@media (max-width: 600px) {
  .contenido {
    width: 95%;
    padding-top: 20px;
  }
  .nombre-box {
    font-size: 1rem;
  }
}

.aviso-sesion {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.85);
  border: 2px solid #e4ba03;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
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

.columna-izquierda {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cuadro-alcalde {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 250px;
  padding: 14px 20px;
  border-radius: 10px;
  background: #e4ba03;
  border: 3px white solid;
  color: #000;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: bold;
}

.cuadro-alcalde i {
  font-size: 3rem;
  flex-shrink: 0;
}

.alcalde-texto {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.alcalde-titulo {
  font-size: 1.2rem;
  font-weight: bolder;
}

.alcalde-frase {
  font-size: 1rem;
  font-style: italic;
  font-weight: 400;
  color: #ffffff;
}
</style>
