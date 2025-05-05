package session

import (
	"github.com/gorilla/websocket"
	"sync"
)

type SessionManager struct {
	mu sync.RWMutex

	sessionsByConn map[*websocket.Conn]*UserSession
}

func NewSessionManager() *SessionManager {
	return &SessionManager{
		sessionsByConn: make(map[*websocket.Conn]*UserSession),
	}
}

func (m *SessionManager) Register(session *UserSession) {
	m.mu.Lock()
	defer m.mu.Unlock()
	m.sessionsByConn[session.Conn] = session
}

func (m *SessionManager) Unregister(conn *websocket.Conn) {
	m.mu.Lock()
	defer m.mu.Unlock()
	delete(m.sessionsByConn, conn)
}

func (m *SessionManager) GetByConn(conn *websocket.Conn) (*UserSession, bool) {
	m.mu.RLock()
	defer m.mu.RUnlock()
	session, ok := m.sessionsByConn[conn]
	return session, ok
}
