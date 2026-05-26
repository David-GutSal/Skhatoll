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
        role="alert"
        aria-live="polite"
      >
        <i v-if="tipoVotacionLocal === 'ALCALDE'" class="fa-solid fa-medal" aria-hidden="true"></i>
        <i v-else-if="tipoVotacionLocal === 'DIA'" class="fa-solid fa-gavel" aria-hidden="true"></i>
        <i
          v-else-if="tipoVotacionLocal === 'LOBOS'"
          class="fa-solid fa-skull"
          aria-hidden="true"
        ></i>
        <i v-else class="fa-solid fa-bell" aria-hidden="true"></i>
        <span>{{ mensajeEvento }}</span>
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
          <span>Es tu turno! Selecciona un jugador y activa tu poder</span>
        </div>
        <button
          class="btn-ir-poderes"
          @click="scrollAPoderes"
          aria-label="Desplazarse a la seccion de poderes"
        >
          <i class="fa-solid fa-arrow-down" aria-hidden="true"></i>
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
        :cazadorMuerto="soyElCazadorMuerto"
        @devorar="devorarJugador"
        @premonicion="usarPremonicion"
        @flechazo="manejarFlechazo"
        @finalizarTurno="finalizarTurno"
        @envenenar="manejarEnvenenar"
        @vidaUsada="manejarVidaUsada"
        @disparo="manejarDisparo"
        @mentorElegido="manejarMentorElegido"
      />
    </div>

    <div class="boton-arriba-wrapper">
      <button class="boton-arriba" @click="irArriba" aria-label="Volver al inicio de la pagina">
        <i class="fa-solid fa-arrow-up" aria-hidden="true"></i> Volver Arriba
      </button>
      <button
        v-if="!soyNarrador"
        class="boton-rendirse"
        @click="confirmarRendirse"
        aria-label="Rendirse y abandonar la partida"
      >
        <i class="fa-solid fa-flag" aria-hidden="true"></i> Rendirse
      </button>
    </div>
    <!-- Modal rendirse -->
    <Transition name="modal">
      <div
        v-if="mostrarModalRendirse"
        class="modal-overlay"
        @click.self="mostrarModalRendirse = false"
      >
        <div class="modal-rendirse">
          <p class="modal-pregunta">
            ¿Estás seguro de que quieres rendirte?
            <i class="fa-solid fa-flag"></i>
          </p>
          <p class="modal-subtitulo">Serás eliminado de la partida.</p>
          <div class="modal-botones">
            <button class="btn-modal-rendirse" @click="ejecutarRendirse">Rendirse</button>
            <button class="btn-modal-cancelar" @click="mostrarModalRendirse = false">
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </Transition>
    <div class="footer-aldea" :class="esDia ? 'footer-dia' : 'footer-noche'"></div>
  </div>
</template>

<script setup>
/**
 * @typedef {Object} Jugador
 * @property {number} idUsuario
 * @property {string} nombre
 * @property {boolean} estaVivo
 */

import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import CabeceraJugador from '@/components/juego/CabeceraJugador.vue'
import PanelVotacionesJugador from '@/components/juego/PanelVotacionesJugador.vue'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'
import BotonMiRol from '@/components/juego/BotonMiRol.vue'
import ZonaPoderes from '@/components/juego/ZonaPoderes.vue'

const router = useRouter()
const store = useStore()
const zonaPoderes = ref(null)

/** @type {import('vue').Ref<boolean>} */
const esDia = ref(true)
/** @type {import('vue').Ref<boolean>} */
const votacionActiva = ref(false)
/** @type {import('vue').Ref<string|null>} */
const tipoVotacionLocal = ref(null)
/** @type {import('vue').Ref<boolean>} */
const esMiTurno = ref(false)
/** @type {import('vue').Ref<Client|null>} */
const stompClient = ref(null)
/** @type {import('vue').Ref<string|null>} */
const mensajeEvento = ref(null)
/** @type {import('vue').Ref<string|null>} */
const alcaldeNombre = ref(null)
/** @type {import('vue').Ref<Jugador|null>} */
const jugadorEnvenenado = ref(null)
/** @type {import('vue').Ref<boolean>} */
const soyElCazadorMuerto = ref(false)
/** @type {import('vue').Ref<Jugador|null>} */
const jugadorSeleccionado = ref(null)

