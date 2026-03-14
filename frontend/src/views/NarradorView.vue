<template>

<div class="contenedor-narrador" :class="fase.toLowerCase()">


<div class="barra-superior">

<div class="panel-narrador">
Narrador: {{ narrador }}
</div>

<div class="selector-fase">

<div class="icono-fase luna" @click="cambiarNoche">
<i class="fa-solid fa-moon"></i>
</div>

<div class="icono-fase sol" @click="cambiarDia">
<i class="fa-solid fa-sun"></i>
</div>

</div>

</div>

<div class="panel-botones">

<button v-if="fase==='DIA'" class="boton">
Iniciar votación
</button>

<button v-if="fase==='DIA'" class="boton">
Elegir alcalde
</button>

<button v-if="fase==='NOCHE'" class="boton boton-noche">
Iniciar eventos
</button>

</div>

<div class="mesa-jugadores">

<div
v-for="jugador in jugadores"
:key="jugador.id"
class="carta-jugador"
:class="estadoClase(jugador)"
@click="seleccionarJugador(jugador)"
>

<img
class="imagen-rol"
src="@/assets/imgs/bruja.jpg"
/>

<div class="info-jugador">

<p><strong>Jugador:</strong> {{ jugador.nombre }}</p>

<p><strong>Rol:</strong> {{ jugador.rol }}</p>

</div>

<div class="iconos-estado">

<span v-if="jugador.alcalde">👑</span>

<span v-if="!jugador.vivo">💀</span>

<span v-if="jugador.objetivo">🎯</span>

</div>

</div>

</div>

</div>

</template>


<script>

export default{

name:"NarradorView",

data(){

return{

fase:"DIA",

narrador:"Maestro de ceremonias",

jugadores:[

{id:1,nombre:"Jugador1",rol:"Bruja",vivo:true,alcalde:false,objetivo:false},

{id:2,nombre:"Jugador2",rol:"Lobo",vivo:true,alcalde:false,objetivo:false},

{id:3,nombre:"Jugador3",rol:"Aldeano",vivo:true,alcalde:true,objetivo:false},

{id:4,nombre:"Jugador4",rol:"Aldeano",vivo:true,alcalde:false,objetivo:false},

{id:5,nombre:"Jugador5",rol:"Vidente",vivo:true,alcalde:false,objetivo:false},

{id:6,nombre:"Jugador6",rol:"Aldeano",vivo:true,alcalde:false,objetivo:false},

{id:7,nombre:"Jugador7",rol:"Lobo",vivo:true,alcalde:false,objetivo:false},

{id:8,nombre:"Jugador8",rol:"Cazador",vivo:true,alcalde:false,objetivo:false}

]

}

},

methods:{


cambiarDia(){

this.fase="DIA"

},

cambiarNoche(){

this.fase="NOCHE"

},


estadoClase(jugador){

return{

muerto:!jugador.vivo,

alcalde:jugador.alcalde,

objetivo:jugador.objetivo

}

},


seleccionarJugador(jugador){

this.jugadores.forEach(j=>j.objetivo=false)

jugador.objetivo=true

}

}

}

</script>


<style scoped>


.contenedor-narrador{

min-height:100vh;
padding:30px;
color:white;

}

.contenedor-narrador.dia{

background-image:url('@/assets/imgs/fondo.jpg');
background-size:cover;

}

.contenedor-narrador.noche{

background-image:url('@/assets/imgs/fondonoche.png');
background-size:cover;

}

.barra-superior{

display:flex;
justify-content:space-between;
align-items:center;
margin-bottom:30px;

}

.panel-narrador{

background:#1f1f1f;
padding:10px 20px;

}

.selector-fase{

display:flex;
gap:20px;

}

.icono-fase{

width:60px;
height:60px;
display:flex;
align-items:center;
justify-content:center;
border-radius:50%;
font-size:24px;
cursor:pointer;

}

.luna{

background:black;
color:white;

}

.sol{

background:white;
color:black;

}

.panel-botones{

display:flex;
justify-content:center;
gap:20px;
margin-bottom:30px;

}

.boton{

background:black;
color:white;
border:none;
padding:12px 20px;
cursor:pointer;

}

.boton-noche{

background:#8b0000;

}

.mesa-jugadores{

display:grid;

grid-template-columns:repeat(4,1fr);

gap:25px;

max-width:1100px;

margin:auto;

padding:30px;

}

.contenedor-narrador.dia .mesa-jugadores{

background:white;
border:4px solid #111;

}


.contenedor-narrador.noche .mesa-jugadores{

background:#8b0000;
border:4px solid white;

}

.carta-jugador{

background:#1f1f1f;
padding:20px;
display:flex;
align-items:center;
gap:20px;
position:relative;
cursor:pointer;

}

.imagen-rol{

width:80px;
height:80px;
object-fit:cover;

}

.info-jugador{

font-size:16px;

}

.iconos-estado{

position:absolute;
top:5px;
right:10px;
font-size:20px;

}

.muerto{

background:#555;
opacity:0.6;

}


.alcalde{

border:3px solid gold;

}


.objetivo{

border:3px solid red;

}

@media (max-width:1200px){

.carta-jugador{

flex-direction:column;
text-align:center;

}

}


@media (max-width:900px){

.mesa-jugadores{

grid-template-columns:repeat(2,1fr);

}

}

@media (max-width:500px){

.mesa-jugadores{

grid-template-columns:1fr;

}

}

</style>