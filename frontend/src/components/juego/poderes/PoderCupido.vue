<template>
  <div class="zona-cupido">
    <div class="poder-cupido">
      <div class="poder-titulo">
        <i class="fa-solid fa-heart" style="color: #ff69b4"></i>
        Tu poder: Flechazo
      </div>

      <p class="poder-descripcion">
        Selecciona dos jugadores en el tablero para enamorarlos.
        <strong>Solo puedes usarlo una vez en toda la partida.</strong>
      </p>

      <div class="enamorados-wrapper">
        <div class="hueco-carta" :class="{ 'hueco-activo': enamorado1 }">
          <div v-if="enamorado1" class="carta-enamorado">
            <i class="fa-solid fa-person"></i>
            <span>{{ enamorado1.nombre }}</span>
            <button class="btn-quitar" :disabled="poderUsado" @click="quitarEnamorado(1)">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>
          <div v-else class="hueco-vacio">
            <i class="fa-solid fa-person"></i>
            <span>Enamorado 1</span>
          </div>
        </div>

        <div class="corazon-central">
          <i class="fa-solid fa-heart"></i>
        </div>

        <div class="hueco-carta" :class="{ 'hueco-activo': enamorado2 }">
          <div v-if="enamorado2" class="carta-enamorado">
            <i class="fa-solid fa-person"></i>
            <span>{{ enamorado2.nombre }}</span>
            <button class="btn-quitar" :disabled="poderUsado" @click="quitarEnamorado(2)">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>
          <div v-else class="hueco-vacio">
            <i class="fa-solid fa-person"></i>
            <span>Enamorado 2</span>
          </div>
        </div>
      </div>

      <p class="instruccion">
        <i class="fa-solid fa-hand-pointer"></i>
        Selecciona jugadores en el tablero para asignarlos
      </p>

      <button
        class="btn-flechazo"
        :disabled="!enamorado1 || !enamorado2 || poderUsado"
        @click="confirmarFlechazo"
      >
        <i class="fa-solid fa-heart"></i>
        {{ poderUsado ? 'Flechazo ya lanzado' : 'Confirmar Flechazo' }}
      </button>
    </div>

    <button class="btn-finalizar" @click="$emit('finalizarTurno')">
      <i class="fa-solid fa-heart-crack"></i>
      Finalizar turno de Cupido
    </button>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { mapGetters } from 'vuex'

export default {
  name: 'PoderCupido',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  emits: ['flechazo', 'finalizarTurno'],

  data() {
    return {
      enamorado1: null,
      enamorado2: null,
      poderUsado: false,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
  },

  watch: {
    jugadorSeleccionado(jugador) {
      if (!jugador || this.poderUsado) return
      if (this.enamorado1?.idUsuario === jugador.idUsuario) return
      if (this.enamorado2?.idUsuario === jugador.idUsuario) return
      if (!this.enamorado1) {
        this.enamorado1 = jugador
      } else if (!this.enamorado2) {
        this.enamorado2 = jugador
      }
    },
  },

  methods: {
    quitarEnamorado(numero) {
      if (numero === 1) this.enamorado1 = null
      else this.enamorado2 = null
    },

    async confirmarFlechazo() {
      if (!this.enamorado1 || !this.enamorado2 || this.poderUsado) return

      console.log('🏹 CUPIDO - codigoSala:', this.codigoSala)
      console.log('🏹 CUPIDO - enamorado1:', this.enamorado1)
      console.log('🏹 CUPIDO - enamorado2:', this.enamorado2)
      console.log('🏹 CUPIDO - URL:', `/partida/${this.codigoSala}/habilidad`)
      console.log('🏹 CUPIDO - body:', {
        nombreHabilidad: 'flechazo',
        objetivos: [this.enamorado1.idUsuario, this.enamorado2.idUsuario],
      })
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
          nombreHabilidad: 'flechazo',
          objetivos: [this.enamorado1.idUsuario, this.enamorado2.idUsuario],
        })

        this.$store.dispatch('sala/setEnamorados', {
          jugador1: this.enamorado1.nombre,
          jugador2: this.enamorado2.nombre,
        })
        this.$store.dispatch('sala/setCupidoUsado')

        this.poderUsado = true

        this.$emit('flechazo', {
          jugador1: this.enamorado1,
          jugador2: this.enamorado2,
        })
      } catch (error) {
        alert('Error al usar el flechazo')
      }
    },

    resetear() {
      this.enamorado1 = null
      this.enamorado2 = null
      this.poderUsado = false
    },
  },
}
</script>

<style scoped>
.zona-cupido {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 600px;
}

.poder-cupido {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #ff69b4;
}

.poder-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #ff69b4;
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

.enamorados-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  justify-content: center;
}

.hueco-carta {
  flex: 1;
  max-width: 180px;
  min-height: 120px;
  border-radius: 10px;
  border: 2px dashed #555;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.hueco-activo {
  border: 2px solid #ff69b4;
  box-shadow:
    0 0 8px rgba(255, 105, 180, 0.4),
    0 0 20px rgba(255, 105, 180, 0.2);
}

.hueco-vacio {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #555;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.85rem;
}

.hueco-vacio i {
  font-size: 2rem;
}

.carta-enamorado {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  width: 100%;
  position: relative;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
}

.carta-enamorado i {
  font-size: 2rem;
  color: #ff69b4;
}

.btn-quitar {
  position: absolute;
  top: 4px;
  right: 4px;
  background: rgba(255, 105, 180, 0.2);
  border: 1px solid #ff69b4;
  color: #ff69b4;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  font-size: 0.7rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.btn-quitar:hover:not(:disabled) {
  background: #ff69b4;
  color: white;
}

.btn-quitar:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.corazon-central {
  font-size: 2rem;
  color: #ff69b4;
  flex-shrink: 0;
  animation: latido 1s ease-in-out infinite;
}

@keyframes latido {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.instruccion {
  font-family: 'Raleway', Arial, sans-serif;
  color: #888;
  font-size: 0.85rem;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-flechazo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  border: 3px solid #ff69b4;
  background: transparent;
  color: #ff69b4;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-flechazo:hover:not(:disabled) {
  background: #ff69b4;
  color: white;
}

.btn-flechazo:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-finalizar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  border: 2px solid #e4ba03;
  background: transparent;
  color: #e4ba03;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-finalizar:hover {
  background: #e4ba03;
  color: #000;
}
</style>