/** @type {import('vue').ComputedRef<string>} */
const nombre = computed(() => store.getters['auth/nombre'])
/** @type {import('vue').ComputedRef<string>} */
const codigoSala = computed(() => store.getters['sala/codigoSala'])
/** @type {import('vue').ComputedRef<Jugador[]>} */
const jugadores = computed(() => store.getters['sala/jugadores'])
/** @type {import('vue').ComputedRef<string|null>} */
const miRol = computed(() => store.getters['sala/miRol'])
/** @type {import('vue').ComputedRef<{jugador1: string, jugador2: string}|null>} */
const enamorados = computed(() => store.getters['sala/enamorados'])

const nombreNarrador = computed(() => {
  const narrador = jugadores.value.find((j) => j.esNarrador === true)
  return narrador ? narrador.nombre : 'Esperando narrador...'
})

const soyNarrador = computed(() => {
  return jugadores.value.some((j) => j.esNarrador === true && j.nombre === nombre.value)
})

const jugadoresVisibles = computed(() => {
  if (!esDia.value && miRol.value && (miRol.value.toLowerCase() === 'lobo' || miRol.value === 'NIÑO LOBO')) {
    return jugadores.value.filter((j) => j.nombre !== nombre.value)
  }
  return jugadores.value
})

/**
 * Scroll to powers section with reduced motion preference
 */
const scrollAPoderes = () => {
  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  zonaPoderes.value?.$el?.scrollIntoView({
    behavior: prefersReducedMotion ? 'auto' : 'smooth',
  })
}

/**
 * Scroll to top of page with reduced motion preference
 */
const irArriba = () => {
  const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({
    top: 0,
    behavior: prefersReducedMotion ? 'auto' : 'smooth',
  })
}

const mostrarModalRendirse = ref(false)

const confirmarRendirse = () => {
  mostrarModalRendirse.value = true
}

const ejecutarRendirse = async () => {
  mostrarModalRendirse.value = false
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/rendirse`)
    store.dispatch('toast/mostrar', { mensaje: 'Te has rendido', tipo: 'info' })
    if (stompClient.value) {
      stompClient.value.deactivate()
      stompClient.value = null
    }
    store.dispatch('sala/resetSala')
    router.push({ name: 'inicio' })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: error.response?.data?.mensaje || 'Error al rendirse',
      tipo: 'error',
    })
  }
}

/**
 * Select a player for voting or abilities
 * @param {Jugador} j - Player to select
 */
const seleccionarJugador = (j) => {
  if (enamorados.value) {
    const miNombre = nombre.value
    const { jugador1, jugador2 } = enamorados.value
    const soyEnamorado = jugador1 === miNombre || jugador2 === miNombre
    if (soyEnamorado) {
      const nombrePareja = jugador1 === miNombre ? jugador2 : jugador1
      if (j.nombre === nombrePareja) return
    }
  }
  if (
    tipoVotacionLocal.value === 'LOBOS' &&
    miRol.value &&
    (miRol.value.toLowerCase() === 'lobo' || miRol.value === 'NIÑO LOBO') &&
    j.bando === 'lobo'
  ) {
    return
  }
  if (!votacionActiva.value && !esMiTurno.value && !soyElCazadorMuerto.value) return
  jugadorSeleccionado.value = j
}

const votarAlcalde = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: error.response?.data?.mensaje || 'Error al votar',
      tipo: 'error',
    })
  }
}

const votarCulpable = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: error.response?.data?.mensaje || 'Error al votar',
      tipo: 'error',
    })
  }
}

const devorarJugador = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: error.response?.data?.mensaje || 'Error al devorar',
      tipo: 'error',
    })
  }
}

const usarPremonicion = () => {
  if (!jugadorSeleccionado.value) return
}

const manejarFlechazo = (pareja) => {
  stompClient.value.publish({
    destination: `/topic/partida/${codigoSala.value}/turno`,
    body: JSON.stringify({
      tipo: 'FLECHAZO',
      jugador1: pareja.jugador1.nombre,
      jugador2: pareja.jugador2.nombre,
    }),
  })
  finalizarTurno()
}

const manejarEnvenenar = (jugador) => {
  jugadorEnvenenado.value = jugador
}

const manejarVidaUsada = (nombreJugador) => {
  stompClient.value?.publish({
    destination: `/topic/partida/${codigoSala.value}/turno`,
    body: JSON.stringify({ tipo: 'BRUJA_VIDA', nombreJugador }),
  })
}

const manejarMentorElegido = (data) => {
  stompClient.value.publish({
    destination: `/topic/partida/${codigoSala.value}/turno`,
    body: JSON.stringify({
      tipo: 'MENTOR_ELEGIDO',
      nombreNinno: data.nombreNinno,
      nombreMentor: data.nombreMentor,
    }),
  })
}

const finalizarTurno = () => {
  esMiTurno.value = false
  jugadorSeleccionado.value = null
  stompClient.value.publish({
    destination: `/topic/partida/${codigoSala.value}/turno`,
    body: JSON.stringify({
      tipo: 'TURNO_FINALIZADO',
      nombreJugador: nombre.value,
    }),
  })
  if (soyElCazadorMuerto.value) {
    setTimeout(() => {
      if (router.currentRoute.value.name === 'jugador') {
        router.push({ name: 'eliminado' })
      }
    }, 1500)
  }
}

let disparoEnProgreso = false
const manejarDisparo = async (jugador) => {
  if (disparoEnProgreso) return
  disparoEnProgreso = true

  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/habilidad`, {
      nombreHabilidad: 'disparo',
      objetivos: [jugador.idUsuario],
    })
    store.dispatch('toast/mostrar', {
      mensaje: `¡Has eliminado a ${jugador.nombre}!`,
      tipo: 'exito',
    })

    finalizarTurno()
  } catch (error) {
    disparoEnProgreso = false
    store.dispatch('toast/mostrar', {
      mensaje: error.response?.data?.mensaje || 'Error al disparar',
      tipo: 'error',
    })
  }
}

