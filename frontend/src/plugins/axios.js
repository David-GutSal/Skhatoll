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

export default axiosInstance
