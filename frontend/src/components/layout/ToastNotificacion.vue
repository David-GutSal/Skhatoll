<template>
  <Transition name="toast">
    <div
      v-if="visible"
      class="toast"
      :class="`toast-${tipo}`"
      @click="ocultar"
    >
      <span class="toast-icono">
        <i :class="icono"></i>
      </span>
      <span class="toast-texto">{{ mensaje }}</span>
      <div class="toast-barra"></div>
    </div>
  </Transition>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'ToastNotificacion',

  computed: {
    ...mapGetters('toast', ['mensaje', 'tipo', 'visible']),

    icono() {
      const iconos = {
        exito: 'fa-solid fa-circle-check',
        error: 'fa-solid fa-circle-xmark',
        aviso: 'fa-solid fa-triangle-exclamation',
        info:  'fa-solid fa-circle-info',
      }
      return iconos[this.tipo] || iconos.info
    },
  },

  watch: {
    visible(val) {
      if (val) {
        clearTimeout(this._timer)
        this._timer = setTimeout(() => {
          this.$store.dispatch('toast/ocultar')
        }, 4000)
      }
    },
  },

  beforeUnmount() {
    clearTimeout(this._timer)
  },

  methods: {
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
  top: 90px;      /* antes: bottom: 28px */
  right: 28px;
  z-index: 9999;
  width: 420px;   /* antes: 340px */
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 18px 20px;   /* antes: 14px 16px */
  border-radius: 12px;
  background: rgba(10, 10, 10, 0.96);
  border-left: 5px solid;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;      /* antes: 0.9rem */
  font-weight: 700;
  color: white;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}

/* Tipos */
.toast-exito { border-color: #2d9e2d; }
.toast-exito .toast-icono { color: #5fd45f; }
.toast-exito .toast-barra { background: #2d9e2d; }

.toast-error { border-color: #cc0000; }
.toast-error .toast-icono { color: #ff5555; }
.toast-error .toast-barra { background: #cc0000; }

.toast-aviso { border-color: #e4ba03; }
.toast-aviso .toast-icono { color: #e4ba03; }
.toast-aviso .toast-barra { background: #e4ba03; }

.toast-info  { border-color: #006199; }
.toast-info .toast-icono  { color: #4db8f0; }
.toast-info .toast-barra  { background: #0087bd; }

.toast-icono {
  font-size: 1.2rem;
  flex-shrink: 0;
  margin-top: 1px;
}

.toast-texto {
  flex: 1;
  line-height: 1.4;
}

/* Barra de progreso */
.toast-barra {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
  animation: barra 4s linear forwards;
}

@keyframes barra {
  from { width: 100%; }
  to   { width: 0%; }
}

/* Animación de entrada/salida */
.toast-enter-active {
  animation: entrar 0.35s cubic-bezier(0.34, 1.4, 0.64, 1) forwards;
}
.toast-leave-active {
  animation: salir 0.3s ease-in forwards;
}

@keyframes entrar {
  from { opacity: 0; transform: translateX(60px) scale(0.92); }
  to   { opacity: 1; transform: translateX(0) scale(1); }
}

@keyframes salir {
  from { opacity: 1; transform: translateX(0) scale(1); }
  to   { opacity: 0; transform: translateX(60px) scale(0.88); }
}
</style>