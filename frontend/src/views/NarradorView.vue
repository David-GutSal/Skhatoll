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

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import IndicadorDiaNoche from '@/components/juego/IndicadorDiaNoche.vue'
import PanelControlNarrador from '@/components/juego/PanelControlNarrador.vue'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'
import solImg from '@/assets/imgs/sol.jpg'
import lunaImg from '@/assets/imgs/luna.jpg'
import ListaPersonajes from '@/components/juego/ListaPersonajes.vue'
import ListaReglas from '@/components/juego/ListaReglas.vue'

const router = useRouter()
const store = useStore()

const esDia = ref(true)
const stompClient = ref(null)
const idSesionActual = ref(null)
const seccionActiva = ref(null)
const modoEventos = ref(false)
const jugadorSeleccionado = ref(null)
const sesionActualTipo = ref(null)
const avisoSesion = ref(null)
const jugadoresYaActuadosEstaNoche = ref([])
const eventosYaUsadosEstaNoche = ref(false)
const votacionActiva = ref(false)
const tipoVotacionLocal = ref(null)

const nombre = computed(() => store.getters['auth/nombre'])
const codigoSala = computed(() => store.getters['sala/codigoSala'])
const jugadoresConRol = computed(() => store.getters['sala/jugadoresConRol'])
const enamorados = computed(() => store.getters['sala/enamorados'])
const cupidoUsado = computed(() => store.getters['sala/cupidoUsado'])
const semiMuertos = computed(() => store.getters['sala/semiMuertos'])

const hayAlcalde = computed(() => jugadoresConRol.value.some((j) => j.alcalde))

const jugadoresConRolConEnamorados = computed(() => {
  if (!enamorados.value) return jugadoresConRol.value
  return jugadoresConRol.value.map((j) => ({
    ...j,
    esEnamorado: j.nombre === enamorados.value.jugador1 || j.nombre === enamorados.value.jugador2,
  }))
})

const textoAlcalde = computed(() => {
  const alcalde = jugadoresConRol.value.find((j) => j.alcalde)
  return alcalde ? alcalde.nombre : 'Elecciones pendientes'
})

const cargarDatos = async () => {
  if (!codigoSala.value) {
    const codigoGuardado = sessionStorage.getItem('codigoSala')
    if (codigoGuardado) {
      store.dispatch('sala/unirse', codigoGuardado)
    } else {
      store.dispatch('toast/mostrar', { mensaje: 'No hay sala activa', tipo: 'error' })
      router.push('/')
      return
    }
  }

  try {
    const res = await axiosInstance.get(`/salas/${codigoSala.value}/roles`)
    store.dispatch('sala/setJugadoresConRol', res.data)
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al cargar jugadores', tipo: 'error' })
  }

  try {
    const res = await axiosInstance.get(`/partida/${codigoSala.value}/sesion-activa`)
    if (res.data?.abierta) {
      idSesionActual.value = res.data.idSesion
    }
  } catch {
    // No hay sesión activa, es normal
  }

  conectarWebSocket()
}

const cambiarFase = async (fase) => {
  try {
    const esNoche = fase === 'noche'
    esDia.value = !esNoche

    if (esNoche) {
      eventosYaUsadosEstaNoche.value = false
      jugadoresYaActuadosEstaNoche.value = []
    } else {
      await confirmarMuertesSemimuertos()
      store.dispatch('sala/reiniciarVotos')
      store.dispatch('sala/setTipoVotacion', null)
    }

    await axiosInstance.put(`/partida/${codigoSala.value}/fase`)
    store.dispatch('toast/mostrar', {
      mensaje: esNoche ? 'Cae la noche sobre Castronegro' : 'La aldea despierta ¡Buenos días!',
      tipo: esNoche ? 'noche' : 'dia',
    })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje:
        error.response?.status === 409
          ? 'Cierra la votación antes de cambiar la fase'
          : 'Error al cambiar la fase',
      tipo: 'error',
    })
  }
}

