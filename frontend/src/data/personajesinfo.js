import cartaBruja from '@/assets/imgs/carta-bruja.jpg'
import cartaAldeano from '@/assets/imgs/carta-aldeano.jpg'
import cartaVidente from '@/assets/imgs/carta-vidente.jpg'
import cartaNinna from '@/assets/imgs/carta-ninna.jpg'
import cartaCazador from '@/assets/imgs/carta-cazador.jpg'
import cartaCupido from '@/assets/imgs/carta-cupido.jpg'
import cartaLobo from '@/assets/imgs/carta-lobo.jpg'
import cartaNinno from '@/assets/imgs/carta-ninno.jpg'
import cartaTonto from '@/assets/imgs/carta-tonto.jpg'
import cartaOso from '@/assets/imgs/carta-domaosos.jpg'
import cartaAngel from '@/assets/imgs/carta-angel.jpg'
import cartaCaballero from '@/assets/imgs/carta-caballero.jpg'

export const listas = [
  {
    bando: 'bien',
    nombre: 'El bando del Bien, Los Aldeanos',
    color: '#2d9e2d',
    abierta: false,
    personajes: [
      {
        nombre: 'Aldeano',
        imagen: cartaAldeano,
        subtitulo: 'El corazón del pueblo',
        descripcion: 'Un simple habitante sin habilidades especiales, pero esencial para descubrir a los hombres lobo mediante la deducción y el voto. Su mayor fuerza es su número y la confianza en sus vecinos',
        poderes: 'No tiene poderes.'
      },
      {
        nombre: 'La Bruja',
        imagen: cartaBruja,
        subtitulo: 'Maestra de las pociones',
        descripcion: 'La vieja hechicera de la aldea, experta en crear ungüentos y pociones. Una de las mayores fuerzas para combatir a los lobos por lo que debe mantenerse siempre alerta para pasar desapercibida y no ser devorada.',
        poderes: 'La bruja posee 2 pociones las cuales utilizará por la noche: una resucita a una persona devorada por los lobos y la otra mata a otro personaje. Puede usar la poción de resurrección consigo misma. Solo puede usar cada una de las pociones una vez por partida'
      },
      {
        nombre: 'La Vidente',
        imagen: cartaVidente,
        subtitulo: 'Clarividencia honesta',
        descripcion: 'Bendecida con los dones de la premonición y la clarividencia, es junto a la bruja una de las mayores fuerzas que tiene el bando de los aldeanos. Debido a sus grandes poderes debe ser cautelosa con lo que dice y hace para sobrevivir lo máximo posible durante toda la partida.',
        poderes: 'Cada noche, tras la llamada del narrador, se levanta y puede ver el rol de otro personaje que ella elija.'
      },
      {
        nombre: 'La Niña',
        imagen: cartaNinna,
        subtitulo: 'Curiosa e imprudente',
        descripcion: 'Una niña pequeña con un gran oído capaz de escuchar hasta los pasos ligeros de un lobo. Puede intentar observar a los hombres lobo durante la noche, pero si es descubierta, los lobos la devorarán en lugar de a la víctima escogida.',
        poderes: 'Cuando por la noche es el turno de los lobos, la niña puede entreabrir los ojos para intentar averiguar quiénes son. Pero solo durante el turno de los lobos. NO puede abrir los ojos totalmente ni hacerse pasar por un lobo.'
      },
      {
        nombre: 'El Cazador',
        imagen: cartaCazador,
        subtitulo: 'Vengativo hasta el final',
        descripcion: 'Un hombre solitario aficionado a la cacería. Sigue muy de cerca todo lo relacionado con los hombres lobo pues quiere cazarlos para exponerlos como trofeo en su hogar.',
        poderes: 'Si este personaje es eliminado de cualquier manera, puede disparar a otro personaje a su elección para eliminarlo de la partida.'
      },
      {
        nombre: 'Cupido',
        imagen: cartaCupido,
        subtitulo: 'Arquero del destino',
        descripcion: 'El pequeño dios del amor, al disparar sus flechas mágicas puede enamorar a 2 jugadores para siempre sin importar su bando. Cupido puede enamorarse a sí mismo.',
        poderes: 'En la primera noche se despertará y enamorará a 2 personas. Estas 2 personas, independientemente de su bando, tendrán como objetivo huir juntos de la aldea por lo que buscarán eliminar al resto de jugadores. Estos jugadores no pueden dañarse entre ellos y si uno de los 2 muere, el otro muere de pena después.'
      },
      {
        nombre: 'El Domador de Osos',
        imagen: cartaOso,
        subtitulo: 'Instinto salvaje',
        descripcion: 'Un antiguo artista circense retirado que domesticaba osos conocido por sus espectáculos de baile con estas grandes criaturas. Tiene un osezno con un gran olfato que puede detectar la presencia de peligro cercano.',
        poderes: 'Cada mañana, después de descubrir las víctimas, si hay al menos un Hombre Lobo adyacente al Domador de Osos, el narrador emitirá un gruñido. Sólo se tendrá en cuenta a los vecinos que todavía estén en juego. Si el Domador de Osos está infectado por el Infecto Padre de todos los Lobos, el narrador gruñirá todos los turnos hasta que sea eliminado.'
      },
      {
        nombre: 'El Tonto del Pueblo',
        imagen: cartaTonto,
        subtitulo: 'No pienso, luego no existo',
        descripcion: 'Un chaval con pocas luces hijo de una familia con alto poder adquisitivo. Parece que siempre está perdiendo el tiempo y se le puede encontrar de vez en cuando montando alguna escandalera.',
        poderes: 'Si la Aldea vota en su contra, enseña su carta y no le podrán linchar. Si eso ocurre pierde su derecho a voto durante el resto de la partida. Si era Alguacil/ Alcalde, el cargo se pierde para el resto de la partida. Su inmunidad solo se aplica al linchamiento del pueblo'
      },
      {
        nombre: 'El Caballero de la Espada Oxidada',
        imagen: cartaCaballero,
        subtitulo: 'Honor hasta el final',
        descripcion: 'Un viejo caballero que fue muy leal a su antiguo rey. Posee una espada que pese a estar oxidada, el caballero guarda con cariño pues fue su mejor compañera de batallas. Incluso ahora duerme por la noche con ella... por lo que podría pasar...',
        poderes: 'Si es devorado queda eliminado de la partida, pero uno de los hombres lobo enfermará. El hombre lobo que esté situado más cerca de él por su izquierda, morirá al terminar la siguiente noche. La muerte de ese lobo se anunciará durante el día tras pasar esa noche.'
      }
    ]
  },
  {
    bando: 'mal',
    nombre: 'El bando del Mal, Los Hombres Lobo',
    color: '#cc0000',
    abierta: false,
    personajes: [
      {
        nombre: 'El Hombre Lobo',
        imagen: cartaLobo,
        subtitulo: 'Depredador insaciable',
        descripcion: 'Antes eran simples aldeanos pero una terrible maldición los transformó en las bestias que son ahora. Cada noche, tras escuchar la llamada de la luna, se levantan en busca de una nueva presa. Por el día vivirán como simples aldeanos buscando pasar desapercibidos',
        poderes: 'Elegir una víctima cada noche junto al resto de lobos para devorarla y eliminarla del juego. Un hombre lobo nunca puede eliminar a otro hombre lobo'
      }
    ]
  },
  {
    bando: 'neutral',
    nombre: 'Ambiguos y Solitarios',
    color: '#868686',
    abierta: false,
    personajes: [
      {
        nombre: 'El Niño Salvaje',
        imagen: cartaNinno,
        subtitulo: 'Fácilmente influenciable',
        descripcion: 'Un niño que vive en el bosque del que nadie sabe su procedencia. A veces se acerca al pueblo a por comida o a observar a los viandantes. Tiene una devoción especial por una persona del pueblo',
        poderes: 'En la primera noche, elige a una persona como mentor o maestro a seguir. Independientemente del bando de ese mentor, si este muere, el niño se convierte en hombre lobo. Puede decidir eliminar a su mentor si es necesario.'
      },
      {
        nombre: 'El Ángel',
        imagen: cartaAngel,
        subtitulo: 'Victoria en el autosacrificio',
        descripcion: 'Le desagrada la vida de una aldea infestada de criaturas malignas. Desearía ser víctima de la peor pesadilla para poder volver a despertar apaciblemente en su mullida cama de nubes.',
        poderes: 'Su objetivo principal es morir en la primera ronda ya sea por la noche o durante el día, de cualquier manera. Si muere, gana la partida y se empieza una nueva. Si pierde, se convierte en un aldeano normal. Cuando este personaje está en juego, la partida comienza con un debate para hacer una posterior votación por linchamiento'
      }
    ]
  }
]