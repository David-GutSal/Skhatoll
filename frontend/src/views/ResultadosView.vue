<template>
  <div class="contenedor-resultados">
    <!-- Borrar para usar datos reales de backend -->
    <div class="cuadro-resultados">
      <h1 class="titulo">RESULTADO FINAL</h1>
      <p class="mensaje-resultado">{{ victoria ? 'VICTORIA' : 'DERROTA' }}</p>
      <p class="descripcion">
        {{ victoria ? '¡Enhorabuena! Tu equipo ha ganado' : 'Lo siento, tu equipo ha perdido' }}
      </p>
      <p class="ganador-equipo">{{ victoria ? 'Ganan los buenos' : 'Ganan los malos' }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "ResultadosView",
  data() {
    return {
      // Simulación: cambiar a false para ver "Derrota"
      victoria: true,
    };
  },
};
</script>

<style scoped>
.contenedor-resultados {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.cuadro-resultados {
  width: 80%;
  max-width: 600px;
  background-color: #000;
  border: 5px solid gold;
  padding: 50px 50px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.titulo {
  color: white;
  text-transform: uppercase;
  font-size: 28px;
  margin: 0;
}

.mensaje-resultado {
  color: white;
  font-size: 24px;
  margin: 0;
}

.descripcion {
  color: white;
  font-size: 18px;
  margin: 0;
}

.ganador-equipo {
  color: red;
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

@media (max-width: 600px) {
  .cuadro-resultados {
    padding: 20px 15px;
  }
  .titulo {
    font-size: 22px;
  }
  .mensaje-resultado {
    font-size: 20px;
  }
  .descripcion {
    font-size: 16px;
  }
  .ganador-equipo {
    font-size: 18px;
  }
}
</style>