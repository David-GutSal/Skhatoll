<template>
  <div class="panel-votaciones" v-if="esDia">

    <button
      class="btn-voto"
      :disabled="!votacionActiva || !jugadorSeleccionado"
      @click="$emit('votarAlcalde')"
    >
      <i class="fa-solid fa-medal"></i>
      Votar Alcalde
    </button>

    <button
      class="btn-voto"
      :disabled="!votacionActiva || !jugadorSeleccionado"
      @click="$emit('votarCulpable')"
    >
      <i class="fa-solid fa-face-dizzy"></i>
      Votar Linchamiento
    </button>

    <div class="cuadro-dia">
      <i class="fa-solid fa-sun"></i>
      ¡Es de día! ¡Reúnete con tus vecinos en la plaza del pueblo!
    </div>

    <div v-if="!esDia" class="cuadro-dia cuadro-noche">
      <i class="fa-solid fa-moon"></i>
      ¡Es de noche! ¡Asegúrate de cerrar bien puertas y ventanas!
    </div>
  </div>
</template>

<script>
export default {
  name: 'PanelVotacionesJugador',
  props: {
    esDia: { type: Boolean, default: true },
    votacionActiva: { type: Boolean, default: false },
    jugadorSeleccionado: { type: Object, default: null },
  },
  emits: ['votarAlcalde', 'votarCulpable'],
}
</script>

<style scoped>
.panel-votaciones {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 12px 20px;
}

.btn-voto {
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
  cursor: pointer;
  transition: transform 0.15s ease, background 0.2s ease, color 0.2s ease;
}

.btn-voto:hover:not(:disabled) {
  background: #e4ba03;
  color: #000;
  transform: scale(0.96);
}

.btn-voto:active:not(:disabled) {
  transform: scale(0.93);
}

.btn-voto:disabled {
  opacity: 0.4;
  background: #888;
  border-color: #888;
  color: white;
  cursor: not-allowed;
}

.cuadro-dia {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 10px;
  border: 2px solid #e4ba03;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  background: rgba(0,0,0,0.4);
}

.cuadro-dia i {
  color: #e4ba03;
}

.cuadro-noche {
  border-color: #cc0000;
  color: #cc0000;
}

@media (max-width: 768px) {
  .panel-votaciones {
    flex-direction: column;
    align-items: stretch;
  }
  .btn-voto {
    justify-content: center;
  }
}
</style>