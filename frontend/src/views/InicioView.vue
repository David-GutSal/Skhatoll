<template>
  <main class="inicio">

    <section class="hero-wrapper">
      <div class="hero-img-container">
        <div class="hero-bg"></div>
        <img src="@/assets/imgs/logo-provisional.png" alt="Skhatoll" class="hero-logo" />
      </div>
    </section>

    <section class="bloques container-fluid px-0">
      <div class="bloques-inner">

        <aside class="bloque bloque-lateral bloque-izq">
          <h2 class="bloque-titulo-principal">Los Hombres Lobo de Castronegro</h2>

          <article class="noticia">
            <img src="@/assets/imgs/img-bloqizq1.jpg" alt="Sobre el juego" class="noticia-img" />
            <h3 class="noticia-titulo">Sobre el juego</h3>
            <p class="noticia-texto">
              Los Hombres Lobo de Castronegro es un juego de roles ocultos orientado a un público a
              partir de 10 años y para entre 8 y 28 jugadores (ampliable con las expansiones).
            </p>
            <p class="noticia-texto">
              <span class="texto-rojo"
                >Es un juego de suspense en el que los jugadores se dividen en 2 facciones rivales,
                aldeanos y lobos, que bajo la supervisión de un narrador tendrán que buscar cómo
                eliminar al equipo contrario.</span
              >
            </p>
            <p class="noticia-texto">
              El juego está ambientado en la aldea de Castronegro en la que una maldición ha
              convertido a algunos aldeanos en hombres lobo. Los aldeanos supervivientes deberán
              averiguar cuáles de sus vecinos se han convertido en hombres lobo para acabar con la
              maldición y no ser infectados o devorados.
            </p>
          </article>

          <article class="noticia">
            <img
              src="@/assets/imgs/empatadoscelebrando.jpg"
              alt="Roles ocultos"
              class="noticia-img"
            />
            <h3 class="noticia-titulo">¿Qué son los juegos de roles ocultos?</h3>
            <p class="noticia-texto">
              Un juego de roles ocultos es un tipo de juego de mesa donde los jugadores reciben
              identidades secretas o bandos (usualmente bien contra mal) al inicio de la partida.
            </p>
            <p class="noticia-texto">
              El objetivo principal es deducir los roles de los demás a través de la interacción,
              deducción y engaño, trabajando para cumplir metas secretas y ganar como equipo. A
              veces también hay metas individuales por lo que los jugadores tendrán que hacer uso de
              todas sus habilidades sociales para alzarse con la victoria.
            </p>
            <p class="noticia-texto">
              Los juegos de roles ocultos son recomendables para todo tipo de edades ya que fomentan
              la creatividad y mejoran las habilidades comunicativas.
            </p>
          </article>

          <article class="noticia">
            <img src="@/assets/imgs/img-bloqizq3.jpg" alt="El Pacto" class="noticia-img" />
            <h3 class="noticia-titulo">Juego completo: El Pacto</h3>
            <p class="noticia-texto">
              Si te gusta el juego y quieres tenerlo en formato físico, te recomendamos la versión
              de <span class="texto-rojo">Los Hombres Lobo de Castronegro: El Pacto.</span>
            </p>
            <p class="noticia-texto">
              Esta edición es la recopilación definitiva de la franquicia, ya que integra en una
              sola caja el juego base de Los Hombres Lobo de Castronegro junto con sus tres
              expansiones oficiales: Luna Nueva, La Aldea y Personajes.
            </p>
            <p class="noticia-texto">
              A diferencia de la versión estándar, cuenta con materiales de mayor calidad e incluye
              todos los componentes necesarios para jugar hasta con 47 personas, tales como los
              edificios de la aldea, las cartas de evento y los nuevos roles especiales con
              habilidades únicas.
            </p>
          </article>

          <article class="noticia">
            <img
              src="@/assets/imgs/aldeanoscelebrando.jpg"
              alt="Aprender a jugar"
              class="noticia-img"
            />
            <h3 class="noticia-titulo">¿Es fácil de aprender a jugar?</h3>
            <p class="noticia-texto">
              <span class="texto-rojo"
                >Es sencillo de aprender porque su mecánica principal se basa en el debate y la
                deducción guiada por un narrador, pero requiere dominar habilidades como la
                capacidad de observación.</span
              >
            </p>
            <p class="noticia-texto">
              Para jugar bien es esencial dominar tres habilidades clave: persuasión (para defender
              tu inocencia), lógica (para descubrir a los lobos) y control emocional bajo presión.
            </p>
            <p class="noticia-texto">
              Además, resulta fundamental entender el funcionamiento de los roles especiales, ya que
              sus poderes pueden cambiar el curso de la partida, y conocer bien la estructura de los
              turnos. Si quieres aprender las reglas completas del juego, haz clic en el siguiente
              botón.
            </p>
            <router-link to="/reglas" class="btn-bloque btn-rojo">REGLAS</router-link>
          </article>
        </aside>

        <div class="bloque bloque-central">
          <div class="cuadro-texto">
            <h2 class="titulo-skhatoll">SKHATOLL</h2>
            <h3 class="cuadro-titulo">¿Qué es Skhatoll? ¿Cuál es su objetivo?</h3>
            <p class="cuadro-parrafo bold">
              Skhatoll es una plataforma digital que busca ayudar a los jugadores de Los Hombres
              Lobo de Castronegro a gestionar sus partidas de una manera más cómoda, intuitiva y
              segura, evitando ciertas trampas o errores que pueden cometerse en partidas reales.
            </p>
            <p class="cuadro-parrafo">
              La idea surgió porque, al jugar en formato físico, los roles se revelaban
              accidentalmente con frecuencia: las cartas se caían, se mostraban sin querer al
              manipularlas o los jugadores olvidaban su rol, ya que las cartas no incluyen
              descripción. Esto obligaba a preguntar al narrador y permitía que otros escucharan o
              intuyeran información.
            </p>
            <img
              src="@/assets/imgs/footer-dia.png"
              alt="Pueblo de Castronegro"
              class="img-centro"
            />
            <p class="cuadro-parrafo rojo-centro">
              Es decir, es una manera más “segura” de jugar al juego sin dar pistas accidentales,
              ocultar mejor tu rol y servir de ayuda al narrador para revisar qué papel interpreta
              cada jugador o revisar reglas.
            </p>
            <p class="cuadro-parrafo">
              Como añadido a la sala de juegos, Skhatoll incluye una sección de reglas y otra de
              personajes para orientar a narradores y jugadores en dudas que puedan surgir durante
              las partidas.
            </p>
            <h3 class="cuadro-titulo">La visión de futuro de Skhatoll</h3>
            <p class="cuadro-parrafo">
              La plataforma tiene un gran potencial evolutivo que le permitiría ofrecer una mayor
              cantidad de servicios y funcionalidades a los usuarios. Algunas de las mejores
              implementaciones podrían ser:
            </p>
            <ul>
              <li>
                <span class="rojo-centro">Versión online:</span> permitiría a los usuarios jugar
                partidas desde sus casas, sin necesidad de reunirse. Complementario a esto se
                podrían añadir funcionalidades como un chat privado, temporizadores de partida o un
                narrador automático.
              </li>
              <li>
                <span class="rojo-centro">Implementar expansiones:</span> el juego tiene varias
                expansiones y algunas de ellas todavía no tienen una versión online. Estas
                expansiones ayudan a amenizar las partidas y darles un toque más original como la de
                <span class="rojo-centro">La Aldea</span> en la que los edificios también son
                personajes o <span class="rojo-centro">Luna Nueva</span> donde cada noche ocurre
                algo nuevo...
              </li>
              <li>
                <span class="rojo-centro">Multiplataforma de juegos:</span> podrían añadirse otros
                juegos de mesa y/o de roles ocultos a la sala de juegos de características parecidas
                como Blood on the Clocktower, Samurái Sword o Bang!
              </li>
            </ul>
          </div>

          <div class="slide-fijo-wrapper">
            <div class="slide-fijo"></div>
            <div class="overlay-cuadro">
              <h3 class="overlay-titulo">¿UNA PARTIDA?</h3>
              <router-link to="/sala" class="btn-sala-juegos">SALA DE JUEGOS</router-link>
            </div>
          </div>

          <div class="cuadro-texto">
            <h3 class="cuadro-titulo">Origen del nombre de Skhatoll</h3>
            <p class="cuadro-parrafo">
              Skhatoll proviene de la combinación de los nombres de 2 personajes de la mitología
              nórdica: <span class="rojo-centro">Sköll y Hati.</span> Estos personajes son 2 lobos
              hermanos hijos de Fenrir, un lobo monstruoso que representa el caos y la destrucción
              que está destinado a matar al dios Odín durante el Ragnarök, el fin del mundo de la
              mitología nórdica.
            </p>
            <p class="cuadro-parrafo">
              La función de sus hijos es la de
              <span class=" cuadro-parrafo rojo-centro">perseguir a los 2 grandes "Astros Celestiales"</span> por lo
              que uno persigue a la Luna y otro al Sol. Los lobos corren detrás de ellos día y
              noche, haciendo que los astros huyan aterrorizados hasta que, en el día del comienzo
              del Ragnarök, los alcanzarán y devorarán. Tras esto el día y la noche se volverán
              oscuros y se caerán todas las estrellas lo que indicará el comienzo del fin del mundo
              y la liberación de su padre.
            </p>
            <p class="cuadro-parrafo rojo-centro">
              En la mitología nórdica este mito explicaba por qué el Sol y la Luna se mueven
              constantemente por el cielo y además es una metáfora de lo que ocurre en el juego de
              los Hombres Lobo de Castronegro donde según avanzan los días van desapareciendo los
              habitantes de la aldea por los ataques de los lobos.
            </p class="cuadro-parrafo">
          </div>

          <img src="@/assets/imgs/fenrirehijos.jpg" alt="Fenrir e hijos" class="img-fenrir" />
        </div>

        <aside class="bloque bloque-lateral bloque-der">
          <h2 class="bloque-titulo-principal">¿Sabías que ...?</h2>

          <article class="noticia">
            <img
              src="@/assets/imgs/loboscelebrando.jpg"
              alt="Lobos y Mafiosos"
              class="noticia-img"
            />
            <h3 class="noticia-titulo">Lobos y Mafiosos</h3>
            <p class="noticia-texto">
              El juego de Los Hombres Lobo de Castronegro es una adaptación de un juego tradicional
              de psicología social llamado Mafia, creado en 1986 por el profesor de psicología ruso
              Dimitry Davidoff en la Universidad de Moscú. Davidoff diseñó la mecánica para
              demostrar cómo una minoría informada (la mafia) puede superar a una mayoría
              desinformada (los inocentes) mediante el engaño y la manipulación.
            </p>
            <p class="noticia-texto">
              La versión temática que conocemos hoy fue publicada originalmente en 2001 por los
              autores franceses Philippe des Pallières y Hervé Marly. Ellos sustituyeron la temática
              de gánsteres por la leyenda de los licántropos.
            </p>
          </article>

          <article class="noticia">
            <img src="@/assets/imgs/carta-ninna.jpg" alt="La Niña Tramposa" class="noticia-img" />
            <h3 class="noticia-titulo">La Niña Tramposa</h3>
            <p class="noticia-texto">
              Este personaje surgió de una anécdota real que ocurría en las partidas caseras,
              siempre había alguien que intentaba espiar a escondidas asi que los creadores
              decidieron convertir esa trampa en una habilidad oficial. Así nació La Niña, el único
              personaje que puede abrir los ojos durante el turno de los lobos… pero... ¡con riesgo!
              Si alguien la descubre, ¡es eliminada!
            </p>
            <p class="noticia-texto">
              La mayoría de los personajes del juego se inspiraron en cuentos clásicos y folclore
              europeo para buscar cómo equilibrar la partida entre aldeanos y lobos. Si quieres
              conocer a todos los personajes, haz clic en el siguiente botón.
            </p>
            <router-link to="/personajes" class="btn-bloque btn-dorado">PERSONAJES</router-link>
          </article>

          <article class="noticia">
            <img src="@/assets/imgs/img-bloqder1.jpg" alt="Licantropía" class="noticia-img" />
            <h3 class="noticia-titulo">El origen de la Licantropía</h3>
            <p class="noticia-texto">
              La leyenda de la licantropía tiene raíces milenarias que se remontan a la Antigua
              Grecia con el mito del Rey Licaón, castigado por Zeus a convertirse en lobo, origen
              del término actual. A lo largo de la historia, esta creencia se alimentó de factores
              reales como enfermedades mal comprendidas (la porfiria, la hipertricosis o la rabia) y
              el trastorno psicológico conocido como licantropía clínica.
            </p>
            <p class="noticia-texto">
              Además, el folclore europeo y el miedo ancestral a los lobos en las zonas boscosas
              consolidaron el mito a través de relatos sobre guerreros nórdicos y juicios reales
              durante la Inquisición. Todo esto consiguió transformar una figura mitológica en uno
              de los monstruos más icónicos de la cultura popular.
            </p>
          </article>

          <article class="noticia">
            <img src="@/assets/imgs/img-bloqder2.jpg" alt="Castronegro" class="noticia-img" />
            <h3 class="noticia-titulo">El nombre de Castronegro</h3>
            <p class="noticia-texto">
              El nombre del pueblo "Castronegro" (en francés, Thiercelieux), donde acontecen los
              hechos del juego de Los Hombres Lobo, no es un nombre inventado al azar. Es el nombre
              de la aldea real donde vive uno de los autores, Philippe des Pallières. Allí se
              encuentra su editorial, Lui-même, y es el lugar donde se testearon las primeras
              partidas del juego.
            </p>
            <p class="noticia-texto">
              Es una aldea pequeña situada en la región de Sena y Marne, a unos 60 kilómetros de
              París, comunicada y muy habitada, algo que contrasta totalmente con su versión
              ficticia, que se describe como una aldea remota, rodeada de bosques profundos y
              azotada por una maldición. El nombre "Castronegro" es la adaptación al español que
              busca mantener esa sonoridad de aldea medieval aislada y oscura.
            </p>
            <p class="noticia-texto">
              Como el pueblo es consciente del éxito del juego, tiene incluidas referencias por todo
              su territorio, lo que promueve el turismo y las visitas.
            </p>
          </article>
        </aside>
      </div>
    </section>
  </main>
