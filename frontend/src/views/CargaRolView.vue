<template>
  <div class="contenedor-carga">
    <div v-if="nombreRol" class="wrapper-carta">
      <p class="texto-tu-rol">Tu rol es:</p>

      <div class="caja-carta" :style="{ borderColor: colorBando }">
        <div class="carta-imagen-wrapper">
          <img :src="imagenRol" :alt="nombreRol" class="carta-imagen" />
          <div class="carta-bando-badge" :style="{ background: colorBando }">
            {{ bando ? bando.toUpperCase() : '' }}
          </div>
        </div>

        <div class="carta-datos">
          <div class="carta-campo">
            <span class="campo-label">Rol</span>
            <span class="campo-valor">{{ nombreRol }}</span>
          </div>
          <div class="carta-campo">
            <span class="campo-label">Bando</span>
            <span class="campo-valor">{{ bando }}</span>
          </div>
          <div class="carta-campo carta-descripcion-wrapper">
            <span class="campo-label">Descripción</span>
            <span class="campo-valor campo-descripcion">{{ descripcionRol }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="esperando">
      <div class="spinner"></div>
      <p class="texto-espera-rol">Asignando rol...</p>
    </div>

    <div class="frases-wrapper">
      <img src="@/assets/imgs/logo-provisional.png" alt="logo" class="logo-frase" />
      <div class="frase-caja">
        <transition name="fade" mode="out-in">
          <p class="frase" :key="fraseActual">"{{ FRASES[fraseActual] }}"</p>
        </transition>
      </div>
      <img src="@/assets/imgs/logo-provisional.png" alt="logo" class="logo-frase" />
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { FRASES, getImagenRol, getColorBando } from '@/data/roles.js'

export default {
  name: 'CargaRolView',

  data() {
    return {
      nombreRol: '',
      descripcionRol: '',
      bando: '',
      fraseActual: 0,
      FRASES,
      intervaloFrases: null,
    }
  },

  computed: {
    imagenRol() {
      return getImagenRol(this.nombreRol)
    },
    colorBando() {
      return getColorBando(this.bando)
    },
  },

  created() {
    const esCreador = this.$store.getters['sala/esCreador']
    if (esCreador) {
      this.$router.push({ name: 'esperaNarrador' })
      return
    }

/* Info profesor: previa para que se puedan ver sin tiempo las vistas de espera
    Descomentar para ver e ir a ir a router/index.js para habilitar los enlaces al router
    Vista espera de los jugadores: http://localhost:5173/preview-carga
    Vista  espera narrador: http://localhost:5173/preview-espera */
//Texto de relleno
    /*this.nombreRol = 'BRUJA'
    this.descripcionRol = 'Posees dos pociones...'
    this.bando = 'aldea'
*/
    this.intervaloFrases = setInterval(() => {
      this.fraseActual = (this.fraseActual + 1) % FRASES.length
    }, 8000)

    const nombreRol = this.$store.getters['sala/miRol']
    if (nombreRol) {
      this.cargarRol(nombreRol)
      setTimeout(() => this.$router.push({ name: 'jugador' }), 5000)
    } else {
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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  box-sizing: border-box;
  gap: 40px;
}

.texto-tu-rol {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 3rem;
  font-weight: 700;
  color: white;
  text-align: center;
  margin: 0 0 20px 0;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.wrapper-carta {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.caja-carta {
  background: #000;
  border: 10px solid #e4ba03 !important;
  border-radius: 16px;
  overflow: hidden;
  max-width: 340px;
  width: 100%;
  box-shadow: 0 0 30px rgba(228, 186, 3, 0.15);
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

.carta-datos {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.carta-campo {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.campo-label {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.75rem;
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.campo-valor {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.95rem;
  color: #aaa;
}

.campo-descripcion {
  font-style: italic;
  line-height: 1.5;
  font-size: 0.85rem;
}

.esperando {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.spinner {
  width: 52px;
  height: 52px;
  border: 4px solid rgba(255, 255, 255, 0.15);
  border-top-color: #e4ba03;
  border-radius: 50%;
  animation: girar 0.9s linear infinite;
}

@keyframes girar {
  to {
    transform: rotate(360deg);
  }
}

.texto-espera-rol {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: #aaa;
  margin: 0;
}

.frases-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
  max-width: 600px;
  width: 100%;
}

.logo-frase {
  height: 36px;
  width: auto;
  object-fit: contain;
  flex-shrink: 0;
  opacity: 0.8;
}

.frase-caja {
  flex: 1;
  background: #000;
  padding: 10px 16px;
  border-radius: 6px;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.frase {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: white;
  font-size: 0.9rem;
  line-height: 1.5;
  text-align: center;
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

@media (max-width: 600px) {
  .texto-tu-rol {
    font-size: 2rem;
  }
  .caja-carta {
    max-width: 280px;
  }
  .frases-wrapper {
    gap: 10px;
  }
  .logo-frase {
    height: 26px;
  }
  .frase {
    font-size: 0.8rem;
  }
}

@media (max-width: 380px) {
  .texto-tu-rol {
    font-size: 1.6rem;
  }
  .logo-frase {
    display: none;
  }
}
</style>
