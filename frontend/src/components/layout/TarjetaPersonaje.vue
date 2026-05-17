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

<script setup>
defineProps({
  personaje: { type: Object, required: true },
  color: { type: String, required: true },
  bando: { type: String, required: true },
})
</script>

<style scoped>
.tarjeta {
  display: flex;
  background: #3d3d3d;
  border-radius: 15px;
  border: 6px solid;
  overflow: hidden;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.9);
  transition: var(--transition-normal);
  align-items: stretch;
}

.tarjeta:hover {
  transform: translateY(-8px);
}

.tarjeta-img {
  width: 360px;
  min-width: 360px;
  height: 100%;
  object-fit: cover;
  display: block;
}

.tarjeta-img-container {
  width: 100%;
  height: 100%;
  background: #3d3d3d;
  display: flex;
}

.flip-card {
  width: 360px;
  min-width: 360px;
  position: relative;
}

.flip-card-inner {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.flip-card-front,
.flip-card-back {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  background: #3d3d3d;
  transition: opacity 0.6s ease;
}

.flip-card-back {
  opacity: 0;
}

.flip-card:hover .flip-card-front {
  opacity: 0;
}

.flip-card:hover .flip-card-back {
  opacity: 1;
}

.flip-card-front img,
.flip-card-back img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.tarjeta-info {
  background: #000000;
  padding: 35px 30px;
  flex: 1;
  min-height: 400px;
}

.tarjeta-nombre {
  font-family: var(--font-cinzel);
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
  font-family: var(--font-raleway);
  font-size: 1.1rem;
  font-style: italic;
  margin-bottom: 14px;
}

.tarjeta-subtitulo,
.tarjeta-descripcion,
.tarjeta-poderes {
  font-family: var(--font-raleway);
  font-size: 1.05rem;
  line-height: 1.65;
  color: #ddd;
}

.poderes-label {
  font-family: var(--font-cinzel);
  font-weight: 700;
  color: var(--color-dorado);
}

@media (max-width: 992px) {
  .tarjeta {
    flex-direction: column;
  }

  .tarjeta-img,
  .flip-card {
    width: 100%;
    min-width: unset;
    height: auto;
    aspect-ratio: 3 / 4;
  }

  .flip-card {
    position: relative;
    height: unset;
  }

  .flip-card-inner {
    position: relative;
    aspect-ratio: 3 / 4;
  }
}
</style>
