<template>
  <div class="panel-control" :class="esDia ? 'dia' : 'noche'">
    <div class="panel-principal">
      <template v-if="esDia">
        <button class="btn-panel" :disabled="hayAlcalde" @click="$emit('votarAlcalde')">
          <i class="fa-solid fa-medal"></i>
          Hacer Elecciones
        </button>

        <button class="btn-panel" @click="$emit('votarLinchamiento')">
          <i class="fa-solid fa-face-dizzy"></i>
          Provocar Linchamiento
        </button>

        <button class="btn-panel" @click="$emit('finalizarVotacion')">
          <i class="fa-solid fa-calendar-check"></i>
          Finalizar Votación
        </button>
      </template>

      <template v-else>
        <button class="btn-panel btn-eventos" @click="toggleEventos">
          <i :class="eventosActivos ? 'fa-solid fa-stop' : 'fa-solid fa-play'"></i>
          {{ eventosActivos ? 'Finalizar Eventos' : 'Iniciar Eventos' }}
        </button>

        <button
          v-if="eventosActivos && sesionActualTipo !== 'LOBOS'"
          class="btn-panel btn-lobos"
          @click="$emit('iniciarVotacionLobos')"
        >
          <i class="fa-solid fa-paw"></i>
          Iniciar Votación Lobos
        </button>

        <button
          v-if="eventosActivos && sesionActualTipo === 'LOBOS'"
          class="btn-panel btn-finalizar-lobos"
          @click="$emit('finalizarVotacion')"
        >
          <i class="fa-solid fa-calendar-check"></i>
          Finalizar Votación Lobos
        </button>

        <div v-if="eventosActivos" class="aviso-seleccion">
          <i class="fa-solid fa-hand-pointer"></i>
          Selecciona un jugador para activar sus poderes
        </div>
      </template>
    </div>

    <div class="btns-info">
      <button class="btn-info" @click="$emit('verPersonajes')">
        <i class="fa-solid fa-users"></i>
        Personajes
      </button>
      <button class="btn-info" @click="$emit('verReglas')">
        <i class="fa-solid fa-scroll"></i>
        Reglas
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  esDia: { type: Boolean, default: true },
  hayAlcalde: { type: Boolean, default: false },
  sesionActiva: { type: Boolean, default: false },
  sesionActualTipo: { type: String, default: null },
})

const emit = defineEmits([
  'votarAlcalde',
  'votarLinchamiento',
  'finalizarVotacion',
  'eventos',
  'verPersonajes',
  'verReglas',
  'iniciarVotacionLobos',
])

const eventosActivos = ref(false)

const toggleEventos = () => {
  eventosActivos.value = !eventosActivos.value
  emit('eventos')
}
</script>

<style scoped>
.panel-control {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 15px;
  background: rgba(0, 0, 0, 0.9) !important;
}

.panel-principal {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 10px;
  border: 3px solid var(--color-dorado);
  background: white;
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  letter-spacing: 0.04em;
  cursor: pointer;
  flex: 1;
  min-width: 160px;
  transition:
    transform 0.15s ease,
    background 0.2s ease,
    color 0.2s ease;
}

.btn-panel:hover {
  background: var(--color-dorado);
  color: var(--color-black);
  transform: scale(0.96);
}

.btn-panel:active {
  transform: scale(0.93);
}

.btn-panel:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.aviso-seleccion {
  width: 100%;
  text-align: center;
  color: var(--color-rojo);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  padding: 6px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.noche .btn-eventos {
  background: var(--color-rojo);
  color: #000;
  border-color: white;
}

.noche .btn-eventos:hover {
  background: white;
  color: var(--color-rojo);
}

.noche .btn-finalizar-lobos {
  background: transparent;
  color: var(--color-dorado);
  border-color: var(--color-dorado);
}

.noche .btn-finalizar-lobos:hover {
  background: var(--color-dorado);
  color: #000;
}

.noche .btn-lobos {
  background: var(--color-rojo);
  color: #000;
  border-color: white;
}

.noche .btn-lobos:hover {
  background: white;
  color: var(--color-rojo);
}

.btns-info {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 10px;
  border: 3px solid var(--color-dorado);
  background: transparent;
  color: white;
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    background 0.2s ease,
    color 0.2s ease;
}

.btn-info:hover {
  background: #006199;
  color: var(--color-dorado);
  transform: scale(0.96);
}

.noche .btn-info {
  border-color: var(--color-rojo);
  color: #ffffff;
}

.noche .btn-info:hover {
  background: white;
  color: var(--color-rojo);
}

@media (max-width: 768px) {
  .panel-principal {
    flex-direction: column;
    align-items: stretch;
  }
  .btn-panel {
    width: 100%;
  }
}
</style>
