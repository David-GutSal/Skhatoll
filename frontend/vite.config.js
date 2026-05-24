import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

const API_TARGET = process.env.VITE_API_URL || 'http://localhost:8080'

export default defineConfig({
  plugins: [
    vue(),
    ...(process.env.NODE_ENV === 'development' ? [vueDevTools()] : []),
  ],
  define: {
    global: 'window',
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    mimeTypes: {
      '.css': 'text/css',
    },
    host: '0.0.0.0',
    allowedHosts: true,
    proxy: {
      '/auth': {
        target: API_TARGET,
        changeOrigin: true,
      },
      '/salas': {
        target: API_TARGET,
        changeOrigin: true,
      },
      '/partida': {
        target: API_TARGET,
        changeOrigin: true,
      },
      '/ws': {
        target: API_TARGET,
        ws: true,
        changeOrigin: true,
      },
      '/topic': {
        target: API_TARGET,
        ws: true,
        changeOrigin: true,
      },
      '/app': {
        target: API_TARGET,
        ws: true,
        changeOrigin: true,
      },
    },
  },
  css: {
    devSourcemap: true,
  },
})