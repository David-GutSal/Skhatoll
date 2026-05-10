<template>
  <div class="poder-ninno">
    <div class="titulo-caja">
      <h2 class="titulo">Elige quién quieres que sea tu mentor</h2>
      <p class="subtitulo">Recuerda que si tu mentor muere, te convertirás en un hombre lobo</p>
    </div>

    <div class="mentor-box" :class="jugadorSeleccionado ? 'mentor-activo' : 'mentor-vacio'">
      <i class="fa-solid fa-hands-holding-child"></i>
      <span v-if="jugadorSeleccionado">{{ jugadorSeleccionado.nombre }}</span>
      <span v-else class="mentor-placeholder">Selecciona a tu mentor en el tablero</span>
    </div>

    <button
      class="btn-mentor"
      :class="{ 'btn-usado': mentorElegido }"
      :disabled="!puedeElegir"
      @click="elegirMentor"
    >
      <i class="fa-solid fa-hands-holding-child"></i>
      {{ mentorElegido ? 'Mentor elegido' : 'Mi Mentor' }}
    </button>

    <div v-if="notificacion" class="notificacion-mentor">
      <i class="fa-solid fa-check-circle"></i>
      {{ notificacion }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'

const props = defineProps({
  jugadorSeleccionado: { type: Object, default: null },
})

const emit = defineEmits(['mentorElegido', 'finalizarTurno'])

const store = useStore()

const mentorElegido = ref(false)
const notificacion = ref(null)

const codigoSala = computed(() => store.getters['sala/codigoSala'])
const mentorNinno = computed(() => store.getters['sala/mentorNinno'])
const nombre = computed(() => store.getters['auth/nombre'])

const puedeElegir = computed(() => {
  if (mentorElegido.value) return false
  if (!props.jugadorSeleccionado) return false
  if (props.jugadorSeleccionado.nombre === nombre.value) return false
  if (!props.jugadorSeleccionado.estaVivo) return false
  return true
})

const elegirMentor = async () => {
  if (!puedeElegir.value) return

  try {
    store.dispatch('sala/setMentorNinno', props.jugadorSeleccionado.nombre)
    mentorElegido.value = true

    emit('mentorElegido', {
      nombreNinno: nombre.value,
      nombreMentor: props.jugadorSeleccionado.nombre,
      idMentor: props.jugadorSeleccionado.idUsuario,
    })

    notificacion.value = `Ahora tu mentor es: ${props.jugadorSeleccionado.nombre}`
    setTimeout(() => {
      notificacion.value = null
    }, 5000)

    setTimeout(() => {
      emit('finalizarTurno')
    }, 1500)
  } catch {
    store.dispatch('toast/mostrar', { mensaje: 'Error al elegir mentor', tipo: 'error' })
  }
}

const resetear = () => {
  notificacion.value = null
}
</script>

<style scoped>
.poder-ninno {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #5dade2;
}

.titulo-caja {
  background: #000;
  border-radius: 10px;
  padding: 20px 24px;
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.titulo {
  font-family: var(--font-cinzel);
  font-size: 1.8rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.subtitulo {
  font-family: var(--font-raleway);
  font-size: 1.2rem;
  font-weight: 700;
  color: #5dade2;
  margin: 0;
}

.mentor-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  border-radius: 10px;
  width: 100%;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 1rem;
  transition: var(--transition-fast);
}

.mentor-vacio {
  background: rgba(255, 255, 255, 0.05);
  border: 2px dashed #555;
  color: #555;
}

.mentor-activo {
  background: rgba(93, 173, 226, 0.15);
  border: 2px solid #5dade2;
  color: white;
  box-shadow:
    0 0 8px rgba(93, 173, 226, 0.4),
    0 0 20px rgba(93, 173, 226, 0.2);
}

.mentor-activo i {
  color: #5dade2;
}

.mentor-placeholder {
  font-style: italic;
  font-weight: 400;
}

.btn-mentor {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  border: 3px solid #5dade2;
  background: #5dade2;
  color: white;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease,
    transform 0.2s ease;
}

.btn-mentor:hover:not(:disabled) {
  background: white;
  color: #5dade2;
}

.btn-mentor:active:not(:disabled) {
  transform: scale(0.93);
}

.btn-mentor:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-usado {
  background: #111 !important;
  border-color: #333 !important;
  color: #555 !important;
  transform: scale(0.95);
}

.notificacion-mentor {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px;
  border-radius: 10px;
  background: rgba(93, 173, 226, 0.1);
  border: 2px solid #5dade2;
  color: #5dade2;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  width: 100%;
  animation: fade-in-up 0.4s ease;
}


</style>
