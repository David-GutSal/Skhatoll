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

<script>
import { mapGetters } from 'vuex'
import axiosInstance from '@/plugins/axios'

export default {
  name: 'PoderNinno',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  emits: ['mentorElegido', 'finalizarTurno'],

  data() {
    return {
      mentorElegido: false,
      notificacion: null,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala', 'mentorNinno']),
    ...mapGetters('auth', ['nombre']),

    puedeElegir() {
      if (this.mentorElegido) return false
      if (!this.jugadorSeleccionado) return false
      if (this.jugadorSeleccionado.nombre === this.nombre) return false
      if (!this.jugadorSeleccionado.estaVivo) return false
      return true
    },
  },

  methods: {
    async elegirMentor() {
      if (!this.puedeElegir) return

      try {
        // TODO: reemplazar con endpoint correcto cuando el backend lo implemente
        // await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
        //   nombreHabilidad: 'modelo',
        //   objetivos: [this.jugadorSeleccionado.idUsuario],
        // })

        this.$store.dispatch('sala/setMentorNinno', this.jugadorSeleccionado.nombre)
        this.mentorElegido = true

        // Notificación al narrador y al propio niño
        this.$emit('mentorElegido', {
          nombreNinno: this.nombre,
          nombreMentor: this.jugadorSeleccionado.nombre,
          idMentor: this.jugadorSeleccionado.idUsuario,
        })

        this.notificacion = `Ahora tu mentor es: ${this.jugadorSeleccionado.nombre}`
        setTimeout(() => {
          this.notificacion = null
        }, 5000)

        // Finalizar turno automáticamente tras elegir mentor
        setTimeout(() => {
          this.$emit('finalizarTurno')
        }, 1500)
      } catch {
        this.$store.dispatch('toast/mostrar', { mensaje: 'Error al elegir mentor', tipo: 'error' })
      }
    },

    resetear() {
      // No reseteamos mentorElegido — el poder solo se usa una vez por partida
      this.notificacion = null
    },
  },
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
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.subtitulo {
  font-family: 'Raleway', Arial, sans-serif;
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
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  transition: all 0.2s ease;
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
  font-family: 'Raleway', Arial, sans-serif;
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
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  width: 100%;
  animation: aparecer 0.4s ease;
}

@keyframes aparecer {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
