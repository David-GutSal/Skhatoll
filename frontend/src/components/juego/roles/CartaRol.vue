<template>
  <div
    v-if="modoVista === 'carga' || modoVista === 'miRol'"
    class="carta-completa"
    :style="{ borderColor: colorBando }"
  >
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

  <!-- ── MODO NARRADOR ── -->
  <div
    v-else-if="modoVista === 'narrador'"
    class="carta-mesa carta-narrador"
    :class="{
      muerto: !jugador.estaVivo,
      alcalde: jugador.alcalde,
      'turno-activo': modoEventos && jugadorSeleccionado?.idUsuario === jugador.idUsuario,
    }"
    :style="{ borderColor: getColorBando(rolJugador) }"
    @click="$emit('seleccionar', jugador)"
  >
    <div class="carta-mesa-imagen">
      <img :src="getImagenRol(nombreRolJugador)" :alt="jugador.nombre" />
      <span v-if="jugador.muerteConfirmada" class="overlay-muerto">
        <i class="fa-solid fa-skull"></i>
      </span>
      <span v-else-if="!jugador.estaVivo && !jugador.muerteConfirmada" class="overlay-semimuerto">
        <i class="fa-solid fa-user-injured colorear"></i>
      </span>
    </div>

    <div class="carta-mesa-datos">
      <p class="carta-mesa-nombre">
        {{ jugador.nombre }}
        <span v-if="jugador.alcalde" class="alcalde-inline" title="Alcalde">
          <i class="fa-solid fa-medal"></i>
        </span>
      </p>
      <p class="carta-mesa-rol" :style="{ color: getColorBando(rolJugador) }">
        {{ nombreRolJugador || '???' }}
      </p>
    </div>

    <div class="carta-iconos">
      <span v-if="jugador.alcalde" class="alcalde-badge" title="Alcalde">
        <i class="fa-solid fa-medal"></i>
      </span>
      <span
        v-else-if="jugador.votos > 0 && jugador.estaVivo"
        :class="esVotacionLobos ? 'badge-votos-lobo' : 'badge-votos'"
      >
        <i :class="esVotacionLobos ? 'fa-solid fa-bone' : 'fa-solid fa-check'"></i>
        {{ jugador.votos }}
      </span>
      <span v-if="jugador.esEnamorado" class="badge-enamorado" title="Enamorado">
        <i class="fa-solid fa-heart"></i>
      </span>
      <span v-if="jugador.esMentor" class="badge-mentor" title="Mentor del Niño Salvaje">
        <i class="fa-solid fa-hands-holding-child"></i>
      </span>
    </div>
  </div>

  <!-- ── MODO JUGADOR ── -->
  <div
    v-else-if="modoVista === 'jugador'"
    class="carta-mesa carta-jugador"
    :class="{
      muerto: !jugador.estaVivo,
      alcalde: jugador.alcalde,
      seleccionado: jugadorSeleccionado?.idUsuario === jugador.idUsuario,
      envenenado: esEnvenenado,
    }"
    @click="$emit('seleccionar', jugador)"
  >
    <div class="carta-mesa-imagen carta-imagen-oculta">
      <i class="fa-solid fa-person"></i>
      <span v-if="jugador.muerteConfirmada" class="overlay-muerto">
        <i class="fa-solid fa-skull"></i>
      </span>
      <span v-else-if="!jugador.estaVivo && !jugador.muerteConfirmada" class="overlay-semimuerto">
        <i class="fa-solid fa-user-injured colorear"></i>
      </span>
    </div>

    <div class="carta-mesa-datos">
      <p class="carta-mesa-nombre">
        {{ jugador.nombre }}
        <span v-if="jugador.alcalde" class="alcalde-inline" title="Alcalde">
          <i class="fa-solid fa-medal"></i>
        </span>
      </p>
      <p class="carta-mesa-rol oculto">???</p>
    </div>

    <div class="carta-iconos">
      <span v-if="jugador.alcalde" class="alcalde-badge" title="Alcalde">
        <i class="fa-solid fa-medal"></i>
      </span>
      <span
        v-if="jugador.votos > 0 && jugador.estaVivo"
        :class="esVotacionLobos ? 'badge-votos-lobo' : 'badge-votos'"
      >
        <i :class="esVotacionLobos ? 'fa-solid fa-bone' : 'fa-solid fa-check'"></i>
        {{ jugador.votos }}
      </span>
      <span v-if="jugador.esMentor" class="badge-mentor" title="Mentor del Niño Salvaje">
        <i class="fa-solid fa-hands-holding-child"></i>
      </span>
    </div>
  </div>
</template>

<script>
import { getImagenRol, getColorBando } from '@/data/roles.js'
import { mapGetters } from 'vuex'

