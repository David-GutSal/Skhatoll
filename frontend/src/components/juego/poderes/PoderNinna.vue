<template>
  <div class="poder-ninna">
    <div class="poder-titulo">
      <i class="fa-solid fa-binoculars"></i>
      Tu poder: Espiar
    </div>

    <p class="poder-descripcion">
      Esta noche puedes asomarte a la ventana y observar lo que pasa por tu ventana. Ten cuidado.
      <strong>Solo puedes usarlo una vez esta noche.</strong>
    </p>

    <div class="panel-ventana">
      <!-- Marco de la ventana -->
      <div class="ventana-marco" :class="{ 'ventana-abierta': ventanaAbierta }">
        <!-- Cielo nocturno siempre visible -->
        <div class="ventana-cielo">
          <div class="estrellas">
            <span v-for="n in 12" :key="n" class="estrella" :style="estrellaEstilo(n)">★</span>
          </div>
          <i class="fa-solid fa-moon luna"></i>
        </div>

        <!-- Contenido al asomarse -->
        <transition name="fade-ventana">
          <div v-if="ventanaAbierta" class="ventana-contenido">
            <p class="frase-ninna">
              Ves sombras oscuras que parecen ser tus vecinos pero...
              <strong>¡Qué es eso! ¡Parece que uno tiene orejas y unos colmillos afilados!</strong>
            </p>
            <div class="sospechosos-lista">
              <span v-for="nombre in sospechosos" :key="nombre" class="chip-sospechoso">
                <i class="fa-solid fa-person"></i>
                {{ nombre }}
              </span>
            </div>
          </div>
        </transition>

        <!-- Alféizar de la ventana -->
        <div class="ventana-alfeizar"></div>
      </div>

      <!-- Botón principal -->
      <button
        v-if="!ventanaAbierta"
        class="btn-ventana"
        :disabled="cargando"
        @click="asomarseVentana"
      >
        <i class="fa-solid fa-binoculars"></i>
        {{ cargando ? 'Mirando...' : 'Asomarme a la ventana' }}
      </button>

      <button v-else class="btn-dormir" @click="$emit('finalizarTurno')">
        Mejor me voy a dormir
        <i class="fa-solid fa-bed"></i>
      </button>
    </div>
  </div>
</template>

<script>
// import axiosInstance from '@/plugins/axios'   ← DESCOMENTAR cuando el endpoint espiar esté disponible
import { mapGetters, mapState } from 'vuex'

