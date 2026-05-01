<template>
  <div class="poder-vidente" :class="esDia ? 'poder-dia' : 'poder-noche'">
    <div class="poder-titulo">
      <i class="fa-solid fa-eye"></i>
      Tu poder: Premonición
    </div>

    <p class="poder-descripcion">
      Selecciona un jugador en el tablero y revela su identidad secreta.
      <strong>Solo puedes usarlo una vez esta noche.</strong>
    </p>

    <button
      class="btn-poder"
      :disabled="!jugadorSeleccionado || poderUsado"
      @click="usarPremonicion"
    >
      <i class="fa-solid fa-eye"></i>
      {{ poderUsado ? 'Ya has usado tu visión esta noche' : 'Revelar identidad' }}
    </button>

    <div v-if="jugadorRevelado" class="revelacion">
      <p class="revelacion-titulo">
        <i class="fa-solid fa-eye"></i>
        {{ jugadorRevelado.nombre }} es...
      </p>
      <CartaRol
        modoVista="narrador"
        :jugador="jugadorRevelado"
        :esDia="false"
        :modoEventos="false"
        :jugadorSeleccionado="null"
        class="carta-revelada"
      />
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { mapGetters } from 'vuex'
import CartaRol from '@/components/juego/roles/CartaRol.vue'

export default {
  name: 'PoderVidente',
  components: { CartaRol },

  props: {
    jugadorSeleccionado: { type: Object, default: null },
    esDia: { type: Boolean, default: false },
  },

  data() {
    return {
      poderUsado: false,
      jugadorRevelado: null,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
  },

  methods: {
    async usarPremonicion() {
      if (!this.jugadorSeleccionado || this.poderUsado) return

      try {
        const res = await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
          nombreHabilidad: 'vision',
          objetivos: [this.jugadorSeleccionado.idUsuario],
        })

        // res.data.detalle = { nombreRol, bando }
        this.jugadorRevelado = {
          nombre: this.jugadorSeleccionado.nombre,
          nombreRol: res.data.detalle.nombreRol,
          bando: res.data.detalle.bando,
          estaVivo: true,
        }

        this.poderUsado = true
        this.$emit('premonicion', this.jugadorSeleccionado)
      } catch (error) {
        this.$store.dispatch('toast/mostrar', { mensaje: 'Error al usar la visión', tipo: 'error' })
      }
    },

    resetear() {
      this.poderUsado = false
      this.jugadorRevelado = null
    },
  },
}
</script>

<style scoped>
.poder-vidente {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
}

.poder-noche {
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #cc0000;
}

.poder-dia {
  background: rgba(0, 0, 0, 0.7);
  border: 3px solid #e4ba03;
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

.revelacion {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.revelacion-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.revelacion-titulo i {
  color: #cc0000;
}

.carta-revelada {
  max-width: 220px;
  width: 100%;
}
</style>
