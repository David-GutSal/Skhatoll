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
      <img src="@/assets/imgs/logo-skhatoll.png" alt="logo" class="logo-frase" />
      <div class="frase-caja">
        <transition name="fade" mode="out-in">
          <p class="frase" :key="fraseActual">"{{ FRASES[fraseActual] }}"</p>
        </transition>
      </div>
      <img src="@/assets/imgs/logo-skhatoll.png" alt="logo" class="logo-frase" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import axiosInstance from '@/plugins/axios'
import { FRASES, getImagenRol, getColorBando } from '@/data/roles.js'

const router = useRouter()
const store = useStore()

const nombreRol = ref('')
const descripcionRol = ref('')
const bando = ref('')
const fraseActual = ref(0)
const intervaloFrases = ref(null)

const imagenRol = computed(() => getImagenRol(nombreRol.value))
const colorBando = computed(() => getColorBando(bando.value))

const cargarRol = (nombre) => {
  nombreRol.value = nombre
  descripcionRol.value = store.getters['sala/miRolDescripcion']
  bando.value = store.getters['sala/miBando']
}

onMounted(() => {
  intervaloFrases.value = setInterval(() => {
    fraseActual.value = (fraseActual.value + 1) % FRASES.length
  }, 8000)

  const rol = store.getters['sala/miRol']
  if (rol) {
    cargarRol(rol)
    setTimeout(() => router.push({ name: 'jugador' }), 5000)
  } else {
    const codigo = store.getters['sala/codigoSala'] || sessionStorage.getItem('codigoSala')
    if (codigo) {
      axiosInstance.get(`/salas/${codigo}/mi-rol`).then((res) => {
        if (res.data && res.data.nombreRol) {
          store.dispatch('sala/setRol', {
            nombreRol: res.data.nombreRol,
            descripcionRol: res.data.descripcionRol || '',
            bando: res.data.bando || '',
          })
          cargarRol(res.data.nombreRol)
          setTimeout(() => router.push({ name: 'jugador' }), 5000)
        }
      }).catch(() => {})
    }
    const unwatch = watch(
      () => store.getters['sala/miRol'],
      (nuevoRol) => {
        if (nuevoRol) {
          cargarRol(nuevoRol)
          setTimeout(() => router.push({ name: 'jugador' }), 5000)
          unwatch()
        }
      },
    )
  }
})

onUnmounted(() => {
  clearInterval(intervaloFrases.value)
})
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
  font-family: var(--font-cinzel);
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
  border: 10px solid var(--color-dorado) !important;
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
  font-family: var(--font-cinzel);
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
  font-family: var(--font-raleway);
  font-weight: 700;
  font-size: 0.75rem;
  color: white;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.campo-valor {
  font-family: var(--font-raleway);
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



.texto-espera-rol {
  font-family: var(--font-raleway);
  font-style: italic;
  color: #aaa;
  margin: 0;
}




</style>
