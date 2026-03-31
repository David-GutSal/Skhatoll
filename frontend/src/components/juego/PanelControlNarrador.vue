<template>
  <div class="panel-control" :class="esDia ? 'dia' : 'noche'">

    <template v-if="esDia">
      <button
        class="btn-panel"
        :disabled="hayAlcalde"
        @click="$emit('votarAlcalde')"
      >
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
    </template>

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

<script>
export default {
  name: 'PanelControlNarrador',
  props: {
    esDia: { type: Boolean, default: true },
    hayAlcalde: { type: Boolean, default: false },
  },
  emits: ['votarAlcalde', 'votarLinchamiento', 'finalizarVotacion', 'eventos', 'verPersonajes', 'verReglas'],

  data() {
    return {
      eventosActivos: false,
    }
  },

  methods: {
    toggleEventos() {
      this.eventosActivos = !this.eventosActivos
      this.$emit('eventos')
    },
  },
}
</script>

<style scoped>

.panel-control {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 16px 20px;
  margin-bottom: 20px;
}

.btn-panel {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 10px;
  border: 3px solid #e4ba03;
  background: white;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: transform 0.15s ease, background 0.2s ease, color 0.2s ease;
}

.btn-panel:hover {
  background: #e4ba03;
  color: #000;
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

.noche .btn-eventos {
  background: #cc0000;
  color: #000;
  border-color: white;
}

.noche .btn-eventos:hover {
  background: white;
  color: #cc0000;
}

.btns-info {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.btn-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 10px;
  border: 3px solid #e4ba03;
  background: transparent;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: transform 0.15s ease, background 0.2s ease, color 0.2s ease;
}

.btn-info:hover {
  background: #000;
  transform: scale(0.96);
}

.noche .btn-info {
  border-color: #cc0000;
  color: #cc0000;
}

.noche .btn-info:hover {
  background: white;
  color: #cc0000;
}

@media (max-width: 768px) {
  .panel-control {
    flex-direction: column;
    align-items: stretch;
  }
  .btns-info {
    margin-left: 0;
    justify-content: center;
  }
  .btn-panel, .btn-info {
    justify-content: center;
  }
}
</style>