const confirmarMuertesSemimuertos = async () => {
  const semimuertos = [...semiMuertos.value]
  console.log('🌅 Confirmando muertes al amanecer:', semimuertos)
  for (const nombre of semimuertos) {
    const jugador = jugadoresConRol.value.find((j) => j.nombre === nombre)
    if (jugador) {
      try {
        await axiosInstance.put(
          `/partida/${codigoSala.value}/jugador/${jugador.idUsuario}/confirmar-muerte`,
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

      if (payload.fase === 'NOCHE') {
        eventosYaUsadosEstaNoche.value = false
        jugadoresYaActuadosEstaNoche.value = []
      }

      if (payload.fase === 'DIA') {
        store.dispatch('sala/reiniciarVotos')
        store.dispatch('sala/setTipoVotacion', null)
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/muerte`, (msg) => {
      const payload = JSON.parse(msg.body)
      store.dispatch('sala/marcarMuerto', payload.nombreJugador)
      store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/alcalde`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (payload.tipo === 'ALCALDE_ELEGIDO') {
        store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
        store.dispatch('sala/reiniciarVotos')
        store.dispatch('toast/mostrar', {
          mensaje: `¡${payload.nombreAlcalde} ha sido elegido alcalde!`,
          tipo: 'aviso',
        })
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votos`, (msg) => {
      const payload = JSON.parse(msg.body)
      store.dispatch('sala/actualizarVotos', payload.votos)
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/fin`, (msg) => {
      const payload = JSON.parse(msg.body)
      store.dispatch('sala/setResultado', {
        bandoGanador: payload.bandoGanador,
        mensaje: payload.mensaje,
      })
      router.push({ name: 'resultados' })
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votacion`, (msg) => {
      const payload = JSON.parse(msg.body)
      console.log('📩 VOTACION PAYLOAD COMPLETO:', JSON.stringify(payload))

      if (payload.tipo === 'VOTACION_ABIERTA') {
        votacionActiva.value = true
        tipoVotacionLocal.value = payload.tipoVotacion
        store.dispatch('sala/setTipoVotacion', payload.tipoVotacion)

        switch (payload.tipoVotacion) {
          case 'ALCALDE':
            store.dispatch('toast/mostrar', {
              mensaje: 'Elecciones de alcalde abiertas',
              tipo: 'info',
            })
            break
          case 'DIA':
            store.dispatch('toast/mostrar', {
              mensaje: 'Votación de linchamiento en curso',
              tipo: 'info',
            })
            break
          case 'LOBOS':
            store.dispatch('toast/mostrar', {
              mensaje: 'Los lobos salen de cazer��a...',
              tipo: 'licantropia',
            })
            break
        }
        return
      }

      if (payload.tipo === 'VOTACION_CERRADA') {
        votacionActiva.value = false
        tipoVotacionLocal.value = null
        store.dispatch('sala/setTipoVotacion', null)
        return
      }

      if (payload.tipo === 'LOBOS' && payload.nombreEliminado) {
        store.dispatch('sala/marcarSemimuerto', payload.nombreEliminado)
        avisoSesion.value = `Los lobos han devorado a ${payload.nombreEliminado} esta noche`
        setTimeout(() => {
          avisoSesion.value = null
        }, 6000)
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/turno`, (msg) => {
      const payload = JSON.parse(msg.body)

      if (payload.tipo === 'TURNO_FINALIZADO') {
        if (
          jugadorSeleccionado.value &&
          !jugadoresYaActuadosEstaNoche.value.includes(jugadorSeleccionado.value.nombre)
        ) {
          jugadoresYaActuadosEstaNoche.value.push(jugadorSeleccionado.value.nombre)
        }

        avisoSesion.value = `${payload.nombreJugador} ha finalizado su turno`
        setTimeout(() => {
          avisoSesion.value = null
        }, 4000)

        if (idSesionActual.value) {
          axiosInstance
            .put(`/partida/${codigoSala.value}/votacion/${idSesionActual.value}/cerrar`)
            .then(() => {
              idSesionActual.value = null
              sesionActualTipo.value = null
              jugadorSeleccionado.value = null
            })
            .catch(() => {})
        }
      }

      if (payload.tipo === 'BRUJA_VIDA') {
        store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)
        avisoSesion.value = `La bruja ha salvado a ${payload.nombreJugador} esta noche`
        setTimeout(() => {
          avisoSesion.value = null
        }, 5000)
        return
      }

      if (payload.tipo === 'FLECHAZO') {
        store.dispatch('sala/setEnamorados', {
          jugador1: payload.jugador1,
          jugador2: payload.jugador2,
        })
        store.dispatch('sala/setCupidoUsado')
      }

      if (payload.tipo === 'MUERTE_ENAMORADO') {
        const jugador = jugadoresConRol.value.find((j) => j.nombre === payload.nombreJugador)
        if (jugador) {
          axiosInstance
            .put(`/partida/${codigoSala.value}/jugador/${jugador.idUsuario}/confirmar-muerte`)
            .catch(() => {})
        }
      }
      if (payload.tipo === 'MENTOR_ELEGIDO') {
        const mentor = jugadoresConRol.value.find((j) => j.nombre === payload.nombreMentor)
        if (mentor) mentor.esMentor = true

        avisoSesion.value = `El Niño Salvaje ha elegido a ${payload.nombreMentor} como su mentor`
        setTimeout(() => {
          avisoSesion.value = null
        }, 5000)
        return
      }
    })
  }

  cliente.activate()
  stompClient.value = cliente
}

const iniciarVotacionLinchamiento = async () => {
  try {
    const res = await axiosInstance.post(`/partida/${codigoSala.value}/votacion/abrir`, {
      tipo: 'DIA',
    })
    idSesionActual.value = res.data
    votacionActiva.value = true
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje:
        error.response?.status === 409
          ? 'Ya hay una votación abierta'
          : 'Error al iniciar votación',
      tipo: 'error',
    })
  }
}

const iniciarVotacionAlcalde = async () => {
  try {
    const res = await axiosInstance.post(`/partida/${codigoSala.value}/votacion/abrir`, {
      tipo: 'ALCALDE',
    })
    idSesionActual.value = res.data
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje:
        error.response?.status === 409
          ? 'Ya hay una votación abierta'
          : 'Error al iniciar elecciones',
      tipo: 'error',
    })
  }
}

