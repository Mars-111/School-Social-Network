import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from './auth/AuthContext.tsx'
import { BrowserRouter } from 'react-router-dom'
import { CacheProvider } from './general/components/CacheContext.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <CacheProvider>
          <App />
        </CacheProvider>
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>
)