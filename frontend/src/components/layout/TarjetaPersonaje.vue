<template>
  <div class="tarjeta mb-4" :style="{ borderColor: color }">
    <div v-if="personaje.nombre === 'El Niño Salvaje'" class="flip-card">
      <div class="flip-card-inner">
        <div class="flip-card-front">
          <img :src="personaje.imagen" :alt="personaje.nombre" class="tarjeta-img" />
        </div>
        <div class="flip-card-back">
          <div class="tarjeta-img-container">
            <img src="@/assets/imgs/carta-ninnolobo.jpg" alt="Niño Lobo" class="tarjeta-img" />
          </div>
        </div>
      </div>
    </div>

    <div v-else>
      <img :src="personaje.imagen" :alt="personaje.nombre" class="tarjeta-img" />
    </div>

    <div class="tarjeta-info">
      <h3 class="tarjeta-nombre" :style="{ color: color }">
        {{ personaje.nombre }}
      </h3>

      <hr class="tarjeta-linea" :style="{ borderColor: color }" />

      <p class="tarjeta-bando" :style="{ color: color }">
        {{ bando }}
      </p>

      <p class="tarjeta-subtitulo">
        Rasgo característico: <em>{{ personaje.subtitulo }}</em>
      </p>

      <p class="tarjeta-descripcion">
        {{ personaje.descripcion }}
      </p>

      <p class="tarjeta-poderes">
        <span class="poderes-label">Poderes:</span>
        {{ personaje.poderes }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TarjetaPersonaje',
  props: {
    personaje: { type: Object, required: true },
    color: { type: String, required: true },
    bando: { type: String, required: true },
  },
}
</script>

<style scoped>
.tarjeta {
  display: flex;
  background: #3d3d3d;
  border-radius: 15px;
  border: 6px solid;
  overflow: hidden;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.9);
  transition: all 0.3s ease;
}

.tarjeta:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 35px rgba(190, 190, 190, 0.6);
}

.tarjeta-img {
  width: 360px;
  min-width: 360px;
  height: 480px;
  object-fit: contain;
  image-rendering: crisp-edges;
}

.tarjeta-img-container {
  width: 360px;
  min-width: 360px;
  height: 480px;
  background: #3d3d3d;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  box-sizing: border-box;
}

.flip-card {
  perspective: 1500px;
  width: 360px;
  min-width: 360px;
  height: 480px;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.8s cubic-bezier(0.23, 1, 0.32, 1);
  transform-style: preserve-3d;
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front,
.flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  background: #3d3d3d;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  box-sizing: border-box;
}

.flip-card-back {
  transform: rotateY(180deg);
}

.tarjeta-info {
  background: #000000;
  padding: 35px 30px;
  flex: 1;
}

.tarjeta-nombre {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.95rem;
  font-weight: 700;
  margin-bottom: 12px;
}

.tarjeta-linea {
  border-top: 2px solid;
  margin: 16px 0;
  opacity: 0.7;
}

.tarjeta-bando {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.1rem;
  font-style: italic;
  margin-bottom: 14px;
}

.tarjeta-subtitulo,
.tarjeta-descripcion,
.tarjeta-poderes {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.05rem;
  line-height: 1.65;
  color: #ddd;
}

.poderes-label {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  color: #e4ba03;
}

@media (max-width: 992px) {
  .tarjeta {
    flex-direction: column;
  }

  .tarjeta-img,
  .flip-card {
    width: 100%;
    min-width: unset;
    height: 420px;
  }

  .flip-card-front,
  .flip-card-back,
  .tarjeta-img-container {
    padding: 0;
    background: #3d3d3d !important;
    width: 100%;
    height: 100%;
  }
}

@media (max-width: 576px) {
  .tarjeta-info {
    padding: 25px 20px;
  }

  .tarjeta-img {
    object-fit: contain;
    image-rendering: crisp-edges;
    border-right: none;
  }
}
</style>
