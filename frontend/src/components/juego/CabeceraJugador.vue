<template>
  <div class="cabecera">

    <div class="nombre-box" :class="esDia ? 'nombre-dia' : 'nombre-noche'">
      <i :class="esNarrador ? 'fa-solid fa-book-open-reader' : 'fa-solid fa-person'"></i>
      <span>{{ esNarrador ? 'Narrador' : 'Jugador' }}: {{ nombreJugador }}</span>
    </div>

    <div class="carta-fase" :class="esDia ? 'carta-dia' : 'carta-noche'">
      <p class="carta-fase-titulo">{{ esDia ? 'EL DÍA' : 'LA NOCHE' }}</p>
      <img
        :src="esDia ? solImg : lunaImg"
        :alt="esDia ? 'Sol' : 'Luna'"
        class="carta-fase-img"
      />
      <p class="carta-fase-texto">
        {{ esDia
          ? 'La cálida luz del Sol ahuyenta a las bestias, pero no a las sospechas'
          : 'La clara luz de la luna ilumina a las bestias que acechan entre las sombras'
        }}
      </p>
    </div>

  </div>
</template>

<script>
import solImg from '@/assets/imgs/sol.jpg'
import lunaImg from '@/assets/imgs/luna.jpg'

export default {
  name: 'CabeceraJugador',
  props: {
    nombreJugador: { type: String, default: '' },
    esDia: { type: Boolean, default: true },
    esNarrador: { type: Boolean, default: false },
  },
  data() {
    return { solImg, lunaImg }
  },
}
</script>

<style scoped>
.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
  background: transparent;
}

.nombre-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 12px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #cc0000;
  align-self: flex-start;
}

.nombre-dia {
  background: #000;
  color: #cc0000;
}

.nombre-noche {
  background: white;
  color: #cc0000;
}

.carta-fase {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 15px;
  width: clamp(120px, 20vw, 260px);
  flex-shrink: 0;
}

.carta-dia {
  background: white;
  border: 8px solid #e4ba03;
}

.carta-noche {
  background: #000;
  border: 8px solid #cc0000;
}

.carta-fase-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(1.2rem, 3vw, 2.5rem);
  margin: 0;
  text-align: center;
}

.carta-dia .carta-fase-titulo { color: #e4ba03; }
.carta-noche .carta-fase-titulo { color: #cc0000; }

.carta-fase-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 10px;
}

.carta-dia .carta-fase-img { border: 5px solid #e4ba03; }
.carta-noche .carta-fase-img { border: 5px solid #cc0000; }

.carta-fase-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(0.6rem, 1.2vw, 0.95rem);
  text-align: center;
  line-height: 1.4;
  margin: 0;
  font-style: italic;
}

.carta-dia .carta-fase-texto { color: #e4ba03; }
.carta-noche .carta-fase-texto { color: #cc0000; }

@media (max-width: 600px) {
  .cabecera {
    flex-direction: column;
    align-items: center;
  }
  .carta-fase {
    width: 60%;
    max-width: 200px;
  }
  .nombre-box {
    font-size: 1rem;
    width: 100%;
    justify-content: center;
  }
}
</style>