</template>

<script>
export default {
  name: 'InicioView',
}
</script>

<style scoped>
.inicio { width: 100%; }

.hero-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 65px;
  margin-bottom: 50px;
}

.hero-img-container {
  position: relative;
  width: 85%;
  height: 390px;
  border: 5px solid #e4ba03;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background-image: url('@/assets/imgs/main-inicio.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

.hero-logo {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  height: 120px;
  object-fit: contain;
  z-index: 2;
}

.bloques {
  display: flex;
  justify-content: center;
  padding: 0 0 40px 0;
}

.bloques-inner {
  display: grid;
  grid-template-columns: 1fr 1.7fr 1fr;
  gap: 40px;
  width: 85%;
  align-items: start;
}

.bloque-lateral {
  background: rgba(0, 0, 0, 0.95);
  border-radius: 15px;
  padding: 25px;
}

.bloque-titulo-principal {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: #e4ba03;
  margin-bottom: 25px;
  text-align: center;
}

.noticia { margin-bottom: 50px; }

.noticia-img {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
  border: 3px solid #e4ba03;
  display: block;
  margin-bottom: 20px;
}

.noticia-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.8rem;
  font-weight: 700;
  color: #cc0000;
  margin-bottom: 8px;
}

.noticia-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  line-height: 1.6;
  margin-bottom: 10px;
}

