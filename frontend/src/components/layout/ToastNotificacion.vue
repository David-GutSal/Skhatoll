<template>
  <Transition name="toast">
    <div
      v-if="visible"
      class="toast"
      :class="`toast-${tipo}`"
      @click="ocultar"
      @mouseenter="pausarTimer"
      @mouseleave="reanudarTimer"
    >
      <button class="toast-cerrar" @click.stop="ocultar">
        <i class="fa-solid fa-xmark"></i>
      </button>

      <span class="toast-icono">
        <i :class="icono"></i>
      </span>

      <div class="toast-cuerpo">
        <span class="toast-texto">{{ mensaje }}</span>

        <span v-if="colaPendiente > 0" class="toast-cola-badge"> +{{ colaPendiente }} más </span>
      </div>

      <div class="toast-barra" :key="mensaje" :style="{ animationDuration: duracion + 'ms' }"></div>
    </div>
  </Transition>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'ToastNotificacion',

  data() {
    return {
      _timer: null,
      _tiempoRestante: null,
      _tiempoInicio: null,
    }
  },

  computed: {
    ...mapGetters('toast', ['mensaje', 'tipo', 'visible', 'colaPendiente']),

    icono() {
      const iconos = {
        exito: 'fa-solid fa-circle-check',
        error: 'fa-solid fa-circle-xmark',
        aviso: 'fa-solid fa-triangle-exclamation',
        info: 'fa-solid fa-circle-info',
        dia: 'fa-solid fa-sun',
        noche: 'fa-solid fa-moon',
      }
      return iconos[this.tipo] || iconos.info
    },

    duracion() {
      return this.tipo === 'error' ? 6000 : 4000
    },
  },

  watch: {
    visible(val) {
      if (val) {
        this.iniciarTimer()
      }
    },
  },

  beforeUnmount() {
    clearTimeout(this._timer)
  },

  methods: {
    iniciarTimer(tiempoRestante = null) {
      clearTimeout(this._timer)
      const tiempo = tiempoRestante ?? this.duracion
      this._tiempoRestante = tiempo
      this._tiempoInicio = Date.now()
      this._timer = setTimeout(() => {
        this.$store.dispatch('toast/ocultar')
      }, tiempo)
    },

    pausarTimer() {
      if (!this._tiempoInicio) return
      clearTimeout(this._timer)
      const transcurrido = Date.now() - this._tiempoInicio
      this._tiempoRestante = Math.max(0, this._tiempoRestante - transcurrido)
    },

    reanudarTimer() {
      if (this._tiempoRestante > 0) {
        this.iniciarTimer(this._tiempoRestante)
      }
    },

    ocultar() {
      clearTimeout(this._timer)
      this.$store.dispatch('toast/ocultar')
    },
  },
}
</script>

<style scoped>
.toast {
  position: fixed;
  top: 90px;
  right: 28px;
  z-index: 9999;
  width: 420px;
  max-width: calc(100vw - 32px);
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 18px 20px;
  border-radius: 15px;
  background: rgba(10, 10, 10, 0.96);
  border-left: 5px solid;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  position: fixed;
}

.toast-exito {
  border-color: #2d9e2d;
}
.toast-exito .toast-icono {
  color: #5fd45f;
}
.toast-exito .toast-barra {
  background: #2d9e2d;
}

.toast-error {
  border-color: #cc0000;
}
.toast-error .toast-icono {
  color: #ff5555;
}
.toast-error .toast-barra {
  background: #cc0000;
}

.toast-aviso {
  border-color: #e4ba03;
}
.toast-aviso .toast-icono {
  color: #e4ba03;
}
.toast-aviso .toast-barra {
  background: #e4ba03;
}

.toast-info {
  border-color: #006199;
}
.toast-info .toast-icono {
  color: #4db8f0;
}
.toast-info .toast-barra {
  background: #0087bd;
}

.toast-dia {
  border-color: #0087bd;
}
.toast-dia .toast-icono {
  color: #4db8f0;
}
.toast-dia .toast-barra {
  background: #0087bd;
}

.toast-noche {
  border-color: #5500a5;
}
.toast-noche .toast-icono {
  color: #aa66ff;
}
.toast-noche .toast-barra {
  background: #5500a5;
}

.toast-icono {
  font-size: 1.2rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.toast-cuerpo {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toast-texto {
  line-height: 1.4;
}

.toast-cola-badge {
  font-size: 0.75rem;
  font-weight: 600;
  color: #888;
  letter-spacing: 0.03em;
}

.toast-cerrar {
  position: absolute;
  top: 8px;
  right: 8px;
  background: transparent;
  border: none;
  color: #c50000;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 4px;
  line-height: 1;
  transition: color 0.2s ease;
}

.toast-cerrar:hover {
  color: white;
}

.toast-barra {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
  animation: barra linear forwards;
}

@keyframes barra {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.toast:hover .toast-barra {
  animation-play-state: paused;
}

.toast-enter-active {
  animation: entrar 0.35s cubic-bezier(0.34, 1.4, 0.64, 1) forwards;
}

.toast-leave-active {
  animation: salir 0.3s ease-in forwards;
}

.fa-sun{
  color: #e4ba03;
}

@keyframes entrar {
  from {
    opacity: 0;
    transform: translateX(60px) scale(0.92);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes salir {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
}
</style>
