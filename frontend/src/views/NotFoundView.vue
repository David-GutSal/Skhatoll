<template>
  <div class="notfound-pagina">
    <div class="notfound-contenedor">
      <div class="recuadro">
        <h1 class="titulo">404</h1>
        <p class="texto">Página no encontrada</p>
        <button class="btn-volver" @click="irInicio">Volver al inicio</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const irInicio = () => {
  router.push({ name: 'inicio' })
}
</script>

<style scoped>
.notfound-pagina {
  min-height: 100vh;
  background-image: url('@/assets/imgs/fondo.webp');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.notfound-contenedor {
  width: 70%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.recuadro {
  width: 100%;
  max-width: 500px;
  background: #2a2a2a;
  border-radius: 15px;
  border: 10px solid #000;
  padding: 45px;
  box-sizing: border-box;
  text-align: center;
}

.titulo {
  font-family: var(--font-cinzel);
  font-size: 4rem;
  font-weight: bold;
  color: var(--color-rojo);
  margin-bottom: 20px;
}

.texto {
  font-family: var(--font-raleway);
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  line-height: 1.4;
  margin: 4px auto 30px;
}

.btn-volver {
  background: #000;
  color: var(--color-rojo);
  border: none;
  padding: 14px 32px;
  border-radius: 10px;
  font-family: var(--font-raleway);
  font-size: 1.5rem;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, transform 0.15s ease;
}

.btn-volver:hover {
  background: white;
  color: #000;
}

.btn-volver:active {
  transform: scale(0.96);
}

@media (max-width: 768px) {
  .notfound-contenedor {
    width: 90%;
  }
  .recuadro {
    padding: 24px;
  }
  .titulo {
    font-size: 2.5rem;
  }
  .texto {
    font-size: 1.2rem;
  }
}
</style>
