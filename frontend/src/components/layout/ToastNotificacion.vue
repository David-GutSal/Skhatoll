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

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

const _timer = ref(null)
const _tiempoRestante = ref(null)
const _tiempoInicio = ref(null)

const mensaje = computed(() => store.getters['toast/mensaje'])
const tipo = computed(() => store.getters['toast/tipo'])
const visible = computed(() => store.getters['toast/visible'])
const colaPendiente = computed(() => store.getters['toast/colaPendiente'])

const icono = computed(() => {
  const iconos = {
    exito: 'fa-solid fa-circle-check',
    error: 'fa-solid fa-circle-xmark',
    aviso: 'fa-solid fa-triangle-exclamation',
    info: 'fa-solid fa-circle-info',
    dia: 'fa-solid fa-sun',
    noche: 'fa-solid fa-moon',
    licantropia: 'fa-solid fa-paw',
    brujeria: 'fa-solid fa-hat-wizard',
    videncia: 'fa-solid fa-eye',
    amorio: 'fa-solid fa-heart',
  }
  return iconos[tipo.value] || iconos.info
})

const duracion = computed(() => tipo.value === 'error' ? 6000 : 4000)

const iniciarTimer = (tiempoRestante = null) => {
  clearTimeout(_timer.value)
  const tiempo = tiempoRestante ?? duracion.value
  _tiempoRestante.value = tiempo
  _tiempoInicio.value = Date.now()
  _timer.value = setTimeout(() => {
    store.dispatch('toast/ocultar')
  }, tiempo)
}

const pausarTimer = () => {
  if (!_tiempoInicio.value) return
  clearTimeout(_timer.value)
  const transcurrido = Date.now() - _tiempoInicio.value
  _tiempoRestante.value = Math.max(0, _tiempoRestante.value - transcurrido)
}

const reanudarTimer = () => {
  if (_tiempoRestante.value > 0) {
    iniciarTimer(_tiempoRestante.value)
  }
}

const ocultar = () => {
  clearTimeout(_timer.value)
  store.dispatch('toast/ocultar')
}

watch(visible, (val) => {
  if (val) {
    iniciarTimer()
  }
})

onUnmounted(() => {
  clearTimeout(_timer.value)
})
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
  font-family: var(--font-raleway);
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
  cursor: pointer;
  overflow: visible;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  position: fixed;
}

.toast-exito,
.toast-brujeria {
  border-color: #2d9e2d;
}
.toast-exito .toast-icono,
.toast-brujeria .toast-icono {
  color: #5fd45f;
}
.toast-exito .toast-barra,
.toast-brujeria .toast-barra {
  background: #2d9e2d;
}

.toast-error,
.toast-licantropia {
  border-color: var(--color-rojo);
}
.toast-error .toast-icono,
.toast-licantropia .toast-icono {
  color: #ff5555;
}
.toast-error .toast-barra,
.toast-licantropia .toast-barra {
  background: var(--color-rojo);
}

.toast-aviso {
  border-color: var(--color-dorado);
}
.toast-aviso .toast-icono {
  color: var(--color-dorado);
}
.toast-aviso .toast-barra {
  background: var(--color-dorado);
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

.toast-noche,
.toast-videncia {
  border-color: #5500a5;
}
.toast-noche .toast-icono,
.toast-videncia .toast-icono {
  color: #aa66ff;
}
.toast-noche .toast-barra,
.toast-videncia .toast-barra {
  background: #5500a5;
}

.toast-amorio {
  border-color: #ff27b7;
}

.toast-amorio .toast-icono {
  color: #ff27b7;
}

.toast-amorio .toast-barra{
  background: #9e0069;
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
  word-break: break-word;
  white-space: normal;
  display: block;  
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

.fa-sun {
  color: var(--color-dorado);
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
