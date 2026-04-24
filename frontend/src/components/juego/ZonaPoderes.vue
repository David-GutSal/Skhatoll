<template>
  <div class="zona">

    <PoderLobo
      v-if="miRol === 'Lobo' && esMiTurno && !esDia"
      ref="poderLobo"
      :jugadorSeleccionado="jugadorSeleccionado"
      @devorar="$emit('devorar')"
    />

    <div v-if="miRol === 'Vidente' && esMiTurno && !esDia" class="zona-vidente">
      <PoderVidente
        ref="poderVidente"
        :jugadorSeleccionado="jugadorSeleccionado"
        :esDia="esDia"
        @premonicion="$emit('premonicion')"
      />
      <button class="btn-finalizar" @click="finalizarPremonicion">
        <i class="fa-solid fa-eye-slash"></i>
        Finalizar premonición
      </button>
    </div>

  </div>
</template>

<script>
import PoderLobo from './poderes/PoderLobo.vue'
import PoderVidente from './poderes/PoderVidente.vue'

export default {
  name: 'ZonaPoderes',
  components: { PoderLobo, PoderVidente },

  props: {
    miRol: { type: String, default: null },
    jugadorSeleccionado: { type: Object, default: null },
    esMiTurno: { type: Boolean, default: false },
    esDia: { type: Boolean, default: true },
  },

  emits: ['devorar', 'premonicion', 'finalizarTurno'],

  watch: {
    esMiTurno(nuevoValor) {
      if (!nuevoValor) {
        if (this.$refs.poderVidente) this.$refs.poderVidente.resetear()
        if (this.$refs.poderLobo) this.$refs.poderLobo.resetear()
      }
    },
  },

  methods: {
    finalizarPremonicion() {
      if (this.$refs.poderVidente) this.$refs.poderVidente.resetear()
      this.$emit('finalizarTurno')
    },
  },
}
</script>

<style scoped>
.zona {
  display: flex;
  justify-content: center;
}

.zona-vidente {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 500px;
}

.btn-finalizar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  border: 2px solid #e4ba03;
  background: transparent;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.btn-finalizar:hover {
  background: #e4ba03;
  color: #000;
}
</style>