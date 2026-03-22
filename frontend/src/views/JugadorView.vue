<template>

<div class="contenedor-jugador">

<CabeceraJugador
:nombreJugador="nombreJugador"
:esDia="esDia"
/>

<PanelVotacionesJugador
:esDia="esDia"
:votacionActiva="votacionActiva"
:jugadorSeleccionado="jugadorSeleccionado"
@votarAlcalde="votarAlcalde"
@votarCulpable="votarCulpable"
/>

<MesaJugadores
:jugadores="jugadoresVisibles"
:jugadorSeleccionado="jugadorSeleccionado"
:esDia="esDia"
@seleccionarJugador="seleccionarJugador"
/>

<BotonMiRol
:miRol="miRol"
/>

<ZonaPoderes
:miRol="miRol"
:jugadorSeleccionado="jugadorSeleccionado"
:esMiTurno="esMiTurno"
@devorar="devorarJugador"
@premonicion="usarPremonicion"
/>

</div>

</template>

<script>

import CabeceraJugador from "@/components/juego/CabeceraJugador.vue"
import PanelVotacionesJugador from "@/components/juego/PanelVotacionesJugador.vue"
import MesaJugadores from "@/components/juego/MesaJugadores.vue"
import BotonMiRol from "@/components/juego/BotonMiRol.vue"
import ZonaPoderes from "@/components/juego/ZonaPoderes.vue"

export default{

components:{
CabeceraJugador,
PanelVotacionesJugador,
MesaJugadores,
BotonMiRol,
ZonaPoderes
},

data(){

return{

nombreJugador:"Jugador3",
miRol:"LOBO",

esDia:true,
votacionActiva:true,
esMiTurno:true,

jugadorSeleccionado:null,

jugadores:[

{id:1,nombre:"Jugador1",rol:"Bruja",vivo:true},
{id:2,nombre:"Jugador2",rol:"Lobo",vivo:true},
{id:3,nombre:"Jugador3",rol:"Lobo",vivo:true},
{id:4,nombre:"Jugador4",rol:"Aldeano",vivo:true},
{id:5,nombre:"Jugador5",rol:"Vidente",vivo:true},
{id:6,nombre:"Jugador6",rol:"Aldeano",vivo:true},
{id:7,nombre:"Jugador7",rol:"Lobo",vivo:true},
{id:8,nombre:"Jugador8",rol:"Cazador",vivo:true}

]

}

},

computed:{

jugadoresVisibles(){

if(!this.esDia && this.miRol==="LOBO"){

return this.jugadores.filter(j=>j.rol!=="Lobo")

}

return this.jugadores

}

},

methods:{

seleccionarJugador(j){

this.jugadorSeleccionado=j

},

votarAlcalde(){

if(!this.jugadorSeleccionado)return

alert("Votaste alcalde a "+this.jugadorSeleccionado.nombre)

},

votarCulpable(){

if(!this.jugadorSeleccionado)return

alert("Votaste linchamiento a "+this.jugadorSeleccionado.nombre)

},

devorarJugador(){

if(!this.jugadorSeleccionado)return

alert("Devoraste a "+this.jugadorSeleccionado.nombre)

},

usarPremonicion(){

if(!this.jugadorSeleccionado)return

alert("Rol: "+this.jugadorSeleccionado.rol)

}

}

}

</script>

<style scoped>

.contenedor-jugador{

min-height:100vh;
padding:20px;

display:flex;
flex-direction:column;
gap:20px;

}

</style>