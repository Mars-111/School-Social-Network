package ws

import (
	"net/http"
	"time"

	"github.com/gorilla/websocket"
	"ws-service/internal/session"
)

// upgrader настраивает CORS и параметры буферов
var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin:     func(r *http.Request) bool { return true },
}

// NewWSHandler возвращает HTTP handler для WebSocket
func NewWSHandler(sm *session.SessionManager) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		// 1. Upgrade HTTP → WebSocket
		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			http.Error(w, "Cannot upgrade", http.StatusBadRequest)
			return
		}

		// 2. Извлечь userID (например, из JWT)
		userID, err := extractUserID(r)
		if err != nil {
			conn.Close()
			return
		}

		// 3. Создать и зарегистрировать сессию
		sess := session.NewUserSession(generateSessionID(), userID, conn)
		sm.Register(sess)

		// 4. Запустить read loop
		go func() {
			defer func() {
				sm.Unregister(conn)
				conn.Close()
			}()

			conn.SetReadLimit(512)
			conn.SetReadDeadline(time.Now().Add(60 * time.Second))
			conn.SetPongHandler(func(string) error {
				conn.SetReadDeadline(time.Now().Add(60 * time.Second))
				return nil
			})

			for {
				_, message, err := conn.ReadMessage()
				if err != nil {
					break
				}
				// TODO: обработать `message` (subscribe, unsubscribe, send)
			}
		}()
	}
}

// генерация sessionID и парсинг userID — реализовать по необходимости
func generateSessionID() string                    { /* UUID */ return "" }
func extractUserID(r *http.Request) (int64, error) { /* JWT */ return 0, nil }