const cargarDatos = async () => {
  try {
    const res = await axiosInstance.get(`/salas/${codigoSala.value}/jugadores`)
    store.dispatch('sala/setJugadores', res.data)
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al cargar jugadores', tipo: 'error' })
  }

  try {
    const miRolRes = await axiosInstance.get(`/salas/${codigoSala.value}/mi-rol`)
    if (miRolRes.data?.nombreRol) {
      store.dispatch('sala/setRol', {
        nombreRol: miRolRes.data.nombreRol,
        descripcionRol: miRolRes.data.descripcionRol || '',
        bando: miRolRes.data.bando || 'aldea',
      })
    }
  } catch (error) {
    /* rol no disponible aún */
  }

  try {
    const estado = await axiosInstance.get(`/partida/${codigoSala.value}/estado`)
    if (estado.data?.estadoDia) {
      const esNoche = estado.data.estadoDia === 'NOCHE'
      esDia.value = !esNoche
      store.dispatch('sala/setFase', estado.data.estadoDia)

      if (estado.data.nombreAlcalde) {
        store.dispatch('sala/designarAlcalde', estado.data.nombreAlcalde)
        alcaldeNombre.value = estado.data.nombreAlcalde
      }
    }
  } catch (error) {
    /* error al cargar estado */
  }

  try {
    const sesion = await axiosInstance.get(`/partida/${codigoSala.value}/sesion-activa`)
    if (sesion.data?.abierta) {
      votacionActiva.value = true
      tipoVotacionLocal.value = sesion.data.tipo
      store.dispatch('sala/setTipoVotacion', sesion.data.tipo)
    }
  } catch {
    /* sin sesión activa */
  }

  conectarWebSocket()
}

