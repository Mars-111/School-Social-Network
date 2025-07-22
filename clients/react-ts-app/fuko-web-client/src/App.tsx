import './App.css'
import { Routes, Route } from 'react-router-dom'
import UniversalError from './general/errors/UniversalError'
import { Login } from './auth/components/Login'
import { ProtectedOutlet } from './auth/components/ProtectedComponents'
import { UserSettings } from './users/components/UserSettings'

function App() {    
    return (
        <Routes>
            <Route path="/" element={<h1>Hello World</h1>} />
            <Route path="/app" element={<ProtectedOutlet loadComponent={<h1>Loading...</h1>} />}>
                <Route index element={<h1>Welcome to the App</h1>} />
                <Route path="chats" element={<h1>Chats Page</h1>} />
                <Route path="chats/:chatId" element={<h1>Chat Details</h1>} />
                <Route path="user/:id" element={<h1>User Profile</h1>} />
                <Route path="user/me" element={<h1>My Profile</h1>} />
                <Route path="settings" element={<h1>Settings Page</h1>} />
                <Route path="settings/profile" element={<UserSettings />} />
            </Route>
            <Route path="/login" element={<Login/>} />
            <Route path="*" element={<UniversalError errorCode={404} />} />
        </Routes>
    )
}

export default App
