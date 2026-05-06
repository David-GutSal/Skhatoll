<template>
  <div class="zona">
    <!-- LOBO -->
    <PoderLobo
      v-if="esRol('lobo') && esMiTurno && !esDia"
      ref="poderLobo"
      :jugadorSeleccionado="jugadorSeleccionado"
      @devorar="$emit('devorar')"
    />

    <!-- VIDENTE -->
    <div v-if="esRol('vidente') && esMiTurno && !esDia" class="zona-vidente">
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

    <!-- CUPIDO -->
    <PoderCupido
      v-if="esRol('cupido') && esMiTurno && !esDia"
      ref="poderCupido"
      :jugadorSeleccionado="jugadorSeleccionado"
      @flechazo="(pareja) => $emit('flechazo', pareja)"
      @finalizarTurno="$emit('finalizarTurno')"
    />

    <!-- BRUJA -->
    <PoderBruja
      v-if="esRol('bruja') && esMiTurno && !esDia"
      ref="poderBruja"
      :jugadorSeleccionado="jugadorSeleccionado"
      @finalizarTurno="$emit('finalizarTurno')"
      @envenenar="(j) => $emit('envenenar', j)"
      @vidaUsada="(nombre) => $emit('vidaUsada', nombre)"
    />

    <!-- CAZADOR — sin restricción de fase: puede usarlo de día o de noche al morir -->
    <PoderCazador
      v-if="esRol('cazador') && esMiTurno"
      ref="poderCazador"
      :jugadorSeleccionado="jugadorSeleccionado"
      @disparo="(j) => $emit('disparo', j)"
      @finalizarTurno="$emit('finalizarTurno')"
    />

    <!-- NIÑO SALVAJE -->
    <PoderNinno
      v-if="esRol('niño salvaje') && esMiTurno && !esDia"
      ref="poderNinno"
      :jugadorSeleccionado="jugadorSeleccionado"
      @mentorElegido="(data) => $emit('mentorElegido', data)"
      @finalizarTurno="$emit('finalizarTurno')"
    />

    <!-- NIÑA -->
    <PoderNinna
      v-if="esRol('niña') && esMiTurno && !esDia"
      ref="poderNinna"
      @finalizarTurno="$emit('finalizarTurno')"
    />
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from 'vue'
import PoderLobo from './poderes/PoderLobo.vue'
import PoderVidente from './poderes/PoderVidente.vue'
import PoderCupido from './poderes/PoderCupido.vue'
import PoderBruja from './poderes/PoderBruja.vue'
import PoderCazador from './poderes/PoderCazador.vue'
import PoderNinno from './poderes/PoderNinno.vue'
import PoderNinna from './poderes/PoderNinna.vue'

const props = defineProps({
  miRol: { type: String, default: null },
  jugadorSeleccionado: { type: Object, default: null },
  esMiTurno: { type: Boolean, default: false },
  esDia: { type: Boolean, default: true },
})

const emit = defineEmits([
  'devorar',
  'premonicion',
  'flechazo',
  'finalizarTurno',
  'envenenar',
  'vidaUsada',
  'disparo',
  'mentorElegido',
])

const poderVidente = ref(null)
const poderLobo = ref(null)
const poderCupido = ref(null)
const poderBruja = ref(null)
const poderCazador = ref(null)
const poderNinno = ref(null)
const poderNinna = ref(null)

watch(() => props.esMiTurno, (nuevo) => {
  if (!nuevo) {
    poderVidente.value?.resetear()
    poderLobo.value?.resetear()
    poderCupido.value?.resetear()
    poderBruja.value?.resetear()
    poderCazador.value?.resetear()
    poderNinno.value?.resetear()
    poderNinna.value?.resetear()
  }
})

watch(() => props.esDia, (nuevoValor) => {
  if (!nuevoValor) {
    poderVidente.value?.resetear()
    poderNinna.value?.resetearNoche()
  }
})

const esRol = (rol) => {
  return (props.miRol || '').toLowerCase() === rol.toLowerCase()
}

const finalizarPremonicion = () => {
  poderVidente.value?.resetear()
  emit('finalizarTurno')
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
  border: 2px solid var(--color-dorado);
  background: transparent;
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-finalizar:hover {
  background: var(--color-dorado);
  color: #000;
}
</style>
