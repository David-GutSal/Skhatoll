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

    <!-- Resultado de la premonición -->
    <div v-if="jugadorRevelado" class="revelacion">
      <p class="revelacion-titulo">
        <i class="fa-solid fa-eye"></i>
        {{ jugadorRevelado.nombre }} es...
      </p>

      <!-- ZONA VISIBLE PROVISIONAL — borrar después -->
      <!-- Simulación: cogemos los datos del jugador directamente del store -->
      <CartaRol
        modoVista="narrador"
        :jugador="jugadorRevelado"
        :esDia="false"
        :modoEventos="false"
        :jugadorSeleccionado="null"
        class="carta-revelada"
      />
      <!-- FIN ZONA PROVISIONAL -->

      <!-- ZONA COMENTADA — sustituir por endpoint cuando esté listo -->
      <!--
      Endpoint a usar:
      GET /partida/{codigoSala}/poder/vidente?idObjetivo={jugadorSeleccionado.idUsuario}

      Respuesta esperada:
      { nombre: "Juan", nombreRol: "Lobo", bando: "lobo" }

      Código a descomentar:
      const res = await axiosInstance.get(
        `/partida/${this.codigoSala}/poder/vidente`,
        { params: { idObjetivo: this.jugadorSeleccionado.idUsuario } }
      )
      this.jugadorRevelado = res.data
      -->
    </div>

  </div>
</template>

<script>
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
    ...mapGetters('sala', ['jugadores']),
  },

  watch: {
    // Cuando cambia esMiTurno a false (turno terminado), reseteamos
    // Esto lo controla el padre pasando esMiTurno como prop
  },

  methods: {
    usarPremonicion() {
      if (!this.jugadorSeleccionado || this.poderUsado) return

      // ZONA VISIBLE PROVISIONAL — borrar después
      // Buscamos el jugador en el store para obtener sus datos completos
      const jugadorEnStore = this.jugadores.find(
        (j) => j.idUsuario === this.jugadorSeleccionado.idUsuario
      )
      this.jugadorRevelado = jugadorEnStore || this.jugadorSeleccionado
      // FIN ZONA PROVISIONAL

      this.poderUsado = true
      this.$emit('premonicion', this.jugadorSeleccionado)
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
  transition: background 0.2s ease, color 0.2s ease;
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