const finalizarVotacion = async () => {
  if (!idSesionActual.value) {
    store.dispatch('toast/mostrar', {
      mensaje: 'No hay ninguna votación activa',
      tipo: 'aviso',
    })
    return
  }
  try {
    await axiosInstance.put(
      `/partida/${codigoSala.value}/votacion/${idSesionActual.value}/cerrar`,
    )
    idSesionActual.value = null
    store.dispatch('sala/reiniciarVotos')
    store.dispatch('sala/setTipoVotacion', null)
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: 'Error al finalizar votación',
      tipo: 'error',
    })
  }
}

const iniciarEventos = () => {
  if (!modoEventos.value && eventosYaUsadosEstaNoche.value) {
    avisoSesion.value = 'Los eventos nocturnos ya han terminado esta noche'
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }

  modoEventos.value = !modoEventos.value

  if (modoEventos.value) {
    jugadoresYaActuadosEstaNoche.value = []
  } else {
    eventosYaUsadosEstaNoche.value = true
    jugadorSeleccionado.value = null
    avisoSesion.value = null
    store.dispatch('sala/setTurnoActivo', null)
  }

  stompClient.value.publish({
    destination: `/topic/partida/${codigoSala.value}/turno`,
    body: JSON.stringify({
      tipo: modoEventos.value ? 'EVENTOS_INICIADOS' : 'EVENTOS_FINALIZADOS',
      nombreJugador: null,
    }),
  })
}

