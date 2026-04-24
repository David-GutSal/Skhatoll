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
        @votarLinchamiento="iniciarVotacionLinchamiento"
        @votarAlcalde="iniciarVotacionAlcalde"
        @finalizarVotacion="finalizarVotacion"
        @eventos="iniciarEventos"
        @verPersonajes="seccionActiva = seccionActiva === 'personajes' ? null : 'personajes'"
        @verReglas="seccionActiva = seccionActiva === 'reglas' ? null : 'reglas'"
      />

      <div class="mesa-wrapper-outer">
        <MesaJugadores
          :jugadores="jugadoresConRol"
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
    // 🟢 RECUPERAR SALA SI SE PERDIÓ
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
    // 1. Cargar jugadores
    try {
      const res = await axiosInstance.get(`/salas/${this.codigoSala}/roles`)
      this.$store.dispatch('sala/setJugadoresConRol', res.data)
    } catch (error) {
      alert('Error al cargar jugadores')
    }

    // 2. 🔥 NUEVO: comprobar sesión activa
    try {
      const res = await axiosInstance.get(`/partida/${this.codigoSala}/sesion-activa`)

      console.log('📡 SESION ACTIVA:', res.data)

      if (res.data?.abierta) {
        this.idSesionActual = res.data.idSesion
        console.log('🟢 SESION RECUPERADA:', this.idSesionActual)
      }
    } catch (error) {
      console.log('ℹ️ No hay sesión activa (normal)')
    }

    // 3. Conectar WS
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
        this.esDia = fase === 'DIA'
        await axiosInstance.put(`/partida/${this.codigoSala}/fase`, { fase })
      } catch (error) {
        alert('Error al cambiar la fase')
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
    cliente.subscribe(`/topic/partida/${this.codigoSala}/alcalde`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (payload.tipo === 'ALCALDE_ELEGIDO') {
        this.$store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
      }
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
    cliente.subscribe(`/topic/partida/${this.codigoSala}/votacion`, (msg) => {
      const payload = JSON.parse(msg.body)
      console.log('📩 VOTACION WS:', payload)

      if (payload.tipo === 'VOTACION_ABIERTA') {
        this.idSesionActual = payload.idSesion
      }
      if (payload.tipo === 'VOTACION_CERRADA') {
        this.idSesionActual = null
      }
      if (payload.tipo === 'ALCALDE' || payload.tipo === 'DIA' || payload.tipo === 'LOBOS') {
        this.idSesionActual = null
      }
    })

    // ✅ DENTRO del onConnect
    cliente.subscribe(`/topic/partida/${this.codigoSala}/turno`, (msg) => {
      const payload = JSON.parse(msg.body)

      if (payload.tipo === 'TURNO_FINALIZADO') {
        this.avisoSesion = `${payload.nombreJugador} ha finalizado su turno`
        setTimeout(() => { this.avisoSesion = null }, 4000)

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
    })
  }

  cliente.activate()
  this.stompClient = cliente
},

    /* CODIGO DE PRUEBAS DE FUNCIONAMIENTO- BORRAR AL TERMINAR
    conectarWebSocket() {
  const token = this.$store.getters['auth/token']

  const cliente = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    connectHeaders: { Authorization: `Bearer ${token}` },
  })

  cliente.onConnect = () => {
    console.log("🟢 WS CONECTADO")

    cliente.subscribe(`/topic/partida/${this.codigoSala}/votacion`, (msg) => {
      console.log("📩 VOTACION WS:", msg.body)

      const payload = JSON.parse(msg.body)
      if (payload.idSesion) {
        this.idSesionActual = payload.idSesion
        console.log("✅ ID SESION ACTUAL:", this.idSesionActual)
      }
    })

    cliente.subscribe(`/topic/partida/${this.codigoSala}/fase`, (msg) => {
      console.log("📩 FASE WS:", msg.body)
    })

    cliente.subscribe(`/topic/partida/${this.codigoSala}/muerte`, (msg) => {
      console.log("📩 MUERTE WS:", msg.body)
    })
  }

  cliente.onStompError = (frame) => {
    console.error("❌ ERROR WS:", frame)
  }

  cliente.activate()
  this.stompClient = cliente
},
*/

    async iniciarVotacionLinchamiento() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
          tipo: 'DIA',
        })

        console.log('🟢 RESPUESTA ABRIR VOTACION:', res.data)

        this.idSesionActual = res.data

        // 🔥 AÑADE ESTO
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

    /* VOTACION DE ALCALDE DESACTIVADA TEMPORALMENTE
async iniciarVotacionAlcalde() {
  console.log("🔥🔥🔥 ALCALDE CLICK 🔥🔥🔥")

  try {
    const res = await axiosInstance.post(
      `/partida/${this.codigoSala}/votacion/abrir`,
      { tipo: 'ALCALDE' }
    )

    this.idSesionActual = res.data
  } catch (error) {
    console.log(error.response?.data)
  }
},
*/
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
      } catch (error) {
        alert('Error al finalizar votación')
      }
    },

    iniciarEventos() {
      this.modoEventos = !this.modoEventos

      this.stompClient.publish({
        destination: `/topic/partida/${this.codigoSala}/turno`,
        body: JSON.stringify({
          tipo: this.modoEventos ? 'EVENTOS_INICIADOS' : 'EVENTOS_FINALIZADOS',
          nombreJugador: null,
        }),
      })

      if (!this.modoEventos) {
        this.jugadorSeleccionado = null
        this.avisoSesion = null
        this.$store.dispatch('sala/setTurnoActivo', null)
      }
    },

    async iniciarVotacionNoche() {
      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/votacion/abrir`, {
          tipo: 'LOBOS',
        })
        this.idSesionActual = res.data
      } catch (error) {
        alert(
          error.response?.status === 409
            ? 'Ya hay una votación abierta'
            : 'Error al iniciar eventos nocturnos',
        )
      }
    },

    async activarTurnoJugador(jugador) {
      if (!this.modoEventos) return

      // Si hay sesión abierta, avisar y no permitir cambiar
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
        // Abrir sesión LOBOS y notificar a todos los lobos
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
        // Abrir sesión HABILIDAD y notificar al jugador concreto
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
</style>
