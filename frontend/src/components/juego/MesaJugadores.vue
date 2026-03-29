<template>
  <div class="mesa" :class="{ noche: !esDia }">
    <CartaRol
      v-for="j in jugadores"
      :key="j.idUsuario"
      :modoVista="modoNarrador ? 'narrador' : 'jugador'"
      :jugador="j"
      :esDia="esDia"
      @seleccionar="$emit('seleccionarJugador', j)"
    />
  </div>
</template>

<script>
import CartaRol from '@/components/juego/roles/CartaRol.vue'

export default {
  name: 'MesaJugadores',

  components: { CartaRol },

  props: {
    jugadores: {
      type: Array,
      default: () => [],
    },
    esDia: {
      type: Boolean,
      default: true,
    },
    // true → narrador (ve roles) | false → jugador (roles ocultos)
    modoNarrador: {
      type: Boolean,
      default: false,
    },
  },

  emits: ['seleccionarJugador'],
}
</script>

<style scoped>
.mesa {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.6);
  border: 3px solid white;
  border-radius: 12px;
}

.mesa.noche {
  border-color: #8b0000;
  background: rgba(0, 0, 0, 0.8);
}

@media (max-width: 900px) {
  .mesa {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 500px) {
  .mesa {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>