export default {
  name: 'CartaRol',

  props: {
    modoVista: { type: String, default: 'jugador' },
    nombreRol: { type: String, default: null },
    descripcion: { type: String, default: '' },
    bando: { type: String, default: null },
    jugador: { type: Object, default: () => ({}) },
    jugadorSeleccionado: { type: Object, default: null },
    modoEventos: { type: Boolean, default: false },
    esEnamorado: { type: Boolean, default: false },
    esEnvenenado: { type: Boolean, default: false },
  },

  emits: ['seleccionar'],

  computed: {
    ...mapGetters('sala', ['tipoVotacion']),

    imagen() {
      return getImagenRol(this.nombreRol)
    },
    colorBando() {
      return getColorBando(this.bando)
    },
    nombreRolJugador() {
      return this.jugador?.rol || this.jugador?.nombreRol || null
    },
    rolJugador() {
      return this.jugador?.bando || null
    },
    esVotacionLobos() {
      return this.tipoVotacion === 'LOBOS'
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
  font-size: 1rem;
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
  font-size: 1rem;
  line-height: 1.6;
  font-style: italic;
  margin: 0;
}

.carta-mesa {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #1f1f1f;
  border: 3px solid #333;
  border-radius: 10px;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    border-color 0.2s ease;
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
  color: white;
}

.carta-imagen-oculta {
  background: #2a2a2a;
  aspect-ratio: 3 / 4;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  width: 100%;
}

.carta-imagen-oculta > i {
  font-size: 2.5rem;
  color: #444;
}

.carta-mesa.envenenado {
  border: 3px solid #9b59b6 !important;
  box-shadow:
    0 0 8px rgba(155, 89, 182, 0.5),
    0 0 20px rgba(155, 89, 182, 0.2);
  animation: pulsoVeneno 1.5s ease-in-out infinite;
}

@keyframes pulsoVeneno {
  0%,
  100% {
    box-shadow: 0 0 8px rgba(155, 89, 182, 0.5);
  }
  50% {
    box-shadow: 0 0 18px rgba(155, 89, 182, 0.9);
  }
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

.overlay-semimuerto {
  position: absolute;
  inset: 0;
  background: rgba(139, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  border-radius: 8px;
  color: #ff4444;
}

.carta-mesa.seleccionado {
  transform: scale(0.92);
  border: 5px solid white !important;
  box-shadow:
    0 0 10px white,
    0 0 20px rgba(255, 255, 255, 0.8),
    0 0 30px rgba(255, 255, 255, 0.4);
  transition: all 0.2s ease;
  animation: flashVoto 0.4s ease;
}

@keyframes flashVoto {
  0% {
    box-shadow: 0 0 0px white;
  }
  50% {
    box-shadow: 0 0 25px white;
  }
  100% {
    box-shadow: 0 0 10px white;
  }
}

.carta-mesa.alcalde {
  border: 4px solid #e4ba03 !important;
  box-shadow:
    0 0 8px rgba(228, 186, 3, 0.5),
    0 0 18px rgba(228, 186, 3, 0.25);
  transition:
    box-shadow 0.3s ease,
    border-color 0.3s ease;
}

.carta-mesa.alcalde:hover {
  box-shadow:
    0 0 12px rgba(228, 186, 3, 0.8),
    0 0 25px rgba(228, 186, 3, 0.4);
}

.carta-mesa.turno-activo {
  transform: scale(0.93);
  border: 4px solid white !important;
  box-shadow:
    0 0 10px rgba(255, 255, 255, 0.9),
    0 0 25px rgba(255, 255, 255, 0.6),
    0 0 45px rgba(255, 255, 255, 0.3);
  transition: all 0.25s ease;
}

.carta-mesa-datos {
  width: 100%;
  text-align: center;
}

.carta-mesa-nombre {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  font-weight: bold;
  color: white;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.carta-mesa-rol {
  font-size: 1rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
  color: #8b0000;
}

.carta-mesa-rol.oculto {
  color: #8f8f8f;
  font-size: 1.5rem;
  letter-spacing: 0.2em;
}

.carta-iconos {
  display: flex;
  gap: 6px;
  font-size: 0.85rem;
}

.muerto {
  opacity: 0.45;
}

.alcalde-inline {
  margin-left: 6px;
  color: #e4ba03;
  font-size: 1rem;
}

.alcalde-inline i {
  color: #e4ba03;
}

.badge-votos {
  background: rgba(0, 0, 0, 0.7);
  color: #e4ba03;
  border: 2px solid #e4ba03;
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 1.5rem;
  font-weight: 700;
}

.badge-votos-lobo {
  background: rgba(139, 0, 0, 0.8);
  color: #ff4444;
  border: 2px solid #cc0000;
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 1.5rem;
  font-weight: 700;
  box-shadow: 0 0 6px rgba(204, 0, 0, 0.5);
}

.badge-mentor {
  color: #5dade2;
  font-size: 2rem;
}

.carta-narrador {
  border-width: 2px;
}

.colorear {
  color: white;
  font-size: 5rem;
}
</style>
