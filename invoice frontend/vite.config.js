import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3001,
    open: true,
    proxy: {
      '/api': 'http://localhost:8081',
    },
  },
  build: {
    outDir: '../invoice backend/src/main/resources/static',
    emptyOutDir: true,
  },
})