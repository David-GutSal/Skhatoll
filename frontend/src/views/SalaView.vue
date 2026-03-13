<template>

<div class="contenedor-sala">

  <div class="caja-sala">

    <h1 class="titulo">Sala de juegos</h1>
    <p>Bienvenido a la aldea de Castronegro</p>

    <div class="botones-sala">

      <button class="boton-sala" @click="crearSala">
        Crear sala
      </button>

      <button class="boton-sala" @click="irUnirse">
        Unirse a sala
      </button>

    </div>

  </div>

</div>

</template>


<script>

export default{

name:"SalaView",

methods:{

crearSala(){

console.log("Crear sala pendiente")

},

irUnirse(){

console.log("Ir a unirse a sala")

}

}

}

</script>


<style scoped>

.contenedor-sala{

display:flex;
justify-content:center;
align-items:center;

min-height:70vh;

}

.caja-sala{

background:#1f1f1f;
padding:40px;

border-radius:10px;

text-align:center;

width:90%;
max-width:500px;

}

.titulo{

color:white;
margin-bottom:30px;

}

p{ color:white;
  margin-bottom:30px;

}

.botones-sala{

display:flex;
justify-content:center;
gap:20px;

flex-wrap:wrap;

}

.boton-sala{

background:#8b0000;
color:white;

border:none;

padding:20px 30px;

font-size:18px;

border-radius:8px;

cursor:pointer;

}

.boton-sala:hover{

background:#a30000;

}

</style>