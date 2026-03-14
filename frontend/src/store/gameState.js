import { reactive } from "vue"

export const MODO_SIMULACION = true

export const gameState = reactive({

fase: "DIA",

narrador: "Narrador",

jugadores: [

{id:1,nombre:"Jugador1",rol:"Bruja",vivo:true,alcalde:false,votos:0},
{id:2,nombre:"Jugador2",rol:"Lobo",vivo:true,alcalde:false,votos:0},
{id:3,nombre:"Jugador3",rol:"Aldeano",vivo:true,alcalde:true,votos:0},
{id:4,nombre:"Jugador4",rol:"Aldeano",vivo:true,alcalde:false,votos:0},
{id:5,nombre:"Jugador5",rol:"Vidente",vivo:true,alcalde:false,votos:0},
{id:6,nombre:"Jugador6",rol:"Aldeano",vivo:true,alcalde:false,votos:0},
{id:7,nombre:"Jugador7",rol:"Lobo",vivo:true,alcalde:false,votos:0},
{id:8,nombre:"Jugador8",rol:"Cazador",vivo:true,alcalde:false,votos:0}

]

})