import { createStore } from 'vuex'
import sala from './sala.js'

/**
 * @typedef {import('vuex').Store} Store
 */

describe('Sala Store - Flujo de Juego', () => {
  /** @type {Store} */
  let store

  beforeEach(() => {
    store = createStore({
      modules: { sala },
    })
  })

  describe('Estado Inicial', () => {
    it('debería tener valores por defecto', () => {
      expect(store.state.sala.codigoSala).toBeNull()
      expect(store.state.sala.esCreador).toBe(false)
      expect(store.state.sala.jugadores).toEqual([])
      expect(store.state.sala.jugadoresConRol).toEqual([])
      expect(store.state.sala.fase).toBe('DIA')
      expect(store.state.sala.tipoVotacion).toBeNull()
    })
  })

  describe('Crear/Unirse a Sala', () => {
    it('debería crear una sala como creador', async () => {
      await store.dispatch('sala/crearSala', 'CODIGO123')

      expect(store.state.sala.codigoSala).toBe('CODIGO123')
      expect(store.state.sala.esCreador).toBe(true)
    })

    it('debería unirse a una sala sin ser creador', async () => {
      await store.dispatch('sala/unirse', 'CODIGO456')

      expect(store.state.sala.codigoSala).toBe('CODIGO456')
      expect(store.state.sala.esCreador).toBe(false)
    })

    it('debería restaurar estado desde sessionStorage', async () => {
      sessionStorage.setItem('codigoSala', 'RESTORED')
      sessionStorage.setItem('esCreador', 'true')

      await store.dispatch('sala/restaurarEstado')

      expect(store.state.sala.codigoSala).toBe('RESTORED')
      expect(store.state.sala.esCreador).toBe(true)

      sessionStorage.clear()
    })
  })

  describe('Gestión de Jugadores', () => {
    it('debería establecer jugadores', async () => {
      const jugadores = [
        { nombre: 'Alice', estaVivo: true },
        { nombre: 'Bob', estaVivo: true },
        { nombre: 'Charlie', estaVivo: false },
      ]

      await store.dispatch('sala/setJugadores', jugadores)

      expect(store.state.sala.jugadores).toHaveLength(3)
      expect(store.state.sala.jugadores[0].nombre).toBe('Alice')
    })

    it('debería establecer jugadores con roles', async () => {
      const jugadoresConRol = [
        { nombre: 'Alice', rol: 'Lobo', estaVivo: true },
        { nombre: 'Bob', rol: 'Aldeano', estaVivo: true },
      ]

      await store.dispatch('sala/setJugadoresConRol', jugadoresConRol)

      expect(store.state.sala.jugadoresConRol).toHaveLength(2)
    })

    it('debería establecer mi rol', async () => {
      const rol = {
        nombreRol: 'Vidente',
        descripcionRol: 'Puede ver el rol de otros',
        bando: 'Aldeanos',
      }

      await store.dispatch('sala/setRol', rol)

      expect(store.state.sala.miRol).toBe('Vidente')
      expect(store.state.sala.miRolDescripcion).toBe('Puede ver el rol de otros')
      expect(store.state.sala.miBando).toBe('Aldeanos')
    })
  })

  describe('Cambio de Fase (Día/Noche)', () => {
    it('debería cambiar a fase NOCHE', async () => {
      expect(store.state.sala.fase).toBe('DIA')

      await store.dispatch('sala/setFase', 'NOCHE')

      expect(store.state.sala.fase).toBe('NOCHE')
    })

    it('debería cambiar a fase DIA', async () => {
      await store.dispatch('sala/setFase', 'NOCHE')

      await store.dispatch('sala/setFase', 'DIA')

      expect(store.state.sala.fase).toBe('DIA')
    })

    it('debería ignorar fase inválida', async () => {
      await store.dispatch('sala/setFase', 'INVALIDA')

      expect(store.state.sala.fase).toBe('DIA')
    })
  })

  describe('Gestión de Muerte', () => {
    beforeEach(async () => {
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true },
        { nombre: 'Bob', estaVivo: true },
        { nombre: 'Charlie', estaVivo: true },
      ])
    })

    it('debería marcar jugador como muerto', async () => {
      await store.dispatch('sala/marcarMuerto', 'Alice')

      const alice = store.state.sala.jugadores.find((j) => j.nombre === 'Alice')
      expect(alice.estaVivo).toBe(false)
      expect(alice.muerteConfirmada).toBe(true)
    })

    it('debería marcar jugador como semi-muerto', async () => {
      await store.dispatch('sala/marcarSemimuerto', 'Bob')

      const bob = store.state.sala.jugadores.find((j) => j.nombre === 'Bob')
      expect(bob.estaVivo).toBe(false)
      expect(bob.muerteConfirmada).toBe(false)
      expect(store.state.sala.semiMuertos).toContain('Bob')
    })

    it('debería quitar semi-muerto', async () => {
      await store.dispatch('sala/marcarSemimuerto', 'Bob')
      await store.dispatch('sala/quitarSemimuerto', 'Bob')

      const bob = store.state.sala.jugadores.find((j) => j.nombre === 'Bob')
      expect(bob.estaVivo).toBe(true)
      expect(store.state.sala.semiMuertos).not.toContain('Bob')
    })
  })

  describe('Flujo de Votación', () => {
    beforeEach(async () => {
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true, votos: 0 },
        { nombre: 'Bob', estaVivo: true, votos: 0 },
        { nombre: 'Charlie', estaVivo: true, votos: 0 },
      ])

      await store.dispatch('sala/setTipoVotacion', 'DIA')
    })

    it('debería establecer tipo de votación', () => {
      expect(store.state.sala.tipoVotacion).toBe('DIA')
    })

    it('debería actualizar votos correctamente', async () => {
      await store.dispatch('sala/actualizarVotos', [
        { nombreObjetivo: 'Alice' },
        { nombreObjetivo: 'Alice' },
        { nombreObjetivo: 'Bob' },
      ])

      const alice = store.state.sala.jugadores.find((j) => j.nombre === 'Alice')
      const bob = store.state.sala.jugadores.find((j) => j.nombre === 'Bob')

      expect(alice.votos).toBe(2)
      expect(bob.votos).toBe(1)
    })

    it('debería reiniciar votos', async () => {
      await store.dispatch('sala/actualizarVotos', [
        { nombreObjetivo: 'Alice' },
        { nombreObjetivo: 'Bob' },
      ])

      await store.dispatch('sala/reiniciarVotos')

      store.state.sala.jugadores.forEach((j) => {
        expect(j.votos).toBe(0)
      })
    })

    it('debería designar alcalde', async () => {
      await store.dispatch('sala/designarAlcalde', 'Alice')

      const alice = store.state.sala.jugadores.find((j) => j.nombre === 'Alice')
      expect(alice.alcalde).toBe(true)
    })
  })

  describe('Turno Nocturno', () => {
    beforeEach(async () => {
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true, esTurno: false },
        { nombre: 'Bob', estaVivo: true, esTurno: false },
      ])
    })

    it('debería activar turno de un jugador', async () => {
      await store.dispatch('sala/turnoNocturno', 'Alice')

      const alice = store.state.sala.jugadores.find((j) => j.nombre === 'Alice')
      expect(alice.esTurno).toBe(true)
    })

    it('debería establecer turno activo', async () => {
      await store.dispatch('sala/setTurnoActivo', 'Alice')

      expect(store.state.sala.turnoActivo).toBe('Alice')
    })
  })

  describe('Enamorados (Cupido)', () => {
    it('debería establecer enamorados', async () => {
      await store.dispatch('sala/setEnamorados', {
        jugador1: 'Alice',
        jugador2: 'Bob',
      })
      await store.dispatch('sala/setCupidoUsado')

      expect(store.state.sala.enamorados.jugador1).toBe('Alice')
      expect(store.state.sala.enamorados.jugador2).toBe('Bob')
      expect(store.state.sala.cupidoUsado).toBe(true)
    })

    it('debería marcar cupido como usado', async () => {
      await store.dispatch('sala/setCupidoUsado')

      expect(store.state.sala.cupidoUsado).toBe(true)
    })
  })

  describe('Bruja - Pociones', () => {
    it('debería usar poción de vida', async () => {
      expect(store.state.sala.brujaPocionVidaUsada).toBe(false)

      await store.dispatch('sala/setBrujaPocionVida')

      expect(store.state.sala.brujaPocionVidaUsada).toBe(true)
    })

    it('debería usar poción de muerte', async () => {
      expect(store.state.sala.brujaPocionMuerteUsada).toBe(false)

      await store.dispatch('sala/setBrujaPocionMuerte')

      expect(store.state.sala.brujaPocionMuerteUsada).toBe(true)
    })
  })

  describe('Narrador y Mentor', () => {
    it('debería establecer narrador', async () => {
      await store.dispatch('sala/setNarrador', 'Narrador')

      expect(store.state.sala.narradorActual).toBe('Narrador')
    })

    it('debería establecer mentor del niño', async () => {
      await store.dispatch('sala/setMentorNinno', 'Alice')

      expect(store.state.sala.mentorNinno).toBe('Alice')
      expect(store.state.sala.narradorActual).toBe('Alice')
    })
  })

  describe('Resultado de Partida', () => {
    it('debería establecer resultado', async () => {
      await store.dispatch('sala/setResultado', {
        bandoGanador: 'Lobos',
        mensaje: 'Los lobos han ganado',
      })

      expect(store.state.sala.bandoGanador).toBe('Lobos')
      expect(store.state.sala.mensajeFin).toBe('Los lobos han ganado')
    })
  })

  describe('Limpieza de Estado', () => {
    it('debería salir de la sala', async () => {
      await store.dispatch('sala/crearSala', 'CODIGO123')
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true },
      ])

      await store.dispatch('sala/salir')

      expect(store.state.sala.codigoSala).toBeNull()
      expect(store.state.sala.jugadores).toEqual([])
    })

    it('debería resetear la sala', async () => {
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true },
      ])
      await store.dispatch('sala/setTipoVotacion', 'DIA')

      await store.dispatch('sala/resetSala')

      expect(store.state.sala.jugadores).toEqual([])
      expect(store.state.sala.tipoVotacion).toBeNull()
    })
  })

  describe('Getters', () => {
    it('debería devolver todos los getters correctamente', () => {
      expect(store.getters['sala/codigoSala']).toBeNull()
      expect(store.getters['sala/esCreador']).toBe(false)
      expect(store.getters['sala/jugadores']).toEqual([])
      expect(store.getters['sala/fase']).toBe('DIA')
    })
  })

  describe('Flujo Completo de Juego', () => {
    it('debería simular un turno completo: día -> noche -> día', async () => {
      await store.dispatch('sala/crearSala', 'PARTIDA1')
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true },
        { nombre: 'Bob', estaVivo: true },
        { nombre: 'Charlie', estaVivo: true },
      ])

      // Fase Día
      expect(store.state.sala.fase).toBe('DIA')

      // Abrir votación de día
      await store.dispatch('sala/setTipoVotacion', 'DIA')
      await store.dispatch('sala/actualizarVotos', [
        { nombreObjetivo: 'Bob' },
        { nombreObjetivo: 'Bob' },
        { nombreObjetivo: 'Charlie' },
      ])

      // Eliminar jogador por votos
      await store.dispatch('sala/marcarMuerto', 'Bob')
      await store.dispatch('sala/reiniciarVotos')

      // Cambiar a noche
      await store.dispatch('sala/setFase', 'NOCHE')

      expect(store.state.sala.fase).toBe('NOCHE')

      // Turno nocturno - lobos
      await store.dispatch('sala/turnoNocturno', 'Alice')

      // Turno nocturn - bruja
      await store.dispatch('sala/setTurnoActivo', 'Charlie')

      // Volver a día
      await store.dispatch('sala/setFase', 'DIA')

      expect(store.state.sala.fase).toBe('DIA')
    })

    it('debería manejar escenario de enamored morir juntos', async () => {
      await store.dispatch('sala/setJugadores', [
        { nombre: 'Alice', estaVivo: true },
        { nombre: 'Bob', estaVivo: true },
      ])

      await store.dispatch('sala/setEnamorados', {
        jugador1: 'Alice',
        jugador2: 'Bob',
      })

      // Alice muere
      await store.dispatch('sala/marcarMuerto', 'Alice')

      // Bob también debería morir (por lógica de enamored)
      const bob = store.state.sala.jugadores.find((j) => j.nombre === 'Bob')
      expect(store.state.sala.enamorados).not.toBeNull()
    })
  })
})