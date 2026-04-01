<template>
  <div class="contenedor-resultados" :class="victoria ? 'fondo-victoria' : 'fondo-derrota'">

    <div class="cuadro" :class="victoria ? 'cuadro-victoria' : 'cuadro-derrota'">

      <template v-if="esNarrador">
        <h1 class="titulo titulo-narrador">
          Los ganadores son:
          <span :class="bandoGanador === 'lobo' ? 'texto-lobos' : 'texto-aldeanos'">
            {{ nombreBando(bandoGanador) }}
          </span>
        </h1>
        <p class="subtitulo subtitulo-narrador">¡Enhorabuena a los ganadores!</p>
      </template>

      <template v-else>
        <h1 class="titulo" :class="victoria ? 'titulo-victoria' : 'titulo-derrota'">
          <span v-if="victoria">VICTORIA <i class="fa-regular fa-face-grin-beam"></i></span>
          <span v-else>DERROTA <i class="fa-regular fa-face-dizzy"></i></span>
        </h1>

        <p class="subtitulo" :class="victoria ? 'subtitulo-victoria' : 'subtitulo-derrota'">
          <span v-if="victoria">
            ¡Enhorabuena! ¡¡El equipo de {{ nombreBando(bandoGanador) }} se ha alzado con la victoria!!
          </span>
          <span v-else>
            ¡Lo sentimos! El equipo de {{ nombreBando(bandoGanador) }} ha ganado la partida...
          </span>
        </p>
      </template>

      <img
        :src="imagenCelebracion"
        :alt="bandoGanador"
        class="imagen-celebracion"
        :class="victoria || esNarrador ? 'imagen-victoria' : 'imagen-derrota'"
      />

      <div class="botones">
        <button class="btn-resultado" @click="irInicio">INICIO</button>
        <button class="btn-resultado" @click="irOtraPartida">¡OTRA PARTIDA!</button>
      </div>

    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import aldeanoscelebrandoImg from '@/assets/imgs/aldeanoscelebrando.jpg'
import loboscelebrandoImg from '@/assets/imgs/loboscelebrando.jpg'

export default {
  name: 'ResultadosView',

  computed: {
    ...mapGetters('sala', ['bandoGanador', 'miBando']),
    ...mapGetters('auth', ['nombre']),
    esNarrador() {
      return this.$store.getters['sala/esCreador']
    },

    victoria() {
      if (this.esNarrador) return true
      if (!this.bandoGanador || !this.miBando) return false
      return this.bandoGanador.toLowerCase() === this.miBando.toLowerCase()
    },

    imagenCelebracion() {
      if (!this.bandoGanador) return aldeanoscelebrandoImg
      return this.bandoGanador.toLowerCase() === 'lobo'
        ? loboscelebrandoImg
        : aldeanoscelebrandoImg
    },
  },

  methods: {
    nombreBando(bando) {
      const nombres = { lobo: 'Los Lobos', aldea: 'Los Aldeanos', narrador: 'El Narrador'}
      return nombres[bando?.toLowerCase()] || bando
    },

    irInicio() {
      this.$store.dispatch('sala/salir')
      this.$router.push({ name: 'inicio' })
    },

    irOtraPartida() {
      this.$store.dispatch('sala/salir')
      this.$router.push({ name: 'sala' })
    },
  },
}
</script>

<style scoped>
.contenedor-resultados {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  background-size: cover;
  background-position: center;
}

.fondo-victoria { background-image: url('@/assets/imgs/fondovictoria.png'); }
.fondo-derrota { background-image: url('@/assets/imgs/fondonoche.png'); }

.cuadro {
  width: 95%;
  max-width: 800px;
  background-image: url('@/assets/imgs/mesa.jpg');
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.cuadro::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  border-radius: 12px;
  pointer-events: none;
}

.cuadro-victoria { border: 8px solid white; }
.cuadro-derrota { border: 8px solid #cc0000; }

.titulo,
.subtitulo,
.imagen-celebracion,
.botones {
  position: relative;
  z-index: 1;
}

.titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(2rem, 6vw, 4rem);
  text-align: center;
  margin: 24px 15px 8px;
  width: calc(100% - 30px);
  box-sizing: border-box;
}

.titulo-victoria { color: #e4ba03; }
.titulo-derrota { color: #cc0000; }

.titulo-narrador {
  color: #e4ba03;
  font-size: clamp(1.4rem, 4vw, 2.5rem);
}

.texto-lobos { color: #cc0000; }
.texto-aldeanos { color: #2d9e2d; }

.subtitulo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: clamp(0.9rem, 2vw, 1.2rem);
  text-align: center;
  margin: 0 15px 16px;
  font-weight: 700;
}

.subtitulo-victoria { color: #e4ba03; }
.subtitulo-derrota { color: #cc0000; }
.subtitulo-narrador { color: #e4ba03; }

.imagen-celebracion {
  width: 100%;
  display: block;
  object-fit: cover;
  max-height: 360px;
}

.imagen-victoria { border-top: 3px solid #e4ba03; border-bottom: 3px solid #e4ba03; }
.imagen-derrota { border-top: 3px solid #cc0000; border-bottom: 3px solid #cc0000; }

.botones {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  padding: 24px 15px;
}

.btn-resultado {
  background: #cc0000;
  color: white;
  border: none;
  padding: 14px 28px;
  border-radius: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.15s ease;
}

.btn-resultado:hover {
  background: white;
  color: #cc0000;
}

.btn-resultado:active {
  transform: scale(0.95);
}

@media (max-width: 600px) {
  .botones {
    flex-direction: column;
    align-items: stretch;
  }
  .btn-resultado {
    text-align: center;
  }
}
</style>