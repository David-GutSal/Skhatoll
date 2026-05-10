<template>
  <div class="contenedor-espera">

    <div class="caja-narrador">

      <i class="fa-solid fa-book-open-reader icono-narrador"></i>

      <h1 class="titulo-narrador">Eres el Narrador</h1>
      <div class="consejos">
        <div class="consejo" v-for="(consejo, i) in consejos" :key="i">
          <i class="fa-solid fa-book icono-consejo"></i>
          <p class="consejo-texto">"{{ consejo }}"</p>
        </div>
      </div>
>
      <div class="cargando">
        <div class="spinner"></div>
        <p class="texto-cargando">Ultimando los preparativos...</p>
      </div>

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

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { FRASES } from '@/data/roles.js'

const router = useRouter()
const store = useStore()

const fraseActual = ref(0)
const intervaloFrases = ref(null)

const consejos = ref([
  'Lo más importante es que tanto tú como tus jugadores os divirtáis, sé un buen guía en la aventura.',
  'Ayuda a los jugadores a entender a sus personajes.',
  'Puedes ver lo que hace cada personaje consultando las reglas o haciendo click sobre su carta.',
  'Conviértete en un storyteller, no en un árbitro, sé totalmente neutral.',
  'Controla bien los tiempos y modula tu voz.',
])

onMounted(() => {
  console.log('[EsperaNarrador] mounted')
  intervaloFrases.value = setInterval(() => {
    fraseActual.value = (fraseActual.value + 1) % FRASES.length
  }, 8000)

  setTimeout(() => {
    router.push({ name: 'narrador' })
  }, 5000)
})

onUnmounted(() => {
  clearInterval(intervaloFrases.value)
})
</script>

<style scoped>

.contenedor-espera {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  box-sizing: border-box;
  gap: 40px;
}

.caja-narrador {
  background: #000;
  border: 5px solid white;
  border-radius: 16px;
  padding: 36px 40px;
  max-width: 560px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.5);
}

.icono-narrador {
  font-size: 4rem;
  color: var(--color-rojo);
}

.titulo-narrador {
  font-family: var(--font-cinzel);
  font-size: 3rem;
  font-weight: 700;
  color: var(--color-rojo);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  text-align: center;
  margin: 0;
}

.consejos {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.consejo {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.icono-consejo {
  color: var(--color-dorado);
  font-size: 1rem;
  flex-shrink: 0;
  margin-top: 3px;
}

.consejo-texto {
  font-family: var(--font-raleway);
  font-weight: 700;
  color: white;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
}

.cargando {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 8px;
}



.texto-cargando {
  font-family: var(--font-raleway);
  font-style: italic;
  color: #aaa;
  font-size: 0.9rem;
  margin: 0;
}




</style>