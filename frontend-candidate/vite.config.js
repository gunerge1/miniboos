import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: true,
    port: 5173,
    proxy: {
      // dev环境：前端请求转给本机后端（生产走VITE_API_BASE直连）
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
