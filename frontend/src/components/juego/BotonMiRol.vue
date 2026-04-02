<template>
  <div class="boton-mirol-wrapper">

    <button class="boton-mirol" @click="mostrar = true">
      <i class="fa-solid fa-scroll"></i> Mi Rol
    </button>

    <div v-if="mostrar" class="modal-overlay" @click.self="mostrar = false">
      <div class="modal-contenido">
        <button class="modal-cerrar" @click="mostrar = false">
          <i class="fa-solid fa-xmark"></i>
        </button>
        <CartaRol
          modoVista="miRol"
          :nombreRol="miRol"
          :descripcion="miRolDescripcion"
          :bando="miBando"
        />
      </div>
    </div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import CartaRol from '@/components/juego/roles/CartaRol.vue'

export default {
  name: 'BotonMiRol',
  components: { CartaRol },

  data() {
    return {
      mostrar: false,
    }
  },

  computed: {
    ...mapGetters('sala', ['miRol', 'miRolDescripcion', 'miBando']),
  },
}
</script>

<style scoped>
.boton-mirol-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.boton-mirol {
  background: #8b0000;
  color: white;
  border: none;
  padding: 16px 36px;
  border-radius: 10px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s ease, transform 0.15s ease;
}

.boton-mirol:hover {
  background: #a30000;
}

.boton-mirol:active {
  transform: scale(0.96);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-contenido {
  position: relative;
  max-width: 360px;
  width: 90%;
}

.modal-cerrar {
  position: absolute;
  top: -14px;
  right: -14px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #1f1f1f;
  border: 2px solid #8b0000;
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  transition: background 0.2s ease;
}

.modal-cerrar:hover {
  background: #8b0000;
}
</style>