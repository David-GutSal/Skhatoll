export default {
  namespaced: true,

  state: () => ({
    mensaje: null,
    tipo: 'info',
    visible: false,
  }),

  mutations: {
    MOSTRAR(state, { mensaje, tipo = 'info' }) {
      state.mensaje = mensaje
      state.tipo = tipo
      state.visible = true
    },
    OCULTAR(state) {
      state.visible = false
    },
  },

  actions: {
    mostrar({ commit, state }, { mensaje, tipo = 'info' }) {
      if (state.visible) {
        commit('OCULTAR')
        setTimeout(() => commit('MOSTRAR', { mensaje, tipo }), 50)
      } else {
        commit('MOSTRAR', { mensaje, tipo })
      }
    },
    ocultar({ commit }) {
      commit('OCULTAR')
    },
  },

  getters: {
    mensaje: (state) => state.mensaje,
    tipo: (state) => state.tipo,
    visible: (state) => state.visible,
  },
}