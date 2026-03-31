<template>
  <div class="indicador-wrapper" :class="esDia ? 'dia' : 'noche'">
    <div
      class="btn-fase"
      :class="{ activo: esDia }"
      @click="$emit('cambiarFase', 'dia')"
      title="Cambiar a día"
    >
      <i class="fas fa-sun"></i>
    </div>
    <div
      class="btn-fase"
      :class="{ activo: !esDia }"
      @click="$emit('cambiarFase', 'noche')"
      title="Cambiar a noche"
    >
      <i class="fas fa-moon"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IndicadorDiaNoche',
  props: {
    esDia: { type: Boolean, default: true },
  },
  emits: ['cambiarFase'],
}
</script>

<style scoped>

.indicador-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  border-radius: 50px;
  border: 5px solid #e4ba03;
  background: white;
  transition: background 0.3s ease, border-color 0.3s ease;
}

.indicador-wrapper.noche {
  background: #1a0033;
  border-color: white;
}

.btn-fase {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  cursor: pointer;
  background: #111;
  color: white;
  transition: transform 0.2s ease, background 0.2s ease, color 0.2s ease;
}

.btn-fase:hover {
  transform: scale(0.9);
  color: #cc0000;
  background: #000;
}

.btn-fase:active {
  transform: scale(1.1);
  color: #e4ba03;
  background: #000;
}

.indicador-wrapper.dia .btn-fase:first-child.activo {
  background: #0087bd;
  color: #e4ba03;
}

.indicador-wrapper.noche .btn-fase:last-child.activo {
  background: #5500a5;
  color: #0087bd;
}
</style>