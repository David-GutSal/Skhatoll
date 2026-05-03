export default {
  namespaced: true,

  state: () => ({
    mensaje: null,
    tipo: 'info',
    visible: false,
    cola: [],
  }),

  mutations: {
    MOSTRAR(state, { mensaje, tipo = 'info' }) {
      state.mensaje = mensaje
      state.tipo = tipo
      state.visible = true
    },
    OCULTAR(state) {
      state.visible = false
      state.mensaje = null
    },
    ENCOLAR(state, payload) {
      state.cola.push(payload)
    },
    DESENCOLAR(state) {
      state.cola.shift()
    },
  },

actions: {
    mostrar({ commit, state, dispatch }, { mensaje, tipo = 'info' }) {
      if (state.visible) {

        commit('ENCOLAR', { mensaje, tipo })
      } else {
        commit('MOSTRAR', { mensaje, tipo })
      }
    },
 
    ocultar({ commit, state, dispatch }) {
      commit('OCULTAR')
      if (state.cola.length > 0) {
        const siguiente = state.cola[0]
        commit('DESENCOLAR')
        setTimeout(() => {
          dispatch('mostrar', siguiente)
        }, 300)
      }
    },
  },

  getters: {
    mensaje: (state) => state.mensaje,
    tipo: (state) => state.tipo,
    visible: (state) => state.visible,
    colaPendiente: (state) => state.cola.length,
  },
}