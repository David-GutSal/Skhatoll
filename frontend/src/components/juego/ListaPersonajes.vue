<template>
  <div class="listas-personajes py-5">
    <div class="container">
      <div class="panel-listas bg-black rounded-4 p-4 shadow-lg">
        <h2 class="listas-titulo text-center mb-5">LISTAS DE PERSONAJES</h2>

        <div
          v-for="lista in listas"
          :key="lista.bando"
          :id="'lista-' + lista.bando"
          class="lista-grupo mb-3"
        >
          <button
            class="lista-header d-flex justify-content-between align-items-center w-100 border-0 bg-transparent text-start py-3 px-4"
            :style="{ color: lista.color }"
            @click="toggleLista(lista.bando)"
          >
            <span class="lista-nombre fs-4 fw-bold">{{ lista.nombre }}</span>
            <i 
              :class="lista.abierta ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'" 
              class="lista-flecha fs-4"
            ></i>
          </button>

          <hr :style="{ borderColor: lista.color, opacity: 0.7 }" class="my-0 mx-4">

          <transition name="desplegable">
            <div v-if="lista.abierta" class="lista-contenido pt-4 px-4">

              <TarjetaPersonaje
                v-for="personaje in lista.personajes"
                :key="personaje.nombre"
                :personaje="personaje"
                :color="lista.color"
                :bando="lista.nombre"
              />
            </div>
          </transition>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import { listas as listasOriginales } from '@/data/personajesinfo'
import TarjetaPersonaje from '@/components/layout/TarjetaPersonaje.vue'

export default {
  name: 'ListaPersonajes',

  components: {
    TarjetaPersonaje
  },

  data() {
    return {
      listas: listasOriginales.map(lista => ({
        ...lista,
        abierta: false
      }))
    }
  },

  methods: {
    toggleLista(bando) {
      const lista = this.listas.find(l => l.bando === bando)
      if (lista) lista.abierta = !lista.abierta
    },
    
   abrirBando(bando) {
      const lista = this.listas.find(l => l.bando === bando)
      if (lista) lista.abierta = true
    } 
  }
}
</script>

<style scoped>
.panel-listas {
  background: #252525 !important;
  border-radius: 20px;
  padding: 30px 20px;
}

.listas-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  color: white;
  font-weight: bolder;
  margin-top: 25px;
}

.lista-header {
  font-family: 'Cinzel', Arial, sans-serif;
}

.lista-nombre {
  font-size: 1.35rem;
  font-weight: 700;
}

.lista-flecha {
  transition: transform 0.3s ease;
}

.lista-header:hover .lista-flecha {
  transform: scale(1.2);
}

.desplegable-enter-active,
.desplegable-leave-active {
  transition: opacity 0.4s ease;
}

.desplegable-enter-from,
.desplegable-leave-to {
  opacity: 0;
}
</style>