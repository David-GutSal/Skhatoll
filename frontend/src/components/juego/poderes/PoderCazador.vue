<template>
  <div class="poder-cazador">
    <!-- Título -->
    <div class="titulo-caja">
      <h2 class="titulo">¡Has sido eliminado!</h2>
      <p class="subtitulo">¿A quién disparas el tiro de gracia?</p>
    </div>

    <!-- Caja de víctima -->
    <div class="victima-box" :class="jugadorSeleccionado ? 'victima-activa' : 'victima-vacia'">
      <i class="fa-solid fa-person"></i>
      <span v-if="jugadorSeleccionado">{{ jugadorSeleccionado.nombre }}</span>
      <span v-else class="placeholder">Selecciona una víctima en el tablero</span>
    </div>

    <!-- Botón disparar -->
    <button
      class="btn-disparar"
      :class="{ disparado: disparado }"
      :disabled="!jugadorSeleccionado || disparado"
      @click="disparar"
    >
      <i class="fa-solid fa-gun"></i>
      {{ disparado ? 'Disparo efectuado' : 'Disparar' }}
    </button>

    <p v-if="disparado" class="mensaje-disparo">
      <i class="fa-solid fa-skull"></i>
      Tu venganza está hecha. Descansa en paz.
    </p>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { mapGetters } from 'vuex'

export default {
  name: 'PoderCazador',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  emits: ['disparo', 'finalizarTurno'],

  data() {
    return {
      disparado: false,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
  },

  methods: {
    async disparar() {
      if (!this.jugadorSeleccionado || this.disparado) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
          nombreHabilidad: 'disparo',
          objetivos: [this.jugadorSeleccionado.idUsuario],
        })
        this.disparado = true
        this.$emit('disparo', this.jugadorSeleccionado)
        // Dar tiempo a que el jugador vea el resultado antes de cerrar el turno
        setTimeout(() => {
          this.$emit('finalizarTurno')
        }, 2500)
      } catch {
        this.$store.dispatch('toast/mostrar', {
          mensaje: 'Error al usar el disparo',
          tipo: 'error',
        })
      }
    },

    resetear() {
      this.disparado = false
    },
  },
}
</script>

<style scoped>
.poder-cazador {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  max-width: 500px;
}

/* ── Título ── */
.titulo-caja {
  background: #000;
  border-radius: 15px;
  padding: 35px;
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 1.8rem;
  color: white;
  margin: 0;
  letter-spacing: 0.05em;
}

.subtitulo {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  color: #2980b9;
  margin: 0;
}

/* ── Caja de víctima ── */
.victima-box {
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

.victima-vacia {
  background: rgba(255, 255, 255, 0.05);
  border: 2px dashed #555;
  color: #555;
}

.victima-activa {
  background: rgba(41, 128, 185, 0.15);
  border: 2px solid #2980b9;
  color: white;
  box-shadow:
    0 0 8px rgba(41, 128, 185, 0.4),
    0 0 20px rgba(41, 128, 185, 0.15);
}

.victima-activa i {
  color: #2980b9;
}

.placeholder {
  font-style: italic;
  font-weight: 400;
}

/* ── Botón disparar ── */
.btn-disparar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 32px;
  border-radius: 10px;
  border: 3px solid #2980b9;
  background: #2980b9;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition:
    background 0.25s ease,
    border-color 0.25s ease,
    transform 0.25s ease;
  width: 100%;
}

.btn-disparar:hover:not(:disabled):not(.disparado) {
  background: #1a5f8a;
  border-color: #1a5f8a;
}

/* Estado tras disparar: rojo y más pequeño */
.btn-disparar.disparado {
  background: #cc0000;
  border-color: #cc0000;
  transform: scale(0.9);
  cursor: not-allowed;
}

.btn-disparar:disabled:not(.disparado) {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ── Mensaje post-disparo ── */
.mensaje-disparo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  font-style: italic;
  color: #888;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  animation: aparecer 0.5s ease;
}

@keyframes aparecer {
  from {
    opacity: 0;
    transform: translateY(-6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
