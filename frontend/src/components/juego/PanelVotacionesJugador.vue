<template>
  <div class="panel-votaciones-wrapper" :class="esDia ? 'wrapper-dia' : 'wrapper-noche'">

    <template v-if="esDia">
      <button
        class="btn-voto"
        :disabled="!votacionActiva || tipoVotacion !== 'ALCALDE' || !jugadorSeleccionado"
        @click="$emit('votarAlcalde')"
      >
        <i class="fa-solid fa-medal"></i>
        Votar Alcalde
      </button>

      <button
        class="btn-voto"
        :disabled="!votacionActiva || tipoVotacion !== 'DIA' || !jugadorSeleccionado"
        @click="$emit('votarCulpable')"
      >
        <i class="fa-solid fa-face-dizzy"></i>
        Votar Linchamiento
      </button>

      <div class="cuadro-fase cuadro-dia">
        <i class="fa-solid fa-sun"></i>
        ¡Es de día! ¡Reúnete con tus vecinos en la plaza del pueblo!
      </div>
    </template>

    <template v-else>
      <div class="cuadro-fase cuadro-noche">
        <i class="fa-solid fa-moon"></i>
        ¡Es de noche! ¡Asegúrate de cerrar bien puertas y ventanas!
      </div>
    </template>

  </div>
</template>

<script>
export default {
  name: 'PanelVotacionesJugador',
  props: {
    esDia: { type: Boolean, default: true },
    votacionActiva: { type: Boolean, default: false },
    tipoVotacion: { type: String, default: null },
    jugadorSeleccionado: { type: Object, default: null },
  },
  emits: ['votarAlcalde', 'votarCulpable'],
}
</script>

<style scoped>
.panel-votaciones-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  padding: 16px 20px;
  border-radius: 15px;
}

.wrapper-dia { background: rgba(0, 0, 0, 0.9); }
.wrapper-noche { background: rgba(0, 0, 0, 0.9); }

.btn-voto {
  display: flex;
  align-items: center;
  justify-content: center;
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
  flex: 1;
  min-width: 160px;
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

.cuadro-fase {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  background: rgba(0, 0, 0, 0.4);
}

.cuadro-dia { border: 2px solid #e4ba03; color: white; }
.cuadro-dia i { color: #e4ba03; }
.cuadro-noche { border: 2px solid #cc0000; color: #cc0000; }
.cuadro-noche i { color: #cc0000; }

@media (max-width: 768px) {
  .panel-votaciones-wrapper {
    flex-direction: column;
    align-items: stretch;
  }
  .btn-voto { width: 100%; }
}
</style>