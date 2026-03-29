<template>

  <!-- ── MODO CARGA / MI ROL ── -->
  <div v-if="modoVista === 'carga' || modoVista === 'miRol'"
    class="carta-completa"
    :style="{ borderColor: colorBando }">

    <div class="carta-imagen-wrapper">
      <img :src="imagen" :alt="nombreRol" class="carta-imagen" />
      <div class="carta-bando-badge" :style="{ background: colorBando }">
        {{ bando ? bando.toUpperCase() : '' }}
      </div>
    </div>

    <div class="carta-info">
      <h2 class="carta-nombre">{{ nombreRol }}</h2>
      <p class="carta-descripcion">{{ descripcion }}</p>
    </div>

  </div>

  <!-- ── MODO NARRADOR: ve imagen + nombre + rol de cada jugador ── -->
  <div v-else-if="modoVista === 'narrador'"
    class="carta-mesa carta-narrador"
    :class="{ muerto: !jugador.estaVivo, alcalde: jugador.alcalde }"
    :style="{ borderColor: jugador.rol ? getColorBando(jugador.bando) : '#555' }"
    @click="$emit('seleccionar', jugador)">

    <div class="carta-mesa-imagen">
      <img :src="getImagenRol(jugador.rol)" :alt="jugador.nombre" />
      <span v-if="!jugador.estaVivo" class="overlay-muerto">💀</span>
    </div>

    <div class="carta-mesa-datos">
      <p class="carta-mesa-nombre">{{ jugador.nombre }}</p>
      <p class="carta-mesa-rol" :style="{ color: getColorBando(jugador.bando) }">
        {{ jugador.rol || '???' }}
      </p>
    </div>

    <div class="carta-iconos">
      <span v-if="jugador.alcalde" title="Alcalde">👑</span>
      <span v-if="jugador.votos > 0" title="Votos">🗳 {{ jugador.votos }}</span>
    </div>

  </div>

  <!-- ── MODO JUGADOR: ve nombre pero NO el rol de los demás ── -->
  <div v-else-if="modoVista === 'jugador'"
    class="carta-mesa carta-jugador"
    :class="{ muerto: !jugador.estaVivo, alcalde: jugador.alcalde }"
    @click="$emit('seleccionar', jugador)">

    <div class="carta-mesa-imagen carta-imagen-oculta">
      <i class="fa-solid fa-person"></i>
      <span v-if="!jugador.estaVivo" class="overlay-muerto">💀</span>
    </div>

    <div class="carta-mesa-datos">
      <p class="carta-mesa-nombre">{{ jugador.nombre }}</p>
      <p class="carta-mesa-rol oculto">???</p>
    </div>

    <div class="carta-iconos">
      <span v-if="jugador.alcalde" title="Alcalde">👑</span>
      <span v-if="jugador.votos > 0" title="Votos">🗳 {{ jugador.votos }}</span>
    </div>

  </div>

</template>

<script>
import { getImagenRol, getColorBando } from '@/data/roles.js'

export default {
  name: 'CartaRol',

  props: {
    
    modoVista: {
      type: String,
      default: 'jugador',
    },
    
    nombreRol: {
      type: String,
      default: null,
    },
    descripcion: {
      type: String,
      default: '',
    },
    bando: {
      type: String,
      default: null,
    },
    
    jugador: {
      type: Object,
      default: () => ({}),
    },
  },

  emits: ['seleccionar'],

  computed: {

    imagen() {
      return getImagenRol(this.nombreRol)
    },

    colorBando() {
      return getColorBando(this.bando)
    },
  },

  methods: {
    getImagenRol,
    getColorBando,
  },
}
</script>

<style scoped>

.carta-completa {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #1f1f1f;
  border: 3px solid #8b0000;
  border-radius: 16px;
  overflow: hidden;
  max-width: 340px;
  width: 100%;
  font-family: 'Raleway', Arial, sans-serif;
}

.carta-imagen-wrapper {
  position: relative;
  width: 100%;
}

.carta-imagen {
  width: 100%;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  display: block;
}

.carta-bando-badge {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 6px 12px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: white;
  text-align: center;
}

.carta-info {
  padding: 20px;
  text-align: center;
}

.carta-nombre {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0 0 12px 0;
}

.carta-descripcion {
  font-family: 'Raleway', Arial, sans-serif;
  color: #ccc;
  font-size: 0.9rem;
  line-height: 1.6;
  font-style: italic;
  margin: 0;
}

/* ── MODO MESA (narrador y jugador) ── */
.carta-mesa {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #1f1f1f;
  border: 2px solid #333;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.15s ease, border-color 0.2s ease;
  font-family: 'Raleway', Arial, sans-serif;
  position: relative;
}

.carta-mesa:hover {
  transform: translateY(-4px);
}

.carta-mesa-imagen {
  position: relative;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.carta-mesa-imagen img {
  width: 100%;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  display: block;
  border-radius: 8px;
}

/* Imagen oculta modo jugador */
.carta-imagen-oculta {
  background: #2a2a2a;
  aspect-ratio: 3 / 4;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.carta-imagen-oculta i {
  font-size: 2.5rem;
  color: #444;
}

.overlay-muerto {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  border-radius: 8px;
}

.carta-mesa-datos {
  width: 100%;
  text-align: center;
}

.carta-mesa-nombre {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 0.85rem;
  font-weight: 700;
  color: white;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.carta-mesa-rol {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
  color: #8b0000;
}

.carta-mesa-rol.oculto {
  color: #444;
  letter-spacing: 0.2em;
}

/* Iconos */
.carta-iconos {
  display: flex;
  gap: 6px;
  font-size: 0.85rem;
}

/* Estados */
.muerto {
  opacity: 0.45;
}

.alcalde {
  box-shadow: 0 0 0 2px gold;
}

/* Narrador: borde más visible */
.carta-narrador {
  border-width: 2px;
}
</style>