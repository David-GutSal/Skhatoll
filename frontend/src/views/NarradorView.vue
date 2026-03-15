Para poder esta vista poner este enlace: http://localhost:5173/narrador

<template>

<div class="contenedor-narrador" :class="{noche: !esDia}">

  <div class="cabecera">

    <div class="narrador-box">
      Narrador: {{gameState.narrador}}
    </div>

    <IndicadorDiaNoche
      :esDia="esDia"
      @cambiarFase="cambiarFase"
    />

  </div>

  <PanelControlNarrador
    :esDia="esDia"
    :hayAlcalde="hayAlcalde"
    @votarLinchamiento="iniciarVotacionLinchamiento"
    @votarAlcalde="iniciarVotacionAlcalde"
    @finalizarVotacion="finalizarVotacion"
    @eventos="iniciarEventos"
  />

  <MesaJugadores
    :jugadores="gameState.jugadores"
    :esDia="esDia"
    @seleccionarJugador="activarTurnoJugador"
  />

</div>

</template>

<script>

import { gameState, MODO_SIMULACION } from "@/store/gameState"
import { enviarEvento } from "@/services/websocketService"

import IndicadorDiaNoche from "@/components/juego/IndicadorDiaNoche.vue"
import PanelControlNarrador from "@/components/juego/PanelControlNarrador.vue"
import MesaJugadores from "@/components/juego/MesaJugadores.vue"

export default {

components:{
IndicadorDiaNoche,
PanelControlNarrador,
MesaJugadores
},

data(){
return{

gameState,

esDia:true,

}
},

computed:{

hayAlcalde(){
return this.gameState.jugadores.some(j=>j.alcalde)
}

},

methods:{

cambiarFase(fase){

this.esDia = fase==="dia"

/*
WEBSOCKET BACKEND

envía cambio de fase a todos los jugadores

payload ejemplo

{
 tipo:"FASE",
 fase:"DIA"
}
*/

if(!MODO_SIMULACION){
enviarEvento("FASE",{fase})
}

},

iniciarVotacionLinchamiento(){

/*
BACKEND
habilita votaciones de linchamiento
*/

if(!MODO_SIMULACION){
enviarEvento("VOTACION_LINCHAMIENTO")
}

},

iniciarVotacionAlcalde(){

if(this.hayAlcalde)return

/*
BACKEND
habilita votaciones alcalde
*/

if(!MODO_SIMULACION){
enviarEvento("VOTACION_ALCALDE")
}

},

finalizarVotacion(){

/*
BACKEND
calcula ganador
*/

if(!MODO_SIMULACION){
enviarEvento("FINALIZAR_VOTACION")
}

},

iniciarEventos(){

/*
BACKEND
inicia fase eventos nocturnos
*/

if(!MODO_SIMULACION){
enviarEvento("INICIAR_EVENTOS")
}

},

activarTurnoJugador(jugador){

/*
BACKEND

envía turno a jugador

ejemplo payload

{
tipo:"TURNO_JUGADOR",
jugador:"Jugador5"
}
*/

if(!MODO_SIMULACION){
enviarEvento("TURNO_JUGADOR",{jugador:jugador.nombre})
}

}

}

}

</script>

<style scoped>

.contenedor-narrador{

min-height:100vh;
padding:20px;

background-image:url("@/assets/imgs/fondo.png");
background-size:cover;

}

.contenedor-narrador.noche{

background-image:url("@/assets/imgs/fondonoche.png");

}

.cabecera{

display:flex;
justify-content:space-between;
align-items:center;
flex-wrap: wrap;
gap: 15px;
margin-bottom:20px;

}

.narrador-box{

background:black;
color:red;

padding:12px 20px;

font-weight:bold;

}

.noche .narrador-box{

background:white;

}

</style>
