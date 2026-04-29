<template>
  <div class="boton-mirol-wrapper">

    <button class="boton-mirol" @click="mostrar = true">
      <i class="fa-solid fa-scroll"></i> Mi Rol
    </button>

    <button
      v-if="enamorados"
      class="boton-enamorado"
      @click="mostrarEnamorado = !mostrarEnamorado"
    >
      <i class="fa-solid fa-heart"></i> Tu enamorado
    </button>

    <!-- Modal Mi Rol -->
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

    <!-- Desplegable enamorado -->
    <div v-if="mostrarEnamorado && enamorados" class="panel-enamorado">
      <button class="panel-cerrar" @click="mostrarEnamorado = false">
        <i class="fa-solid fa-xmark"></i>
      </button>
      <p class="panel-titulo">
        <i class="fa-solid fa-heart"></i>
        Tu enamorado es: <strong>{{ nombreEnamorado }}</strong>
      </p>
      <p class="panel-texto">
        - Recordad, vuestro objetivo es huir juntos de este pueblo así que, independientemente de vuestros roles, debéis permanecer unidos.
      </p>
      <p class="panel-texto">
        - No os podéis votar ni matar entre vosotros porque si uno muere el otro morirá de pena después.
      </p>
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
      mostrarEnamorado: false,
    }
  },

  computed: {
    ...mapGetters('sala', ['miRol', 'miRolDescripcion', 'miBando', 'enamorados', 'nombre']),

    nombreEnamorado() {
      if (!this.enamorados) return null
      // Devuelve el nombre del otro enamorado
      const miNombre = this.$store.getters['auth/nombre']
      if (this.enamorados.jugador1 === miNombre) return this.enamorados.jugador2
      return this.enamorados.jugador1
    },
  },
}
</script>

<style scoped>
.boton-mirol-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 12px;
  margin-top: 15px;
  position: relative;
  flex-wrap: wrap;
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

.boton-enamorado {
  background: transparent;
  color: #ff69b4;
  border: 3px solid #ff69b4;
  padding: 16px 24px;
  border-radius: 10px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s ease, color 0.2s ease;
}

.boton-enamorado:hover {
  background: #ff69b4;
  color: white;
}

.panel-enamorado {
  position: absolute;
  top: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.95);
  border: 2px solid #ff69b4;
  border-radius: 12px;
  padding: 20px 24px;
  width: min(420px, 90vw);
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel-cerrar {
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent;
  border: 1px solid #ff69b4;
  color: #ff69b4;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 0.75rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.panel-cerrar:hover {
  background: #ff69b4;
  color: white;
}

.panel-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.1rem;
  font-weight: 700;
  color: #ff69b4;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.panel-titulo i {
  color: #ff69b4;
}

.panel-texto {
  font-family: 'Raleway', Arial, sans-serif;
  color: #ccc;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 0;
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