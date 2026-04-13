<template>
  <div class="cabecera">

    <!-- Columna izquierda: Nombre del jugador + Narrador -->
    <div class="columna-izquierda">
      <div class="nombre-box" :class="esDia ? 'nombre-dia' : 'nombre-noche'">
        <i :class="esNarrador ? 'fa-solid fa-book-open-reader' : 'fa-solid fa-person'"></i>
        <span>{{ esNarrador ? 'Narrador' : 'Jugador' }}: {{ nombreJugador }}</span>
      </div>

      <!-- Cuadro del Narrador - Solo para jugadores normales -->
      <div 
        v-if="!esNarrador && nombreNarrador"
        class="narrador-box"
        :class="esDia ? 'narrador-dia' : 'narrador-noche'"
      >
        <i class="fa-solid fa-book-open-reader"></i>
        <span>El Narrador es: <strong>{{ nombreNarrador }}</strong></span>
      </div>
    </div>

    <!-- Carta de fase (derecha) -->
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
    nombreNarrador: { type: String, default: '' },
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
  gap: 20px;
  flex-wrap: wrap;
}

.columna-izquierda {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-width: 280px;
}

/* Caja del nombre del jugador */
.nombre-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 12px;
  width: 400px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.3rem;
  font-weight: 700;
  color: #cc0000;
}

.nombre-dia {
  background: #000;
}

.nombre-noche {
  background: white;
}

/* Caja del Narrador - mismo ancho que la del nombre */
.narrador-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-radius: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.05rem;
  font-weight: 700;
  width: 400px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  border: 2px solid;
}

.narrador-dia {
  background: rgba(0, 0, 0, 0.82);
  border-color: #e4ba03;
  color: #ffdd57;
}

.narrador-noche {
  background: rgba(0, 0, 0, 0.85);
  border-color: #cc0000;
  color: #ff7777;
}

.narrador-box i {
  font-size: 26px;
  min-width: 30px;
}

/* Carta de fase */
.carta-fase {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 15px;
  width: clamp(140px, 22vw, 270px);
  flex-shrink: 0;
  min-width: 210px;
  margin-left: 23px;
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
  font-size: clamp(0.85rem, 1.2vw, 1.2rem);
  text-align: center;
  line-height: 1.4;
  margin: 0;
  font-style: italic;
}

.carta-dia .carta-fase-texto { color: #e4ba03; }
.carta-noche .carta-fase-texto { color: #cc0000; }

@media (max-width: 750px) {
  .cabecera {
    flex-direction: column;
    align-items: center;
  }
  .columna-izquierda {
    width: 100%;
    min-width: auto;
    align-items: center;
  }
  .nombre-box,
  .narrador-box {
    width: 100%;
    justify-content: center;
  }
}
</style>