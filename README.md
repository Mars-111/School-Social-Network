<h1>FukkoMe</h1> 
<h2>FukkoMe - это мессенджер совмещающий в себе простоту и стиль.</h2>

* FukkoMe - это оригинальное название, придуманное и использованное для этого проекта начиная с 26.06.2025. Любое комерчиское использование этого названия требует отельного разрешения от автора.
* Автор проекта: Арсений Фильков Федорович (Mars)


<h3>Структура проекта</h3>
Сервисы:
- chat service (логика сообщений, чатов, событий)
- identity service (логика авторизации и хранения пользоватея)
- timeline master service (логика нумерации последовательности в пределах чата)
- socket service (логика рассылки сообщенй по сокетам)
- store file service (логика хранения и использования файлов)


//Все что ниже требует доработки

Для работы необходимо запустить 2 докер  контейнера:
1. image файл по School-Social-Network/docker/docker-compose/(я предпочитаю postgress версию)
2. docker run --name chats-service-db -p 5432:5432 -e POSTGRES_DB=chats -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=123 postgres:16

Далее запустить Socket-broker-service и chats-service, а так же клиент web-client. (keyloak-service еще в разработке).

<h1>Kafka:</h1>
Файл docker-compose.yml ищите в docker папке.

Топики:
<ul>
  <li>docker exec -it broker-1 opt/kafka/bin/kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic messages --partitions 10 --replication-factor 2 --config retention.ms=2000000 --config cleanup.policy=delete</li>
  <li>docker exec -it broker-1 opt/kafka/bin/kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic events --partitions 10 --replication-factor 2 --config retention.ms=2000000 --config cleanup.policy=delete</li>
</ul>
