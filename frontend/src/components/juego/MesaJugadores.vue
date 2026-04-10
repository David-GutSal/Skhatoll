<template>
  <div class="mesa-wrapper" :class="esDia ? 'mesa-dia' : 'mesa-noche'">

    <div class="mesa">
      <CartaRol
        v-for="j in jugadoresFiltrados"
        :key="j.idUsuario"
        :modoVista="modoNarrador ? 'narrador' : 'jugador'"
        :jugador="j"
        :esDia="esDia"
        @seleccionar="$emit('seleccionarJugador', j)"
      />
    </div>
  </div>
</template>

<script>
import CartaRol from '@/components/juego/roles/CartaRol.vue'
import mesaImg from '@/assets/imgs/mesa.jpg'
import mesanocheImg from '@/assets/imgs/mesanoche.jpg'

export default {
  name: 'MesaJugadores',
  components: { CartaRol },

  props: {
    jugadores: { type: Array, default: () => [] },
    esDia: { type: Boolean, default: true },
    modoNarrador: { type: Boolean, default: false },
  },

  emits: ['seleccionarJugador'],

  data() {
    return { mesaImg, mesanocheImg }
  },

  computed: {
    jugadoresFiltrados() {
      return this.jugadores.filter((j) => !j.esNarrador)
    },

    bgMesa() {
      return this.esDia ? this.mesaImg : this.mesanocheImg
    },
  },
}
</script>

<style scoped>

.mesa-wrapper {
  border-radius: 16px;
  overflow: hidden;
  border: 5px solid black;
  background-size: cover;
  background-position: center;
}

.mesa-dia {
  background-image: v-bind("'url(' + mesaImg + ')'");
  border-color: #000;
}

.mesa-noche {
  background-image: v-bind("'url(' + mesanocheImg + ')'");
  border-color: white;
}

.mesa {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  padding: 24px;
  background: rgba(0, 0, 0, 0.45);
}

.mesa :deep(.carta-mesa) {
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.6);
}

@media (max-width: 900px) {
  .mesa {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 500px) {
  .mesa {
    grid-template-columns: repeat(2, 1fr);
    padding: 14px;
    gap: 10px;
  }
}
</style>
