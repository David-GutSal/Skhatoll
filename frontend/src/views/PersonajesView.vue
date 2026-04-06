<template>
  <main class="personajes py-5">
    <div class="personajes-inner container">

      <section class="seccion-intro bg-dark rounded-3 p-4 p-md-5 mb-5">
        <div class="row g-4 align-items-stretch">

          <div class="col-lg-7 d-flex flex-column justify-content-center">
            <h1 class="intro-titulo text-center text-lg-start mb-4">PERSONAJES</h1>
            <p class="intro-texto">
              En Los Hombres Lobo de Castronegro, cada jugador recibe un rol secreto que define su bando y su forma de jugar. La partida se convierte en un intenso debate donde la confianza, la mentira y la deducción son las principales armas. Cada rol tiene habilidades únicas que pueden cambiar radicalmente el curso del juego.
            </p>
            <p class="intro-texto">Cada personaje tiene un rasgo característico que puede ayudar a los jugadores a inspirarse para interpretar su rol. Esto es una mera guía, lo importante siempre es echarle imaginación y pasárselo bien.</p>
            <p class="intro-texto">- Existen tres grandes grupos de personajes:</p>

            <ul class="bandos-lista ps-4">
              <li>
                <span 
                  class="bando-nombre bando-bien-texto bando-link" 
                  @click="irABando('bien')"
                >
                  El bando del Bien, Los Aldeanos:
                </span>
                <span class="intro-texto"> Su objetivo es descubrir e identificar a los hombres lobo para salvar a la aldea. Tienen que buscar el bien común y que se salve al mayor número de gente. Entre ellos hay aldeanos con poderes especiales que podrán usar para combatir el mal.</span>
              </li>
              <li>
                <span 
                  class="bando-nombre bando-mal-texto bando-link" 
                  @click="irABando('mal')"
                >
                  El bando del Mal, Los Hombres Lobo:
                </span>
                <span class="intro-texto"> Una minoría que debe eliminar a los aldeanos sin ser descubiertos, mintiendo y manipulando durante el día. Antes eran simples aldeanos, pero una maldición los convirtió en las bestias que son ahora.</span>
              </li>
              <li>
                <span 
                  class="bando-nombre bando-neutral-texto bando-link" 
                  @click="irABando('neutral')"
                >
                  Ambiguos y solitarios:
                </span>
                <span class="intro-texto"> Son una minoría de personajes que añaden a las partidas incertidumbre y un mayor número de estrategias. Los Ambiguos serían personajes que si se cumplen ciertas condiciones, pueden cambiar de un bando a otro. Su objetivo principal es que gane el bando al que se han unido. Los personajes Solitarios serían aquellos que no pertenecen claramente a ningún bando o su victoria depende de condiciones especiales, la mayoría de ellos tienen unos objetivos totalmente ajenos a los que tienen los aldeanos y los lobos.</span>
              </li>
            </ul>
          </div>

          <div class="col-lg-5">
            <img 
              src="@/assets/imgs/intropersonajes.jpg" 
              alt="Personajes - Los Hombres Lobo de Castronegro" 
              class="intro-img img-fluid h-100 w-100"
            >
          </div>
        </div>
      </section>

      <section class="seccion-narrador bg-dark rounded-3 p-4 p-md-5 mb-5">
        
        
        <div class="row g-4 align-items-stretch">
           
          <div class="col-lg-5">
            <img 
              src="@/assets/imgs/narrador.png" 
              alt="El Narrador" 
              class="narrador-img img-fluid h-100 w-100"
            >
          </div>

          <div class="col-lg-7 d-flex flex-column justify-content-center">
            <h2 class="narrador-titulo text-center mb-4">EL NARRADOR</h2>
            <p class="intro-texto mb-4">
              Aunque en las reglas parece un simple árbitro, los autores del juego siempre han defendido que el narrador es el personaje más importante. Es el encargado de darle vida y amenizar la partida además de controlar que todo vaya como es debido. Tiene tanta importancia que, en festivales de juegos, se organizan sesiones donde el narrador usa música, velas y una interpretación teatral para convertir una simple partida de cartas en una experiencia de rol inmersiva.
            </p>

            <p class="intro-texto mb-2">
              <strong class="text-info">Habilidades de un buen narrador:</strong>
            </p>
            <p class="intro-texto">
              Debe ser imparcial, claro al hablar y capaz de mantener la tensión y el suspense durante toda la partida. Conoce perfectamente todos los roles y sus habilidades, gestiona bien el tiempo de las rondas y sabe crear atmósfera sin dar pistas.
            </p>
            <p class="intro-texto"> Además, observa el estado de los jugadores y resuelve dudas con rapidez y discreción, haciendo que la experiencia sea justa, dinámica y divertida para todos.</p>
          </div>
        </div>
      </section>
      <ListaPersonajes ref="listaPersonajes" />
    </div>
  </main>
</template>

<script>
import ListaPersonajes from '@/components/juego/ListaPersonajes.vue'

export default {
  name: 'PersonajesView',
  components: { ListaPersonajes },

  methods: {
    irABando(bando) {

      if (this.$refs.listaPersonajes) {
        this.$refs.listaPersonajes.abrirBando(bando);
      }

      this.$nextTick(() => {
        setTimeout(() => {
          const el = document.getElementById('lista-' + bando);
          if (el) {
            el.scrollIntoView({ 
              behavior: 'smooth', 
              block: 'start',
              inline: 'nearest'
            });
          }
        }, 100);
      });
    },
  },
}
</script>

<style scoped>

.intro-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.5rem;
  font-weight: bolder;
  color: white;
}

.narrador-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: #4a90d9;
  text-align: left !important;
}

.text-info {
  color: #4a90d9 !important;
  font-size: 1.5rem;
}

.intro-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  color: white;
  line-height: 1.6;
}

.bandos-lista {
  list-style: square;
  color: #e4ba03;
  font-size: 1.5rem;
}

.bando-nombre {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  font-weight: 700;
}

.bando-link {
  cursor: pointer;
  transition: color 0.2s ease;
}

.bando-link:hover {
  color: #e4ba03 !important;
}

.bando-bien-texto { color: #2d9e2d; }
.bando-mal-texto { color: #cc0000; }
.bando-neutral-texto { color: #868686; }

.intro-img {
  border: 5px solid #e4ba03;
  border-radius: 15px;
  object-fit: cover;
}

.narrador-img {
  border: 4px solid #4a90d9;
  border-radius: 15px;
  object-fit: cover;
}

@media (max-width: 992px) {
  .intro-titulo { font-size: 2.1rem; }
  .narrador-titulo { font-size: 1.8rem; }
}

@media (max-width: 576px) {
  .intro-titulo { font-size: 1.65rem; }
  .intro-texto { font-size: 1.05rem; }
}
</style>