<template>
  <div class="contenedor-resultados" :class="fondoClase">

    <div class="cuadro" :class="bordeClase">

      <template v-if="esEmpate">
        <h1 class="titulo titulo-empate">
          EMPATE <i class="fa-regular fa-face-meh-blank"></i>
        </h1>
        <p class="subtitulo subtitulo-empate">
          Upps... Hubo empate... ¡Habrá que echar otra partida para ver quién gana!
        </p>
      </template>

      <template v-else-if="esNarrador">
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
        :class="imagenBordeClase"
      />

      <div class="botones">
        <button class="btn-resultado" :class="esEmpate ? 'btn-empate' : ''" @click="irInicio">INICIO</button>
        <button class="btn-resultado" :class="esEmpate ? 'btn-empate' : ''" @click="irOtraPartida">¡OTRA PARTIDA!</button>
      </div>

    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import aldeanoscelebrandoImg from '@/assets/imgs/aldeanoscelebrando.jpg'
import loboscelebrandoImg from '@/assets/imgs/loboscelebrando.jpg'
import empatadoscelebrandoImg from '@/assets/imgs/empatadoscelebrando.jpg'

export default {
  name: 'ResultadosView',

  computed: {
    ...mapGetters('sala', ['bandoGanador', 'miBando']),
    ...mapGetters('auth', ['nombre']),

    esNarrador() {
      return this.$store.getters['sala/esCreador']
    },

    esEmpate() {
      return this.bandoGanador?.toLowerCase() === 'empate'
    },

    victoria() {
      if (this.esNarrador || this.esEmpate) return false
      if (!this.bandoGanador || !this.miBando) return false
      return this.bandoGanador.toLowerCase() === this.miBando.toLowerCase()
    },

    fondoClase() {
      if (this.esEmpate) return 'fondo-empate'
      return this.victoria || this.esNarrador ? 'fondo-victoria' : 'fondo-derrota'
    },

    bordeClase() {
      if (this.esEmpate) return 'cuadro-empate'
      return this.victoria || this.esNarrador ? 'cuadro-victoria' : 'cuadro-derrota'
    },

    imagenBordeClase() {
      if (this.esEmpate) return 'imagen-empate'
      return this.victoria || this.esNarrador ? 'imagen-victoria' : 'imagen-derrota'
    },

    imagenCelebracion() {
      if (this.esEmpate) return empatadoscelebrandoImg
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
.fondo-empate { background-image: url('@/assets/imgs/fondodia.png'); }

.cuadro {
  width: 95%;
  max-width: 800px;
  background-image: url('@/assets/imgs/mesa.jpg');
  background-size: cover;
  background-position: center;
  border-radius: 15px;
  overflow: hidden;
  position: relative;
}

.cuadro::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  pointer-events: none;
}

.cuadro-victoria { border: 8px solid white; }
.cuadro-derrota { border: 8px solid #cc0000; }
.cuadro-empate { border: 8px solid white; }

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
.titulo-empate { color: #4a90d9; }

.titulo-narrador {
  color: #e4ba03;
  font-size: clamp(2rem, 4vw, 2.5rem);
}

.texto-lobos { color: #cc0000; }
.texto-aldeanos { color: #2d9e2d; }

.subtitulo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: clamp(1.3rem, 2vw, 1.2rem);
  text-align: center;
  margin: 0 15px 16px;
  font-weight: 700;
}

.subtitulo-victoria { color: #e4ba03; }
.subtitulo-derrota { color: #cc0000; }
.subtitulo-narrador { color: #e4ba03; }
.subtitulo-empate { color: #4a90d9; }

.imagen-celebracion {
  width: 100%;
  display: block;
  object-fit: cover;
  max-height: 380px;
}

.imagen-victoria { border-top: 5px solid #e4ba03; border-bottom: 5px solid #e4ba03; }
.imagen-derrota { border-top: 5px solid #cc0000; border-bottom: 5px solid #cc0000; }
.imagen-empate { border-top: 5px solid #4a90d9; border-bottom: 5px solid #4a90d9; }

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

.btn-empate {
  background: #4a90d9;
}

.btn-empate:hover {
  background: white;
  color: #4a90d9;
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

  .subtitulo {
  font-size: clamp(0.9rem, 2vw, 0.9rem);
}
}
</style>