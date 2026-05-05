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
        @mentorElegido="manejarMentorElegido"
      />
    </div>

    <div class="boton-arriba-wrapper">
      <button class="boton-arriba" @click="irArriba">
        <i class="fa-solid fa-arrow-up"></i> Volver Arriba
      </button>
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
import CabeceraJugador from '@/components/juego/CabeceraJugador.vue'
import PanelVotacionesJugador from '@/components/juego/PanelVotacionesJugador.vue'
import MesaJugadores from '@/components/juego/MesaJugadores.vue'
import BotonMiRol from '@/components/juego/BotonMiRol.vue'
import ZonaPoderes from '@/components/juego/ZonaPoderes.vue'

const router = useRouter()
const store = useStore()
const zonaPoderes = ref(null)

const esDia = ref(true)
const votacionActiva = ref(false)
const tipoVotacionLocal = ref(null)
const esMiTurno = ref(false)
const stompClient = ref(null)
const mensajeEvento = ref(null)
const alcaldeNombre = ref(null)
const jugadorEnvenenado = ref(null)
const soyElCazadorMuerto = ref(false)
const jugadorSeleccionado = ref(null)

const nombre = computed(() => store.getters['auth/nombre'])
const codigoSala = computed(() => store.getters['sala/codigoSala'])
const jugadores = computed(() => store.getters['sala/jugadores'])
const miRol = computed(() => store.getters['sala/miRol'])
const enamorados = computed(() => store.getters['sala/enamorados'])

const nombreNarrador = computed(() => {
  const narrador = jugadores.value.find((j) => j.esNarrador === true)
  return narrador ? narrador.nombre : 'Esperando narrador...'
})

const soyNarrador = computed(() => {
  return jugadores.value.some((j) => j.esNarrador === true && j.nombre === nombre.value)
})

const jugadoresVisibles = computed(() => {
  if (!esDia.value && miRol.value && miRol.value.toLowerCase() === 'lobo') {
    return jugadores.value.filter((j) => j.nombre !== nombre.value)
  }
  return jugadores.value
})

const scrollAPoderes = () => {
  zonaPoderes.value?.$el?.scrollIntoView({ behavior: 'smooth' })
}

const irArriba = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

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
  if (!votacionActiva.value && !esMiTurno.value) return
  jugadorSeleccionado.value = j
}

const votarAlcalde = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al votar', tipo: 'error' })
  }
}

const votarCulpable = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al votar', tipo: 'error' })
  }
}

const devorarJugador = async () => {
  if (!jugadorSeleccionado.value) return
  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/votar`, {
      idObjetivo: jugadorSeleccionado.value.idUsuario,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al devorar', tipo: 'error' })
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
      router.push({ name: 'eliminado' })
    }, 1500)
  }
}

const manejarDisparo = (jugador) => {
  console.log('🔫 Cazador disparó a:', jugador.nombre)
}

const cargarDatos = async () => {
  try {
    const res = await axiosInstance.get(`/salas/${codigoSala.value}/jugadores`)
    store.dispatch('sala/setJugadores', res.data)
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al cargar jugadores', tipo: 'error' })
  }

  try {
    const sesion = await axiosInstance.get(`/partida/${codigoSala.value}/sesion-activa`)
    if (sesion.data?.abierta) {
      votacionActiva.value = true
      tipoVotacionLocal.value = sesion.data.tipo
    }
  } catch {
    // No hay sesión activa, es normal
  }

  conectarWebSocket()
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
      store.dispatch('sala/marcarMuerto', payload.nombreJugador)
      store.dispatch('sala/quitarSemimuerto', payload.nombreJugador)

      if (
        miRol.value &&
        miRol.value.toLowerCase() === 'niño salvaje' &&
        store.getters['sala/mentorNinno'] === payload.nombreJugador
      ) {
        store.commit('sala/SET_ROL', {
          nombreRol: 'Lobo',
          descripcionRol: 'Te has transformado en un Hombre Lobo',
          bando: 'lobo',
        })
        mensajeEvento.value = '¡Tu mentor ha muerto! ¡Te has convertido en un Hombre Lobo!'
        setTimeout(() => {
          mensajeEvento.value = null
        }, 8000)
      }

      if (payload.nombreJugador === nombre.value) {
        if ((miRol.value || '').toLowerCase() === 'cazador') {
          soyElCazadorMuerto.value = true
          mensajeEvento.value =
            '¡Has sido eliminado! El Narrador activará tu poder de Cazador en breve...'
          return
        }
        router.push({ name: 'eliminado' })
        return
      }

      const enamorados = store.getters['sala/enamorados']
      if (enamorados) {
        const { jugador1, jugador2 } = enamorados
        if (payload.nombreJugador === jugador1 || payload.nombreJugador === jugador2) {
          const nombrePareja = payload.nombreJugador === jugador1 ? jugador2 : jugador1
          const jugadorPareja = jugadores.value.find((j) => j.nombre === nombrePareja)
          if (jugadorPareja && jugadorPareja.estaVivo) {
            stompClient.value.publish({
              destination: `/topic/partida/${codigoSala.value}/turno`,
              body: JSON.stringify({
                tipo: 'MUERTE_ENAMORADO',
                nombreJugador: nombrePareja,
              }),
            })
          }
        }
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votos`, (msg) => {
      const payload = JSON.parse(msg.body)
      const tipoVotacionActual = store.getters['sala/tipoVotacion']
      if (tipoVotacionActual === 'LOBOS') {
        if (miRol.value && miRol.value.toLowerCase() === 'lobo') {
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
        store.dispatch('toast/mostrar', {
          mensaje: `¡${payload.nombreAlcalde} ha sido elegido alcalde!`,
          tipo: 'aviso',
        })
      }
    })

    cliente.subscribe(`/topic/partida/${codigoSala.value}/votacion`, (msg) => {
      const payload = JSON.parse(msg.body)

      if (payload.tipo === 'VOTACION_ABIERTA' || payload.tipo === 'VOTACION_CERRADA') {
        votacionActiva.value = payload.abierta

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
      if (payload.tipo === 'MUERTE_ENAMORADO') return

      if (payload.tipo === 'FLECHAZO') {
        const soyEnamorado =
          payload.jugador1 === nombre.value || payload.jugador2 === nombre.value
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

      if (payload.tipo === 'TURNO_LOBOS') {
        const soyLobo = payload.nombresLobos?.includes(nombre.value)
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
        const mentor = jugadores.value.find((j) => j.nombre === payload.nombreMentor)
        if (mentor) mentor.esMentor = true
        return
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
</style>
