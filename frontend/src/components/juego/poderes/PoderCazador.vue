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
export default {
  name: 'PoderCazador',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  emits: ['disparo'],

  data() {
    return {
      disparado: false,
    }
  },

  methods: {
    disparar() {
      if (!this.jugadorSeleccionado || this.disparado) return
      this.disparado = true
      this.$emit('disparo', this.jugadorSeleccionado)
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
  font-family: var(--font-cinzel);
  font-weight: 700;
  font-size: 1.8rem;
  color: white;
  margin: 0;
  letter-spacing: 0.05em;
}

.subtitulo {
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 1rem;
  color: #a4b322;
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
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 1rem;
  transition: var(--transition-fast);
}

.victima-vacia {
  background: rgba(255, 255, 255, 0.05);
  border: 2px dashed #555;
  color: #555;
}

.victima-activa {
  background: rgba(41, 128, 185, 0.15);
  border: 2px solid #a4b322;
  color: white;
  box-shadow:
    0 0 8px rgba(41, 128, 185, 0.4),
    0 0 20px rgba(41, 128, 185, 0.15);
}

.victima-activa i {
  color: #a4b322;
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
  border: 3px solid #a4b322;
  background: #1e5f00;
  color: white;
  font-family: var(--font-raleway);
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
  background: #3ab600;
}

/* Estado tras disparar: rojo y más pequeño */
.btn-disparar.disparado {
  background: var(--color-rojo);
  border-color: var(--color-rojo);
  transform: scale(0.9);
  cursor: not-allowed;
}

.btn-disparar:disabled:not(.disparado) {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ── Mensaje post-disparo ── */
.mensaje-disparo {
  font-family: var(--font-raleway);
  font-size: 1.2rem;
  font-style: italic;
  color: #ffffff;
  background-color: #1e5f00;
  padding: 7px;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  animation: fade-in-up 0.4s ease;
}


</style>
