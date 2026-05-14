<template>
  <div class="contenedor-sala">
    <div class="caja-sala">
      <h1 class="titulo">Sala de Juegos</h1>

      <div class="imagen-wrapper">
        <img src="@/assets/imgs/bienvenida.jpg" alt="Aldea de Castronegro" class="imagen-sala" />
        <div class="botones-sala">
          <button class="boton-sala" @click="handleCrearSala">Crear sala</button>
          <button class="boton-sala" @click="irUnirse">Unirse a sala</button>
        </div>
      </div>

      <p class="subtitulo">
        <i class="fa-solid fa-feather-pointed"></i> Adéntrate en la aldea de Castronegro, elige
        entre unirte a una partida o crear tu propia aventura...
      </p>
    </div>
  </div>
</template>

<script setup>
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import axiosInstance from '@/plugins/axios'

const store = useStore()
const router = useRouter()

store.dispatch('sala/resetSala')

const handleCrearSala = async () => {
  try {
    const res = await axiosInstance.post('/salas/crear')
    store.dispatch('sala/crearSala', res.data.codigoSala)
    router.push({ name: 'lobby' })
  } catch (error) {
    store.dispatch('toast/mostrar', { mensaje: 'Error al crear la sala', tipo: 'error' })
  }
}

const irUnirse = () => {
  store.dispatch('sala/unirse', null)
  router.push({ name: 'lobby' })
}
</script>

<style scoped>
.contenedor-sala {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 40px 20px;
  box-sizing: border-box;
}

.caja-sala {
  background: #1f1f1f;
  padding: 40px 40px 36px;
  border-radius: 15px;
  text-align: center;
  width: 50%;
  box-sizing: border-box;
}

.titulo {
  color: white;
  margin: 0 0 28px 0;
  font-size: 2.5rem;
  font-weight: bolder;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.imagen-wrapper {
  position: relative;
  width: 100%;
  border-radius: 15px;
  overflow: hidden;
}

.imagen-sala {
  width: 100%;
  display: block;
  aspect-ratio: 1360 / 768;
  object-fit: cover;
  border-radius: 8px;
}

.botones-sala {
  position: absolute;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 50px;
  flex-wrap: wrap;
  padding: 25px;
  box-sizing: border-box;
}

.boton-sala {
  background: #8b0000;
  color: white;
  border: 5px solid #000;
  padding: 18px 32px;
  font-size: 1.1rem;
  font-family: var(--font-cinzel);
  font-weight: 700;
  letter-spacing: 0.05em;
  border-radius: 10px;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.6);
  transition:
    background 0.8s ease-out,
    transform 0.45s ease-out,
    border-color 0.6s ease-out;
}

.boton-sala:hover {
  background: #1f1f1f;
  transform: scale(1.1);
  border: 5px solid #8b0000;
  transition:
    background 0.4s ease-out,
    transform 0.3s ease-out,
    border-color 0.4s ease-out;
}

.subtitulo {
  color: #ffffff;
  font-family: var(--font-raleway);
  font-size: 1.5rem;
  font-style: italic;
  letter-spacing: 0.05em;
  margin: 24px 0 0 0;
}

@media (max-width: 1185px) {
  .caja-sala {
    width: 70%;
    padding: 24px 16px;
  }
}

@media (max-width: 765px) {
  .caja-sala {
    width: 85%;
  }
  .botones-sala {
    flex-direction: column;
  }
}

@media (max-width: 615px) {
  .caja-sala {
    width: 95%;
    padding: 20px 12px;
  }

  .botones-sala {
    gap: 20px;
    padding: 15px;
  }
}

@media (max-width: 420px) {
  .caja-sala {
    width: 100%;
    padding: 20px 12px;
  }

  .botones-sala {
    gap: 10px;
    padding: 10px;
  }

  .boton-sala {
    padding: 10px 20px;
    font-size: 1rem;
    font-weight: 500;
  }
}
</style>