const activarTurnoJugador = async (jugador) => {
  if (!modoEventos.value) return

  const esCazadorMuerto =
    (jugador.nombreRol || '').toLowerCase() === 'cazador' && !jugador.estaVivo

  if (!jugador.estaVivo && !esCazadorMuerto) {
    return
  }

  if (jugador.nombreRol === 'Cupido' && cupidoUsado.value) {
    avisoSesion.value = '¡Cupido ya usó sus poderes, no puede volver a usarlos en esta partida!'
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }

  if (
    jugador.nombreRol === 'Vidente' &&
    jugadoresYaActuadosEstaNoche.value.includes(jugador.nombre)
  ) {
    avisoSesion.value = '¡La Vidente ya usó su poder esta noche!'
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }

  if (
    jugador.nombreRol === 'Niña' &&
    jugadoresYaActuadosEstaNoche.value.includes(jugador.nombre)
  ) {
    avisoSesion.value = '¡La Niña ya usó su poder esta noche!'
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }

  if (idSesionActual.value) {
    avisoSesion.value = `Cierra el turno de ${jugadorSeleccionado.value?.nombre} antes de activar otro jugador`
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }

  jugadorSeleccionado.value = jugador
  store.dispatch('sala/setTurnoActivo', jugador)

  const esLobo = jugador.nombreRol === 'Lobo'

  if (esLobo) {
    try {
      const res = await axiosInstance.post(`/partida/${codigoSala.value}/votacion/abrir`, {
        tipo: 'LOBOS',
      })
      idSesionActual.value = res.data
      sesionActualTipo.value = 'LOBOS'

      const lobosVivos = jugadoresConRol.value.filter(
        (j) => j.nombreRol === 'Lobo' && j.estaVivo,
      )
      stompClient.value.publish({
        destination: `/topic/partida/${codigoSala.value}/turno`,
        body: JSON.stringify({
          tipo: 'TURNO_LOBOS',
          nombresLobos: lobosVivos.map((j) => j.nombre),
        }),
      })
    } catch (error) {
      store.dispatch('toast/mostrar', {
        mensaje:
          error.response?.status === 409
            ? 'Ya hay una votación abierta'
            : 'Error al iniciar turno de lobos',
        tipo: 'error',
      })
    }
  } else {
    try {
      const res = await axiosInstance.post(`/partida/${codigoSala.value}/votacion/abrir`, {
        tipo: 'HABILIDAD',
      })
      idSesionActual.value = res.data
      sesionActualTipo.value = 'HABILIDAD'

      stompClient.value.publish({
        destination: `/topic/partida/${codigoSala.value}/turno`,
        body: JSON.stringify({
          tipo: 'TURNO_JUGADOR',
          nombreJugador: jugador.nombre,
          rolActivo: jugador.nombreRol,
        }),
      })

      const toastsPorRol = {
        Vidente: { mensaje: 'La Vidente está teniendo una visión...', tipo: 'videncia' },
        Bruja: { mensaje: 'La Bruja prepara sus pociones...', tipo: 'brujeria' },
        Cupido: { mensaje: 'Cupido está lanzando sus flechas de amor...', tipo: 'amorio' },
      }

      const toastRol = toastsPorRol[jugador.nombreRol]
      if (toastRol) {
        store.dispatch('toast/mostrar', toastRol)
      }
    } catch (error) {
      store.dispatch('toast/mostrar', {
        mensaje:
          error.response?.status === 409
            ? 'Ya hay una votación abierta'
            : 'Error al iniciar turno',
        tipo: 'error',
      })
    }
  }
  if (
    jugador.nombreRol === 'Niño Salvaje' &&
    jugadoresYaActuadosEstaNoche.value.includes(jugador.nombre)
  ) {
    avisoSesion.value = '¡El Niño Salvaje ya eligió su mentor!'
    setTimeout(() => {
      avisoSesion.value = null
    }, 4000)
    return
  }
}

onMounted(() => {
  cargarDatos()
})

onUnmounted(() => {
  if (stompClient.value) {
    stompClient.value.deactivate()
    stompClient.value = null
  }
})
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
  font-family: var(--font-cinzel);
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--color-rojo);
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
  border: 8px solid var(--color-dorado);
}
.carta-noche {
  background: #000;
  border: 8px solid var(--color-rojo);
}

.carta-fase-titulo {
  font-family: var(--font-cinzel);
  font-weight: 700;
  font-size: clamp(1.2rem, 3vw, 2.5rem);
  margin: 0;
  text-align: center;
}

.carta-dia .carta-fase-titulo {
  color: var(--color-dorado);
}
.carta-noche .carta-fase-titulo {
  color: var(--color-rojo);
}

.carta-fase-img {
  width: 100%;
  aspect-ratio: 1/1;
  object-fit: cover;
  border-radius: 10px;
}

.carta-dia .carta-fase-img {
  border: 5px solid var(--color-dorado);
}
.carta-noche .carta-fase-img {
  border: 5px solid var(--color-rojo);
}

.carta-fase-texto {
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: clamp(0.85rem, 1.2vw, 1.2rem);
  text-align: center;
  line-height: 1.4;
  margin: 0;
  font-style: italic;
}

.carta-dia .carta-fase-texto {
  color: var(--color-dorado);
}
.carta-noche .carta-fase-texto {
  color: var(--color-rojo);
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
  border: 5px solid var(--color-dorado);
}

.seccion-noche {
  background: rgba(0, 0, 0, 0.85);
  border: 5px solid var(--color-rojo);
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
  border: 2px solid var(--color-dorado);
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  animation: fade-in-up 0.4s ease;
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
  background: var(--color-dorado);
  border: 3px white solid;
  color: #000;
  font-family: var(--font-raleway);
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
