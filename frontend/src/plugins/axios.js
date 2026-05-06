import axios from 'axios'
import store from '@/store'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

axiosInstance.interceptors.request.use((config) => {
  const token = store.getters['auth/token']
  console.log("INTERCEPTOR TOKEN:", token)
  if (token) {
     config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch('toast/mostrar', {
        mensaje: 'Tu sesión ha expirado. Vuelve a iniciar sesión.',
        tipo: 'error',
      })
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
