<template>
  <div class="poder-lobo">
    <div class="poder-titulo">
      <i class="fa-solid fa-bone"></i>
      Tu poder: Devorar
    </div>

    <p class="poder-descripcion">¡Es hora de cazar! ¿A qué jugador vais a devorar esta noche?</p>

    <div class="victima-box" :class="jugadorSeleccionado ? 'victima-activa' : 'victima-vacia'">
      <i class="fa-solid fa-person"></i>
      <span v-if="jugadorSeleccionado">{{ jugadorSeleccionado.nombre }}</span>
      <span v-else class="victima-placeholder">Selecciona una víctima en el tablero</span>
    </div>

    <button class="btn-poder" :disabled="!jugadorSeleccionado || poderUsado" @click="devorar">
      <i class="fa-solid fa-bone"></i>
      {{ poderUsado ? 'Ya has votado esta noche' : 'Devorar' }}
    </button>

    <div v-if="votosLobos.length > 0" class="votos-lobos">
      <p class="votos-titulo">
        <i class="fa-solid fa-paw"></i>
        Votos de la manada
      </p>
      <div v-for="voto in votosLobos" :key="voto.nombreObjetivo" class="voto-item">
        <span class="voto-lobo">
          <i class="fa-solid fa-bone"></i>
          {{ voto.nombreVotante }}
        </span>
        <i class="fa-solid fa-arrow-right"></i>
        <span class="voto-victima">{{ voto.nombreObjetivo }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { mapGetters } from 'vuex'

export default {
  name: 'PoderLobo',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  data() {
    return {
      poderUsado: false,
      votosLobos: [],
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
  },

  watch: {
    '$store.state.sala.jugadores': {
      handler(jugadores) {
        const nuevosVotos = []
        jugadores.forEach((j) => {
          if (j.votos > 0) {
            nuevosVotos.push({
              nombreVotante: `${j.votos} voto${j.votos > 1 ? 's' : ''}`,
              nombreObjetivo: j.nombre,
            })
          }
        })
        this.votosLobos = nuevosVotos
      },
      deep: true,
    },
  },

  methods: {
    async devorar() {
      if (!this.jugadorSeleccionado || this.poderUsado) return

      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/votar`, {
          idObjetivo: this.jugadorSeleccionado.idUsuario,
        })
        this.poderUsado = true
        this.$emit('devorar')
      } catch (error) {
        this.$store.dispatch('toast/mostrar', { mensaje: 'Error al votar', tipo: 'error' })
      }
    },

    resetear() {
      this.poderUsado = false
      this.votosLobos = []
    },
  },
}
</script>

<style scoped>
.poder-lobo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #cc0000;
}

.poder-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #cc0000;
  display: flex;
  align-items: center;
  gap: 10px;
}

.poder-descripcion {
  font-family: 'Raleway', Arial, sans-serif;
  color: #ccc;
  font-size: 0.9rem;
  text-align: center;
  margin: 0;
  line-height: 1.5;
}

.victima-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  border-radius: 10px;
  width: 100%;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.victima-vacia {
  background: rgba(255, 255, 255, 0.05);
  border: 2px dashed #555;
  color: #555;
}

.victima-activa {
  background: rgba(204, 0, 0, 0.15);
  border: 2px solid #cc0000;
  color: white;
  box-shadow:
    0 0 8px rgba(204, 0, 0, 0.4),
    0 0 20px rgba(204, 0, 0, 0.2);
}

.victima-activa i {
  color: #cc0000;
}

.victima-placeholder {
  font-style: italic;
  font-weight: 400;
}

.btn-poder {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  border: 3px solid #cc0000;
  background: transparent;
  color: #cc0000;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-poder:hover:not(:disabled) {
  background: #cc0000;
  color: white;
}

.btn-poder:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.votos-lobos {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 12px 16px;
  border-radius: 10px;
  background: rgba(204, 0, 0, 0.08);
  border: 1px solid rgba(204, 0, 0, 0.3);
}

.votos-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 0.95rem;
  font-weight: 700;
  color: #cc0000;
  margin: 0 0 4px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.voto-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  color: white;
}

.voto-lobo {
  color: #ff4444;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 6px;
}

.voto-victima {
  color: white;
  font-weight: 700;
}

.voto-item i {
  color: #555;
  font-size: 0.8rem;
}
</style>
