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

<script>
import { FRASES } from '@/data/roles.js'

export default {
  name: 'EsperaNarradorView',

  data() {
    return {
      FRASES,
      fraseActual: 0,
      intervaloFrases: null,
      consejos: [
        'Lo más importante es que tanto tú como tus jugadores os divirtáis, sé un buen guía en la aventura.',
        'Ayuda a los jugadores a entender a sus personajes.',
        'Puedes ver lo que hace cada personaje consultando las reglas o haciendo click sobre su carta.',
        'Conviértete en un storyteller, no en un árbitro, sé totalmente neutral.',
        'Controla bien los tiempos y modula tu voz.',
      ],
    }
  },

  mounted() {
    this.intervaloFrases = setInterval(() => {
      this.fraseActual = (this.fraseActual + 1) % FRASES.length
    }, 8000)
    setTimeout(() => this.$router.push({ name: 'narrador' }), 5000)
  },

  beforeUnmount() {
    clearInterval(this.intervaloFrases)
  },
}
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
  color: #cc0000;
}

.titulo-narrador {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 3rem;
  font-weight: 700;
  color: #cc0000;
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
  color: #e4ba03;
  font-size: 1rem;
  flex-shrink: 0;
  margin-top: 3px;
}

.consejo-texto {
  font-family: 'Raleway', Arial, sans-serif;
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

.spinner {
  width: 28px;
  height: 28px;
  border: 3px solid rgba(255, 255, 255, 0.15);
  border-top-color: #cc0000;
  border-radius: 50%;
  animation: girar 0.9s linear infinite;
  flex-shrink: 0;
}

@keyframes girar {
  to { transform: rotate(360deg); }
}

.texto-cargando {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: #aaa;
  font-size: 0.9rem;
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
.fade-leave-active { transition: opacity 0.6s ease; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }

@media (max-width: 600px) {
  .caja-narrador {
    padding: 24px 20px;
    width: calc(100% - 40px);
    padding: 24px 20px;
  }
  .titulo-narrador {
    font-size: 2rem;
  }
  .icono-narrador {
    font-size: 3rem;
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
  .titulo-narrador {
    font-size: 1.6rem;
  }
  .logo-frase {
    display: none;
  }
}
</style>