.texto-rojo {
  color: #cc0000;
  font-weight: 600;
}

.btn-bloque {
  display: block;
  width: 100%;
  padding: 15px;
  text-align: center;
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.2s ease;
  margin-top: 35px;
  border: none;
  cursor: pointer;
}

.btn-rojo { background: #cc0000; color: white; }
.btn-rojo:hover { background: white; color: #cc0000; }

.btn-dorado { background: #e4ba03; color: white; }
.btn-dorado:hover { background: white; color: #e4ba03; }

.bloque-central {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.titulo-skhatoll {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 4rem;
  font-weight: 900;
  color: #e4ba03;
  text-align: center;
  margin-bottom: 25px;
}

.cuadro-texto {
  background: rgba(255, 255, 255, 0.8);
  border: 8px solid #e4ba03;
  padding: 32px;
}

.cuadro-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 800;
  color: #8b0000;
  margin: 15px 0;
}

.cuadro-parrafo {
  font-family: 'Raleway', Arial, sans-serif;
  color: #000000;
  line-height: 1.6;
  margin-bottom: 12px;
}

.bold { font-weight: 600; }
.rojo-centro { color: #8b0000; font-weight: 600; }

ul {
margin: 10px 0;
font-family: 'Raleway', Arial, sans-serif;
list-style: square;
}

li {
margin: 10px 0;
font-size: 0.95rem;
}

li::marker {
color: #e4ba03;
font-weight: bolder;
font-size: 1.2rem;
}

.img-centro {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
  display: block;
  margin: 30px 0;
}

.slide-fijo-wrapper {
  margin: 50px 0;
  overflow: hidden;
  height: 600px;
  border: 8px solid #e4ba03;
  position: relative;
}

.slide-fijo {
  width: 100%;
  height: 100%;
  background-image: url('@/assets/imgs/slide.jpeg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  box-shadow: inset 0 0 50px 20px rgba(0, 0, 0, 0.8);
}

.overlay-cuadro {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 78%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.85);
  border: 6px solid #e4ba03;
  padding: 45px 30px;
  text-align: center;
  z-index: 3;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.8);
}

.overlay-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.1rem;
  font-weight: 900;
  color: #8b0000;
  margin-bottom: 28px;
}

.btn-sala-juegos {
  display: inline-block;
  background: #8b0000;
  color: white;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.15rem;
  font-weight: 700;
  padding: 16px 40px;
  border-radius: 50px;
  text-decoration: none;
  transition: all 0.25s ease;
}

.btn-sala-juegos:hover {
  background: #000000;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(85, 0, 0, 0.3);
}

.btn-sala-juegos:active {
  transform: scale(0.93);
}

.img-fenrir {
  width: 100%;
  height: auto;
  border: 8px solid #e4ba03;
  display: block;
  margin-top: 50px;
}

@media (max-width: 1300px) {
  .bloques-inner {
    grid-template-columns: 1fr;
    width: 90%;
  }

  .bloque-central { order: -1; }

  
}

@media (max-width: 768px) {
  .slide-fijo-wrapper {
    height: 480px;
  }

  .overlay-cuadro {
    width: 85%;
    padding: 35px 25px;
  }

  .overlay-titulo {
    font-size: 1.85rem;
  }

  .btn-sala-juegos {
    padding: 14px 35px;
    font-size: 1.05rem;
  }
}

@media (max-width: 576px) {

  .bloques-inner {
    width: 95%;
  }

  .slide-fijo-wrapper {
    height: 420px;
    margin: 40px 0;
  }

  .overlay-cuadro {
    width: 88%;
    padding: 30px 20px;
  }

  .overlay-titulo {
    font-size: 1.7rem;
    margin-bottom: 20px;
  }

  .btn-sala-juegos {
    padding: 13px 32px;
    font-size: 1rem;
  }

  .titulo-skhatoll {
    font-size: 3.8rem;
  }
}
</style>