const conectarWebSocket = () => {
  const token = store.getters['auth/token']
  const cliente = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    connectHeaders: { Authorization: `Bearer ${token}` },
  })

  cliente.onConnect = () => {
    cliente.subscribe(`/topic/partida/${codigoSala.value}/fase`, (msg) => {
      const payload = JSON.parse(msg.body)
      esDia.value = payload.fase === 'DIA'
      store.dispatch('sala/setFase', payload.fase)
      store.dispatch('toast/mostrar', {
        mensaje:
          payload.fase === 'DIA'
            ? 'La aldea despierta ¡Buenos días!'
            : 'Cae la noche sobre Castronegro',
        tipo: payload.fase === 'DIA' ? 'dia' : 'noche',
      })
      if (payload.fase === 'DIA') {
        store.dispatch('sala/reiniciarVotos')
        store.dispatch('sala/setTipoVotacion', null)
        tipoVotacionLocal.value = null
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/muerte`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (
        payload.tipo === 'CONFIRMAR' ||
        payload.tipo === 'LINCHAMIENTO' ||
        payload.tipo === 'RENDIRSE'
      ) {
        store.dispatch('sala/marcarMuerto', payload.nombreJugador)
      } else if (payload.tipo === 'REVIVIR') {
        store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)
      } else {
        store.dispatch('sala/marcarSemimuerto', payload.nombreJugador)
      }

      if (
        miRol.value &&
        miRol.value.toLowerCase() === 'niño salvaje' &&
        store.getters['sala/mentorNinno'] === payload.nombreJugador
      ) {
        store.commit('sala/SET_ROL', {
          nombreRol: 'NIÑO LOBO',
          descripcionRol: 'Te has transformado en un Hombre Lobo',
          bando: 'lobo',
          nombreJugador: nombre.value,
        })

        axiosInstance
          .get(`/salas/${codigoSala.value}/mi-rol`)
          .then((miIdRes) => {
            return axiosInstance.put(
              `/partida/${codigoSala.value}/jugador/${miIdRes.data.idUsuario}/rol`,
              {
                nombreRol: 'NIÑO LOBO',
              },
            )
          })
          .then(() => {})
          .catch(() => {})

        mensajeEvento.value = '¡Tu mentor ha muerto! ¡Te has convertido en un Hombre Lobo!'
        setTimeout(() => {
          mensajeEvento.value = null
        }, 8000)
      }

      if (payload.nombreJugador === nombre.value && payload.muerteConfirmada) {
        if (store.getters['sala/bandoGanador'] && router.currentRoute.value.name !== 'resultados') {
          router.push({ name: 'resultados' })
          return
        }
        if ((miRol.value || '').toLowerCase() === 'cazador') {
          soyElCazadorMuerto.value = true
          mensajeEvento.value =
            '¡Has sido eliminado! El Narrador activará tu poder de Cazador en breve...'
          return
        }
        if (store.getters['sala/bandoGanador']) {
          router.push({ name: 'resultados' })
          return
        }
        setTimeout(() => {
          if (router.currentRoute.value.name !== 'resultados') {
            router.push({ name: 'eliminado' })
          }
        }, 500)
        return
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votos`, (msg) => {
      const payload = JSON.parse(msg.body)
      const tipoVotacionActual = store.getters['sala/tipoVotacion']
      if (tipoVotacionActual === 'LOBOS') {
        if (miRol.value && (miRol.value.toLowerCase() === 'lobo' || miRol.value === 'NIÑO LOBO')) {
          store.dispatch('sala/actualizarVotos', payload.votos)
        }
        return
      }
      store.dispatch('sala/actualizarVotos', payload.votos)
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/alcalde`, (msg) => {
      const payload = JSON.parse(msg.body)
      if (payload.tipo === 'ALCALDE_ELEGIDO') {
        store.dispatch('sala/designarAlcalde', payload.nombreAlcalde)
        alcaldeNombre.value = payload.nombreAlcalde
        store.dispatch('sala/reiniciarVotos')
        if (payload.nombreAlcalde) {
          store.dispatch('toast/mostrar', {
            mensaje: `¡${payload.nombreAlcalde} ha sido elegido alcalde!`,
            tipo: 'alcaldia',
          })
        }
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votacion`, (msg) => {
      const payload = JSON.parse(msg.body)

      if (payload.tipo === 'VOTACION_ABIERTA' || payload.tipo === 'VOTACION_CERRADA') {
        votacionActiva.value = payload.abierta

        if (!payload.abierta) {
          store.dispatch('sala/reiniciarVotos')
          store.dispatch('sala/setTipoVotacion', null)
          return
        }

        if (payload.abierta) {
          tipoVotacionLocal.value = payload.tipoVotacion
          store.dispatch('sala/setTipoVotacion', payload.tipoVotacion)

          if (payload.tipoVotacion === 'ALCALDE') {
            mensajeEvento.value = 'ELECCIONES ABIERTAS'
            store.dispatch('toast/mostrar', {
              mensaje: 'Se han abierto las elecciones de alcalde',
              tipo: 'info',
            })
          } else if (payload.tipoVotacion === 'DIA') {
            mensajeEvento.value = 'VOTACIÓN DE LINCHAMIENTO'
            store.dispatch('toast/mostrar', {
              mensaje: 'Votación de linchamiento en curso',
              tipo: 'info',
            })
          } else if (payload.tipoVotacion === 'LOBOS') {
            mensajeEvento.value = 'LOS LOBOS DECIDEN'
          } else if (payload.tipoVotacion === 'HABILIDAD') {
            mensajeEvento.value = 'TURNO DE HABILIDAD'
          }

          setTimeout(() => {
            mensajeEvento.value = null
          }, 30000)
        } else {
          tipoVotacionLocal.value = null
          store.dispatch('sala/setTipoVotacion', null)
        }
        return
      }

      if (payload.tipo === 'LOBOS' && payload.nombreEliminado) {
        store.dispatch('sala/marcarSemimuerto', payload.nombreEliminado)
        mensajeEvento.value = `Los lobos han devorado a ${payload.nombreEliminado} esta noche...`
        setTimeout(() => {
          mensajeEvento.value = null
        }, 8000)
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/turno`, (msg) => {
      const payload = JSON.parse(msg.body)

      if (payload.tipo === 'EVENTOS_INICIADOS') {
        mensajeEvento.value =
          '¡Llegó la noche! Presta atención, puede que el narrador te llame para que utilices tus poderes'
        setTimeout(() => {
          mensajeEvento.value = null
        }, 10000)
        return
      }

      if (payload.tipo === 'EVENTOS_FINALIZADOS') {
        mensajeEvento.value = null
        esMiTurno.value = false
        jugadorSeleccionado.value = null
        return
      }

      if (payload.tipo === 'TURNO_FINALIZADO') return

      if (payload.tipo === 'FLECHAZO') {
        const soyEnamorado = payload.jugador1 === nombre.value || payload.jugador2 === nombre.value
        if (soyEnamorado) {
          const nombrePareja =
            payload.jugador1 === nombre.value ? payload.jugador2 : payload.jugador1
          store.dispatch('sala/setEnamorados', {
            jugador1: payload.jugador1,
            jugador2: payload.jugador2,
          })
          mensajeEvento.value = `¡Estás enamorado de ${nombrePareja}!`
          setTimeout(() => {
            mensajeEvento.value = null
          }, 8000)
        }
        return
      }

      if (payload.tipo === 'ROL_CAMBIADO') {
        store.commit('sala/UPDATE_JUGADOR_ROL', {
          nombreJugador: payload.nombreJugador,
          nombreRol: payload.nombreRol,
          bando: payload.bando,
        })
        return
      }

      if (payload.tipo === 'TURNO_LOBOS') {
        const esLobo = miRol.value && (miRol.value.toLowerCase() === 'lobo' || miRol.value === 'NIÑO LOBO')
        const soyLobo = esLobo || payload.nombresLobos?.includes(nombre.value)
        esMiTurno.value = soyLobo

        store.dispatch('toast/mostrar', {
          mensaje: 'Los lobos salen de cazería...',
          tipo: 'licantropia',
        })
        if (soyLobo) {
          mensajeEvento.value =
            '¡Es hora de cazar! Decide junto a los tuyos a quién devoráis esta noche'
          setTimeout(() => {
            mensajeEvento.value = null
          }, 30000)
        }
        return
      }

      if (payload.tipo === 'TURNO_JUGADOR') {
        const faseDelPayload = payload.fase
        if (faseDelPayload) {
          esDia.value = faseDelPayload === 'DIA'
          store.dispatch('sala/setFase', faseDelPayload)
        } else {
          const faseStore = store.getters['sala/fase']
          if (faseStore) {
            esDia.value = faseStore === 'DIA'
          }
        }
        esMiTurno.value = payload.nombreJugador === nombre.value

        const toastsPorRol = {
          Vidente: { mensaje: 'La Vidente está teniendo una visión...', tipo: 'videncia' },
          Bruja: { mensaje: 'La Bruja prepara sus pociones...', tipo: 'brujeria' },
          Cupido: { mensaje: 'Cupido está lanzando sus flechas de amor...', tipo: 'amorio' },
        }
        const toastRol = toastsPorRol[payload.rolActivo]

        if (toastRol) {
          store.dispatch('toast/mostrar', toastRol)
        }

        if (esMiTurno.value) {
          mensajeEvento.value = `Es tu turno, ${nombre.value}. Activa tu poder.`
          setTimeout(() => {
            mensajeEvento.value = null
          }, 30000)
        } else {
          esMiTurno.value = false
        }
        return
      }

      if (payload.tipo === 'MENTOR_ELEGIDO') {
        if (payload.nombreNinno === nombre.value) return
        store.dispatch('sala/setMentorNinno', payload.nombreMentor)
        return
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/fin`, (msg) => {
      const payload = JSON.parse(msg.body)
      store.dispatch('sala/setResultado', {
        bandoGanador: payload.bandoGanador,
        mensaje: payload.mensaje,
      })
      if (router.currentRoute.value.name !== 'resultados') {
        router.push({ name: 'resultados' })
      }
    })
  }

  cliente.activate()
  stompClient.value = cliente
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
.contenedor-jugador {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.dia {
  background-image: url('@/assets/imgs/fondodia.webp');
}
.noche {
  background-image: url('@/assets/imgs/fondonoche.webp');
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
  border-color: var(--color-dorado) !important;
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
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.95rem;
  animation: fade-in-up 0.4s ease;
}

.evento-dia {
  background: rgba(0, 0, 0, 0.6);
  border: 2px solid var(--color-dorado);
  color: var(--color-dorado);
}

.evento-noche {
  background: rgba(0, 0, 0, 0.7);
  border: 2px solid var(--color-rojo);
  color: var(--color-rojo);
}

.cuadro-turno {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 20px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.85);
  border: 2px solid var(--color-dorado);
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.95rem;
  animation: fade-in-up 0.4s ease;
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
  border: 2px solid var(--color-dorado);
  background: transparent;
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-ir-poderes:hover {
  background: var(--color-dorado);
  color: var(--color-black);
}

.footer-aldea {
  width: 100%;
  height: 900px;
  background-size: cover;
  background-position: bottom;
  background-repeat: no-repeat;
}

.footer-dia {
  background-image: url('@/assets/imgs/footer-dia.webp');
}
.footer-noche {
  background-image: url('@/assets/imgs/footer-noche.webp');
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

.boton-arriba-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 12px;
  margin-top: 75px;
  position: relative;
  flex-wrap: wrap;
}

.boton-arriba {
  background: #000000;
  border: 3px solid white;
  color: white;
  padding: 16px 36px;
  border-radius: 10px;
  font-family: var(--font-cinzel);
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
}

.boton-arriba:hover {
  background: #a30000;
}

.boton-arriba:active {
  transform: scale(0.96);
}

.boton-rendirse {
  background: #8b0000;
  border: 3px solid #ffffff;
  color: white;
  padding: 16px 36px;
  border-radius: 10px;
  font-family: var(--font-cinzel);
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
}

.boton-rendirse:hover {
  background: #000000;
  border-color: #8b0000;
}

.boton-rendirse:active {
  transform: scale(0.96);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9998;
}

.modal-rendirse {
  background: #111;
  border: 3px solid #cc0000;
  border-radius: 15px;
  padding: 36px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  max-width: 420px;
  width: 90%;
}

.modal-pregunta {
  font-family: var(--font-raleway);
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
  text-align: center;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.modal-pregunta i {
  color: #cc0000;
}

.modal-subtitulo {
  font-family: var(--font-raleway);
  font-size: 0.95rem;
  color: #aaa;
  text-align: center;
  margin: 0;
  font-style: italic;
}

.modal-botones {
  display: flex;
  gap: 12px;
  margin-top: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.btn-modal-rendirse {
  padding: 12px 28px;
  border-radius: 10px;
  border: 2px solid #cc0000;
  background: #cc0000;
  color: white;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

.btn-modal-rendirse:hover {
  background: transparent;
  color: #cc0000;
}

.btn-modal-cancelar {
  padding: 12px 28px;
  border-radius: 10px;
  border: 2px solid #111;
  background: #000;
  color: white;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

.btn-modal-cancelar:hover {
  background: white;
  color: #000;
  border-color: white;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