export default {
  name: 'PoderNinna',

  emits: ['finalizarTurno'],

  data() {
    return {
      ventanaAbierta: false,
      cargando: false,
      sospechosos: [],
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
    ...mapState('sala', ['jugadores']),
  },

  methods: {
    async asomarseVentana() {
      if (this.cargando || this.ventanaAbierta) return
      this.cargando = true

      // ─────────────────────────────────────────────────────────────
      // OPCIÓN A — Endpoint espiar (PENDIENTE, habilitar cuando esté disponible)
      // ─────────────────────────────────────────────────────────────
      // try {
      //   const res = await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
      //     nombreHabilidad: 'espiar',
      //     objetivos: [],
      //   })
      //   const detalle = res.data?.detalle
      //   if (Array.isArray(detalle) && detalle.length > 0) {
      //     this.sospechosos = detalle
      //   } else {
      //     this.sospechosos = []
      //   }
      // } catch {
      //   this.$store.dispatch('toast/mostrar', { mensaje: 'Error al espiar', tipo: 'error' })
      //   this.cargando = false
      //   return
      // }
      // ─────────────────────────────────────────────────────────────

      // ─────────────────────────────────────────────────────────────
      // OPCIÓN B — Jugadores aleatorios (provisional hasta que el endpoint esté disponible)
      // Selecciona entre 2 y 4 jugadores vivos al azar, excluyendo a la propia niña
      // ─────────────────────────────────────────────────────────────
      const vivosAjenos = this.jugadores.filter(
        (j) => j.estaVivo && j.nombre !== this.$store.getters['auth/nombre'],
      )
      const cantidad = Math.min(vivosAjenos.length, Math.floor(Math.random() * 3) + 2)
      const mezclados = [...vivosAjenos].sort(() => Math.random() - 0.5)
      this.sospechosos = mezclados.slice(0, cantidad).map((j) => j.nombre)
      // ─────────────────────────────────────────────────────────────

      this.cargando = false
      this.ventanaAbierta = true
    },

    estrellaEstilo(n) {
      return {
        left: `${((n * 37) % 90) + 5}%`,
        top: `${((n * 23) % 60) + 5}%`,
        fontSize: `${(n % 3) * 4 + 8}px`,
        opacity: `${((n * 17) % 5) * 0.15 + 0.4}`,
        animationDelay: `${(n * 0.3) % 2}s`,
      }
    },

    resetear() {
      this.ventanaAbierta = false
      this.sospechosos = []
      this.cargando = false
    },

    resetearNoche() {
      this.resetear()
    },
  },
}
</script>

<style scoped>
.poder-ninna {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  max-width: 500px;
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #5dade2;
}

.poder-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #5dade2;
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

.panel-ventana {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0;
  width: 100%;
  overflow: hidden;
}

.ventana-marco {
  width: 100%;
  min-height: 200px;
  background: #0a0a1a;
  position: relative;
  display: flex;
  flex-direction: column;
  border: 5px solid #5dade2;
  transition: min-height 0.5s ease;
  overflow: hidden;
}

.ventana-abierta {
  min-height: 320px;
}

.ventana-marco::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 4px;
  height: 100%;
  background: #5dade2;
  opacity: 0.4;
  z-index: 2;
}

.ventana-marco::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
  width: 100%;
  height: 4px;
  background: #5dade2;
  opacity: 0.4;
  z-index: 2;
}

.ventana-cielo {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.estrellas {
  position: absolute;
  inset: 0;
}

.estrella {
  position: absolute;
  color: white;
  animation: parpadeo 3s ease-in-out infinite;
}

@keyframes parpadeo {
  0%,
  100% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.2;
  }
}

.luna {
  position: absolute;
  top: 12px;
  right: 16px;
  font-size: 2rem;
  color: #f0e68c;
  filter: drop-shadow(0 0 8px rgba(240, 230, 140, 0.6));
  z-index: 1;
}

.ventana-contenido {
  position: relative;
  z-index: 3;
  padding: 20px 20px 10px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: auto;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.85) 60%, transparent);
}

.frase-ninna {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  color: #ccc;
  margin: 0;
  line-height: 1.6;
  font-style: italic;
}

.frase-ninna strong {
  color: #ff4444;
  font-style: normal;
}

.sospechosos-lista {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip-sospechoso {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(93, 173, 226, 0.15);
  border: 1px solid #5dade2;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.85rem;
  font-weight: 700;
}

.chip-sospechoso i {
  color: #5dade2;
}

.ventana-alfeizar {
  height: 14px;
  background: #5dade2;
  opacity: 0.3;
  width: 100%;
  position: absolute;
  bottom: 0;
}

.btn-ventana,
.btn-dormir {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 24px;
  width: 100%;
  border: none;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-ventana {
  background: #5dade2;
  color: white;
}

.btn-ventana:hover:not(:disabled) {
  background: #3a8fc0;
}

.btn-ventana:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-dormir {
  background: #830000;
  color: #aaa;
  border-top: 1px solid #830000;
}

.btn-dormir:hover {
  background: #1a1a2e;
  color: white;
}

.fade-ventana-enter-active {
  transition:
    opacity 0.6s ease,
    transform 0.6s ease;
}
.fade-ventana-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-ventana-enter-to {
  opacity: 1;
  transform: translateY(0);
}

@keyframes aparecer {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
