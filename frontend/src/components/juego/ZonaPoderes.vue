<template>
  <div class="zona">
    <PoderLobo
      v-if="miRol === 'Lobo' && esMiTurno && !esDia"
      :jugadorSeleccionado="jugadorSeleccionado"
      @devorar="$emit('devorar')"
    />

    <PoderVidente
      v-if="miRol === 'Vidente' && esMiTurno && !esDia"
      ref="poderVidente"
      :jugadorSeleccionado="jugadorSeleccionado"
      :esDia="esDia"
      @premonicion="$emit('premonicion')"
    />
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

  emits: ['devorar', 'premonicion'],

  watch: {
    esMiTurno(nuevoValor) {
      // Al terminar el turno reseteamos el poder de la vidente
      if (!nuevoValor && this.$refs.poderVidente) {
        this.$refs.poderVidente.resetear()
      }
    },
  },
}
</script>

<style scoped>
.zona {
  display: flex;
  justify-content: center;
}
</style>