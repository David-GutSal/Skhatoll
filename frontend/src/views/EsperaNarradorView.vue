<template>
  <div class="contenedor-carga">

    <!-- Carta del rol asignado -->
    <div v-if="nombreRol" class="wrapper-carta">
      <p class="texto-sobre-carta">Tu rol en esta partida es...</p>
      <CartaRol
        modoVista="carga"
        :nombreRol="nombreRol"
        :descripcion="descripcionRol"
        :bando="bando"
      />
    </div>

    <!-- Esperando asignación -->
    <div v-else class="esperando">
      <div class="spinner"></div>
      <p>Asignando rol...</p>
    </div>

    <!-- Mensaje de espera -->
    <p class="mensaje-espera">
      <i class="fa-solid fa-hourglass-half"></i>
      Espera a que el narrador inicie la partida...
    </p>

    <!-- Slider de frases -->
    <div class="slider-frases">
      <transition name="fade" mode="out-in">
        <p class="frase" :key="fraseActual">"{{ FRASES[fraseActual] }}"</p>
      </transition>
    </div>

  </div>
</template>

<script>
import { mapActions } from 'vuex'
import CartaRol from '@/components/juego/roles/CartaRol.vue'
import { FRASES } from '@/data/roles.js'

export default {
  name: 'CargaRolView',
  components: { CartaRol },

  data() {
    return {
      nombreRol: '',
      descripcionRol: '',
      bando: '',
      fraseActual: 0,
      stompClient: null,
      FRASES,
      intervaloFrases: null,
    }
  },

  created() {
    // Si es el narrador lo mandamos a su pantalla de espera
    const esCreador = this.$store.getters['sala/esCreador']
    if (esCreador) {
      this.$router.push({ name: 'esperaNarrador' })
      return
    }

    // Iniciar rotación de frases
    this.intervaloFrases = setInterval(() => {
      this.fraseActual = (this.fraseActual + 1) % FRASES.length
    }, 8000)

    // Intentar leer el rol ya asignado del store
    const nombreRol = this.$store.getters['sala/miRol']
    if (nombreRol) {
      this.cargarRol(nombreRol)
      setTimeout(() => this.$router.push({ name: 'jugador' }), 5000)
    } else {
      // Esperar a que llegue el rol por WebSocket
      const unwatch = this.$watch(
        () => this.$store.getters['sala/miRol'],
        (nuevoRol) => {
          if (nuevoRol) {
            this.cargarRol(nuevoRol)
            setTimeout(() => this.$router.push({ name: 'jugador' }), 5000)
            unwatch()
          }
        },
      )
    }
  },

  beforeUnmount() {
    clearInterval(this.intervaloFrases)
  },

  methods: {
    ...mapActions('sala', ['setRol']),

    cargarRol(nombreRol) {
      this.nombreRol = nombreRol
      this.descripcionRol = this.$store.getters['sala/miRolDescripcion']
      this.bando = this.$store.getters['sala/miBando']
    },
  },
}
</script>

<style scoped>
.contenedor-carga {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 160px);
  padding: 40px 20px;
  box-sizing: border-box;
  gap: 30px;
}

.texto-sobre-carta {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.1rem;
  color: #ccc;
  text-align: center;
  letter-spacing: 0.08em;
  margin: 0 0 16px 0;
  text-transform: uppercase;
}

.wrapper-carta {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.esperando {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(255, 255, 255, 0.2);
  border-top-color: #8b0000;
  border-radius: 50%;
  animation: girar 0.9s linear infinite;
}

@keyframes girar {
  to { transform: rotate(360deg); }
}

.mensaje-espera {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: #ccc;
  font-style: italic;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Frases */
.slider-frases {
  max-width: 500px;
  text-align: center;
  min-height: 48px;
}

.frase {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: #aaa;
  font-size: 0.95rem;
  line-height: 1.6;
  margin: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.6s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 480px) {
  .contenedor-carga {
    padding: 24px 16px;
  }
}
</style>