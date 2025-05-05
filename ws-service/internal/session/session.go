package session

import (
	"sync"

	"github.com/gorilla/websocket"
)

type UserSession struct {
	SessionID string
	UserID    int64
	Conn      *websocket.Conn

	mu         sync.Mutex
	subscribed map[string]struct{} // chatID → пустая структура (как Set)
}

func NewUserSession(sessionID string, userID int64, conn *websocket.Conn) *UserSession {
	return &UserSession{
		SessionID:  sessionID,
		UserID:     userID,
		Conn:       conn,
		subscribed: make(map[string]struct{}),
	}
}

func (s *UserSession) Send(message []byte) error {
	// защищаем WriteMessage от concurrent write
	s.mu.Lock()
	defer s.mu.Unlock()
	return s.Conn.WriteMessage(websocket.TextMessage, message)
}

func (s *UserSession) Subscribe(chatID string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	s.subscribed[chatID] = struct{}{}
}

func (s *UserSession) Unsubscribe(chatID string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	delete(s.subscribed, chatID)
}

func (s *UserSession) IsSubscribed(chatID string) bool {
	s.mu.Lock()
	defer s.mu.Unlock()
	_, ok := s.subscribed[chatID]
	return ok
}
