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

    
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'

const props = defineProps({
  jugadorSeleccionado: { type: Object, default: null },
})

const emit = defineEmits(['flechazo', 'finalizarTurno'])

const store = useStore()

const enamorado1 = ref(null)
const enamorado2 = ref(null)
const poderUsado = ref(false)

const codigoSala = computed(() => store.getters['sala/codigoSala'])

watch(() => props.jugadorSeleccionado, (jugador) => {
  if (!jugador || poderUsado.value) return
  if (enamorado1.value?.idUsuario === jugador.idUsuario) return
  if (enamorado2.value?.idUsuario === jugador.idUsuario) return
  if (!enamorado1.value) {
    enamorado1.value = jugador
  } else if (!enamorado2.value) {
    enamorado2.value = jugador
  }
})

const quitarEnamorado = (numero) => {
  if (numero === 1) enamorado1.value = null
  else enamorado2.value = null
}

const confirmarFlechazo = async () => {
  if (!enamorado1.value || !enamorado2.value || poderUsado.value) return

  try {
    await axiosInstance.post(`/partida/${codigoSala.value}/habilidad`, {
      nombreHabilidad: 'flechazo',
      objetivos: [enamorado1.value.idUsuario, enamorado2.value.idUsuario],
    })

    store.dispatch('sala/setEnamorados', {
      jugador1: enamorado1.value.nombre,
      jugador2: enamorado2.value.nombre,
    })
    store.dispatch('sala/setCupidoUsado')

    poderUsado.value = true

    emit('flechazo', {
      jugador1: enamorado1.value,
      jugador2: enamorado2.value,
    })
  } catch (error) {
    store.dispatch('toast/mostrar', {
      mensaje: 'Error al usar el flechazo',
      tipo: 'error',
    })
  }
}

const resetear = () => {
  enamorado1.value = null
  enamorado2.value = null
  poderUsado.value = false
}

defineExpose({ resetear })
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
  font-family: var(--font-cinzel);
  font-size: 1.3rem;
  font-weight: 700;
  color: #ff69b4;
  display: flex;
  align-items: center;
  gap: 10px;
}

.poder-descripcion {
  font-family: var(--font-raleway);
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
  transition: var(--transition-fast);
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
  font-family: var(--font-raleway);
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
  font-family: var(--font-raleway);
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
  transition: background var(--transition-fast);
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
  font-family: var(--font-raleway);
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
  font-family: var(--font-raleway);
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
  border: 2px solid var(--color-dorado);
  background: transparent;
  color: var(--color-dorado);
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-finalizar:hover {
  background: var(--color-dorado);
  color: #000;
}
</style>
