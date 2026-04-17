<template>
  <main class="reglas">
    <div class="reglas-inner">

      <!-- PARTE 1: Título e intro -->
      <section class="seccion-intro">
        <h1 class="intro-titulo">REGLAS Y JUEGOS</h1>
        <p class="intro-texto">En esta sección se recopilan las reglas principales del juego así como una guía de seguimiento de la partida y cosas a tener en cuenta. También hay una sección donde pueden verse expansiones y juegos similares a Los Hombres Lobo de Castronegro.</p>
      </section>

      <!-- PARTE 2: Fichas -->
      <section class="row g-3 mb-4">
        <div class="col-md-4">
          <div class="ficha">
            <img src="@/assets/imgs/medalla-alcalde.jpg" alt="Alcalde" class="ficha-img" />
            <h3 class="ficha-titulo">La figura del Alcalde / Alguacil</h3>
            <p class="ficha-texto">Este cargo se puede asignar a cualquier jugador. Los jugadores presentan sus candidaturas y después serán votados. Ganará el jugador con más votos. Su función principal es la de desempatar en las votaciones. El cargo de alguacil tiene las siguientes características:</p>
            <ul class="ficha-lista">
              <li>No se puede rechazar el honor de ser Alguacil.</li>
              <li>El voto de este jugador contará como doble.</li>
              <li>Si después de una votación hay un empate, el Alcalde decide quién será la víctima a linchar.</li>
              <li>Si el Alguacil es eliminado, antes de morir elige a su sucesor en el cargo.</li>
            </ul>
          </div>
        </div>
        <div class="col-md-4">
          <div class="ficha">
            <img src="@/assets/imgs/linchamiento.jpg" alt="Linchamiento" class="ficha-img" />
            <h3 class="ficha-titulo">Votación por linchamiento</h3>
            <p class="ficha-texto">Estas votaciones ocurren durante el día y son la manera más eficaz de los aldeanos de eliminar a los lobos. En las votaciones por linchamiento los jugadores son libres de argumentar quién piensan que es un lobo o defenderse a ellos mismos de una acusación. Tras los debates empezarán las votaciones en las que los jugadores votarán a los presuntos lobos (puedes votarte a ti mismo).</p>
            <p class="ficha-texto">En caso de empate será el alcalde el que decida qué persona será linchada. Esa persona linchada normalmente revelará su rol y quedará eliminada de la partida.</p>
          </div>
        </div>
        <div class="col-md-4">
          <div class="ficha">
            <img src="@/assets/imgs/diaynoche.jpg" alt="Fases del juego" class="ficha-img" />
            <h3 class="ficha-titulo">Las fases del juego</h3>
            <p class="ficha-texto">Cada turno del juego transcurre en 2 fases completas de día y noche siempre guiadas por un narrador omnisciente que sabrá los roles que interpreta cada personaje.</p>
            <p class="ficha-texto"><i class="fa-solid fa-moon icono-ficha"></i> <strong>Durante la noche:</strong> los jugadores cierran los ojos y son llamados por el narrador de forma ordenada para que usen su poderes o sean informados de algo. Solo cuando el narrador los llame podrán abrir los ojos.</p>
            <p class="ficha-texto"><i class="fa-solid fa-sun icono-ficha"></i> <strong>Durante el día:</strong> Los aldeanos abrirán los ojos y se revelarán las víctimas de la noche. Después debatirán y harán votaciones para eliminar a los lobos.</p>
          </div>
        </div>
      </section>

      <!-- PARTE 3: Tabla + Carrusel de consejos -->
      <section class="row g-3 mb-4">
        <!-- Tabla de jugadores -->
        <div class="col-md-6">
          <div class="seccion-tabla">
            <h2 class="tabla-titulo">Distribución de personajes en partidas</h2>
            <p class="tabla-texto">Esta tabla es una guía básica para equilibrar ambos bandos. Se utilizan los personajes básicos e indispensables del juego.</p>
            <p class="tabla-texto">Si se quieren meter más personajes siempre hay que buscar un equilibrio entre personajes buenos y malos teniendo en cuenta el poder de los personajes ambiguos.</p>
            <div class="tabla-wrapper">
              <table class="tabla-juego">
                <thead>
                  <tr class="fila-cabecera">
                    <th>Jugadores (sin narrador)</th>
                    <th>Lobos</th>
                    <th>Aldeanos</th>
                    <th>Vidente</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(fila, i) in tablaJugadores" :key="i" :class="i % 2 === 0 ? 'fila-gris' : 'fila-roja'">
                    <td>{{ fila[0] }}</td>
                    <td>{{ fila[1] }}</td>
                    <td>{{ fila[2] }}</td>
                    <td>{{ fila[3] }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- Carrusel de consejos -->
        <div class="col-md-6">
          <div class="seccion-consejos">
            <div class="tarjeta-consejo" :style="{ backgroundColor: tarjetaActual.color }">
              <h3 v-if="tarjetaActual.titulo" class="consejo-titulo" :style="{ color: tarjetaActual.colorTitulo }">{{ tarjetaActual.titulo }}</h3>
              <div class="consejo-textos">
                <p v-for="(parrafo, i) in tarjetaActual.textos" :key="i" class="consejo-parrafo" :style="{ color: tarjetaActual.colorTexto }">
                  <span class="cuadro-negro"></span>{{ parrafo }}
                </p>
              </div>
            </div>
            <div class="consejos-nav">
              <button class="btn-nav" @click="anteriorTarjeta">
                <i class="fa-solid fa-chevron-left"></i>
              </button>
              <span class="consejos-contador">{{ indiceTarjeta + 1 }} / {{ tarjetas.length }}</span>
              <button class="btn-nav" @click="siguienteTarjeta">
                <i class="fa-solid fa-chevron-right"></i>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- PARTE 4: Lista de reglas (componente compartido con narrador) -->
      <section class="mb-4">
        <ListaReglas />
      </section>

      <!-- PARTE 5: Carrusel de juegos -->
      <section class="seccion-juegos mb-4">
        <div class="juegos-header">
          <h2 class="juegos-titulo">Si te gustó Los Hombres Lobo de Castronegro te gustarán...</h2>
        </div>
        <div class="carrusel-wrapper">
          <button class="btn-carrusel" @click="anteriorJuego">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <div class="carrusel-pista" ref="carrusel">
            <div
              v-for="juego in juegos"
              :key="juego.titulo"
              class="juego-ficha"
              :style="{ backgroundImage: 'url(' + juego.imagen + ')' }"
            >
              <div class="juego-info">
                <a :href="juego.enlace" target="_blank" class="juego-titulo">{{ juego.titulo }}</a>
                <p class="juego-texto">{{ juego.texto }}</p>
              </div>
            </div>
          </div>
          <button class="btn-carrusel" @click="siguienteJuego">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </section>

    </div>
  </main>
</template>

<script>
import ListaReglas from '@/components/juego/ListaReglas.vue'
import imgBloodclock from '@/assets/imgs/juego-bloodclock.jpg'
import imgSamurai from '@/assets/imgs/juego-samurai.jpg'
import imgAsamblea from '@/assets/imgs/juego-asambleadelmal.jpg'
import imgAldea from '@/assets/imgs/juego-laaldea.jpg'
import imgUltimate from '@/assets/imgs/juego-ultimatewerewolf.jpg'
import imgLuna from '@/assets/imgs/juego-lunanueva.png'
import imgBang from '@/assets/imgs/juego-bang.jpg'

export default {
  name: 'ReglasView',
  components: { ListaReglas },

  data() {
    return {
      indiceTarjeta: 0,
      indiceJuego: 0,

      tablaJugadores: [
        [8,2,1,5],[9,2,1,6],[10,2,1,7],[11,2,1,8],
        [12,3,1,8],[13,3,1,9],[14,3,1,10],[15,3,1,11],
        [16,3,1,12],[17,3,1,13],[18,4,1,13],
      ],

      tarjetas: [
        {
          color: '#3a3a3a',
          colorTitulo: '#cc0000',
          colorTexto: '#cc0000',
          titulo: 'PREPARACIÓN Y CONSEJOS',
          textos: [],
        },
        {
          color: '#3a3a3a',
          colorTitulo: '#cc0000',
          colorTexto: 'white',
          titulo: 'PREPARACIÓN',
          textos: [
            'Los jugadores eligen un narrador que no juega pero guiará la partida. Este narrador repartirá las cartas e iniciará la partida.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: 'Consejos al narrador',
          textos: [
            'Tu papel es fundamental: de tu talento depende el ambiente del juego. Guiados por ti, los jugadores se van a divertir. No dudes en crear una atmósfera de angustia. Crea suspense cuando reveles las víctimas de los Hombres Lobo. Y aviva los debates si decaen.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: '',
          textos: [
            'Las frases de llamada nocturna de los personajes solo son ejemplos. Cuando domines bien la técnica, déjate llevar por tu propia inspiración.',
            'Con jugadores experimentados, aunque no seáis muchos, no dudes en utilizar los personajes con poderes especiales, porque añade interés a las partidas.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: '',
          textos: [
            'Con jugadores novatos, introduce poco a poco las otras cartas de personajes. Las partidas son relativamente cortas, jugad varias seguidas y variad los personajes.',
            'Cuando hables, ten cuidado de no dar pistas de la identidad de los personajes: "llamo al Vidente…eeh, digo, a La Vidente".',
            'Por la noche, cuando te dirijas a los personajes, no dirijas la voz hacia ellos; el resto de jugadores podría deducir su posición en la mesa.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: '',
          textos: [
            'Si los hombres lobo no consiguen ponerse de acuerdo o no se deciden, mala suerte para ellos, porque esa noche no habrá víctima.',
            'Cuando la Vidente espía, haz como que le das la vuelta a las cartas de todos los jugadores.',
            'Puedes esperar varios turnos antes de hacer elegir por votación al Alguacil, para dar tiempo a los jugadores a formarse una opinión de sus compañeros.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: 'Consejos para jugadores',
          textos: [
            'Los Hombres Lobo: una estrategia eficaz para librarse de una acusación es votar contra tu propio compañero. Hace falta no obstante que los aldeanos se den cuenta.',
            'La Vidente: cuidado, si has descubierto un Hombre Lobo, a lo mejor vale la pena descubrir tu identidad para acusar a un jugador, pero no demasiado pronto.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: '',
          textos: [
            'La Niña Pequeña: es un personaje muy poderoso, pero muy angustioso de jugar. No dudes en espiar: da miedo pero hay que aprovecharlo rápidamente antes de ser eliminado.',
            'El Cazador: siempre es bueno, en caso de acusación, hacerse pasar por el Cazador.',
            'Cupido: si te designas a ti mismo como enamorado, no escojas un compañero sentimental bocazas.',
          ],
        },
        {
          color: '#cc0000',
          colorTitulo: '#000',
          colorTexto: '#000',
          titulo: '',
          textos: [
            'El Alguacil: no dudes en presentarte al cargo de Alguacil, haz campaña. Pero si eres Hombre Lobo, no busques el puesto demasiado abiertamente. Muéstrate orgulloso de tu cargo, exhibe la carta en tu solapa.',
            'La Bruja: tu personaje va cobrando importancia a medida que se acerca el final de la partida. No desperdicies tus hechizos.',
          ],
        },
      ],

      juegos: [
        {
          titulo: 'Blood on the Clocktower',
          enlace: 'https://bloodontheclocktower.com/',
          imagen: imgBloodclock,
          texto: 'Un juego de deducción social para 5-20 jugadores ambientado en un pueblo victoriano. Ofrece roles únicos y la posibilidad de participar incluso después de morir, elevando la tensión hasta el final.',
        },
        {
          titulo: 'Samurai Sword',
          enlace: 'https://www.youtube.com/watch?v=udCXta6aW0E',
          imagen: imgSamurai,
          texto: 'Un juego de cartas para 3-7 jugadores ambientado en el Japón feudal donde samurái, shōgun, ninjas y rōnin luchan por el poder con roles ocultos y alianzas cambiantes.',
        },
        {
          titulo: 'La Asamblea del Mal',
          enlace: 'https://www.youtube.com/watch?v=aTOuLHM4LiU',
          imagen: imgAsamblea,
          texto: 'Un juego de roles ocultos para 5-10 jugadores donde los villanos intentan infiltrarse en el consejo de héroes. Combina deducción con habilidades especiales y votaciones dramáticas.',
        },
        {
          titulo: 'Los Hombres Lobo de Castronegro: La Aldea',
          enlace: 'https://es.scribd.com/document/444169570/La-Aldea-de-Castronegro',
          imagen: imgAldea,
          texto: 'La expansión oficial que añade nuevos personajes y escenarios al juego base. Introduce roles como el Seductor y el Oso, ampliando las posibilidades estratégicas de cada partida.',
        },
        {
          titulo: 'One Night Ultimate Werewolf',
          enlace: 'https://beziergames.com/collections/one-night-ultimate-werewolf',
          imagen: imgUltimate,
          texto: 'Una versión rápida e intensa donde toda la partida ocurre en una sola noche. Ideal para grupos de 3-10 jugadores que quieren disfrutar de múltiples partidas en poco tiempo.',
        },
        {
          titulo: 'Los Hombres Lobo de Castronegro: Luna Nueva',
          enlace: 'https://es.scribd.com/document/389540969/Hombres-Lobo-de-Castronegro-Luna-Nueva-Reglamento-Cartas',
          imagen: imgLuna,
          texto: 'La primera expansión del juego clásico que incorpora nuevos personajes con poderes únicos como el Ladrón y el Salvaje, añadiendo capas estratégicas y sorpresas a cada partida.',
        },
        {
          titulo: 'BANG!',
          enlace: 'https://www.youtube.com/watch?v=1gRPOZKz5Y4',
          imagen: imgBang,
          texto: 'Un clásico juego de cartas del Oeste para 2-7 jugadores con roles ocultos. El Sheriff, los Forajidos, el Renegado y los Ayudantes luchan en duelos de cartas llenos de acción y traición.',
        },
      ],
    }
  },

  computed: {
    tarjetaActual() {
      return this.tarjetas[this.indiceTarjeta]
    },
  },

  methods: {
    anteriorTarjeta() {
      this.indiceTarjeta = (this.indiceTarjeta - 1 + this.tarjetas.length) % this.tarjetas.length
    },
    siguienteTarjeta() {
      this.indiceTarjeta = (this.indiceTarjeta + 1) % this.tarjetas.length
    },
    anteriorJuego() {
      this.indiceJuego = Math.max(0, this.indiceJuego - 1)
      this.scrollCarrusel()
    },
    siguienteJuego() {
      this.indiceJuego = Math.min(this.juegos.length - 1, this.indiceJuego + 1)
      this.scrollCarrusel()
    },
    scrollCarrusel() {
      const carrusel = this.$refs.carrusel
      if (!carrusel) return
      const fichaAncho = carrusel.querySelector('.juego-ficha')?.offsetWidth + 16 || 0
      carrusel.scrollTo({ left: this.indiceJuego * fichaAncho, behavior: 'smooth' })
    },
  },
}
</script>

<style scoped>
.reglas {
  width: 100%;
  padding: 20px 0 40px;
}

.reglas-inner {
  width: 85%;
  margin: 0 auto;
}

/* PARTE 1 */
.seccion-intro {
  background: #000;
  border-radius: 15px;
  padding: 45px;
  text-align: center;
  margin-bottom: 16px;
}

.intro-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
}

.intro-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  line-height: 1.6;
  margin: 0;
}

/* PARTE 2: Fichas */
.ficha {
  background: #000;
  border-radius: 15px;
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.ficha-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border: 4px solid #cc0000;
  border-radius: 15px;
  margin-bottom: 20px;
}

.ficha-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #cc0000;
  text-align: center;
  margin-bottom: 10px;
}

.ficha-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.95rem;
  color: white;
  text-align: left;
  line-height: 1.6;
  margin-bottom: 8px;
}

.ficha-lista {
  list-style: square;
  padding-left: 20px;
  margin: 0;
}

.ficha-lista li {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.95rem;
  color: white;
  line-height: 1.6;
  margin-bottom: 4px;
}

.ficha-lista li::marker {
  color: #e4ba03;
}

.icono-ficha {
  color: #cc0000;
}

/* PARTE 3: Tabla */
.seccion-tabla {
  background: #000;
  border-radius: 15px;
  padding: 45px;
  height: 100%;
}

.tabla-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: #cc0000;
  text-align: center;
  margin-bottom: 16px;
}

.tabla-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  margin-bottom: 10px;
  line-height: 1.5;
}

.tabla-wrapper {
  background: #3a3a3a;
  border-radius: 15px;
  border: 4px solid #cc0000;
  overflow: hidden;
  padding: 0;
  margin-top: 16px;
}

.tabla-juego {
  width: 100%;
  border-collapse: collapse;
}

.fila-cabecera th {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  font-weight: 700;
  background: #e4ba03;
  padding: 12px 10px;
  text-align: center;
}

.tabla-juego td {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  font-weight: 700;
  color: white;
  padding: 10px;
  text-align: center;
}

.fila-gris { background: #3a3a3a; }
.fila-roja { background: #8b0000; }

/* PARTE 3: Consejos */
.seccion-consejos {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tarjeta-consejo {
  flex: 1;
  border-radius: 15px;
  border: 4px solid #e4ba03;
  padding: 45px;
  box-shadow: 0 0 15px rgba(255,255,255,0.1);
  min-height: 300px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consejo-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

.consejo-textos {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.consejo-parrafo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  line-height: 1.6;
  margin: 0;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.cuadro-negro {
  display: inline-block;
  width: 10px;
  height: 10px;
  min-width: 10px;
  background: #000;
  margin-top: 5px;
}

.consejos-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 16px 0 0;
}

.btn-nav {
  background: none;
  border: none;
  color: #e4ba03;
  font-size: 1.5rem;
  cursor: pointer;
  transition: transform 0.15s ease;
}

.btn-nav:hover {
  transform: scale(1.2);
}

.consejos-contador {
  font-family: 'Raleway', Arial, sans-serif;
  color: white;
  font-size: 0.9rem;
}

/* PARTE 5: Carrusel de juegos */
.seccion-juegos {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.juegos-header {
  background: #000;
  border-radius: 15px;
  padding: 35px;
  text-align: center;
}

.juegos-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  color: #e4ba03;
  margin: 0;
}

.carrusel-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-carrusel {
  background: none;
  border: none;
  color: #e4ba03;
  font-size: 2rem;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.15s ease;
}

.btn-carrusel:hover {
  transform: scale(1.2);
}

.carrusel-pista {
  display: flex;
  gap: 16px;
  overflow: hidden;
  flex: 1;
  scroll-behavior: smooth;
}

.juego-ficha {
  flex: 0 0 calc(33.33% - 11px);
  height: 280px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
  position: relative;
}

.juego-info {
  background: rgba(0, 0, 0, 0.85);
  border-radius: 10px;
  padding: 35px;
  width: 100%;
}

.juego-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.2rem;
  color: #e4ba03;
  text-decoration: none;
  display: block;
  margin-bottom: 8px;
  transition: font-size 0.2s ease, color 0.2s ease;
  cursor: pointer;
}

.juego-titulo:hover {
  font-size: 1.35rem;
  color: #cc0000;
}

.juego-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.85rem;
  color: white;
  font-weight: 700;
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 992px) {
  .reglas-inner { width: 90%; }
  .juego-ficha { flex: 0 0 calc(50% - 8px); }
}

@media (max-width: 600px) {
  .reglas-inner { width: 95%; }
  .seccion-intro { padding: 20px; }
  .intro-titulo { font-size: 1.6rem; }
  .intro-texto { font-size: 1rem; }
  .seccion-tabla { padding: 20px; }
  .tarjeta-consejo { padding: 20px; }
  .juego-ficha { flex: 0 0 100%; }
  .juegos-titulo { font-size: 1.3rem; }
}
</style>