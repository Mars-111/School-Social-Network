pet-проект.
School Social Network - это соцаильная сеть с уклоном на помощь в образовательном процеесе. 

Пока готов лишь чась с чатом.

Для работы необходимо запустить 2 докер  контейнера:
1. image файл по School-Social-Network/docker/docker-compose/(я предпочитаю postgress версию)
2. docker run --name chats-service-db -p 5432:5432 -e POSTGRES_DB=chats -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=123 postgres:16

Далее запустить Socket-broker-service и chats-service, а так же клиент web-client. (keyloak-service еще в разработке).

<h1>Kafka:</h1>
Файл docker-compose.yml ищите в docker папке.

Топики:
docker exec -it broker-1 opt/kafka/bin/kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic messages --partitions 10 --replication-factor 2 --config retention.ms=2000000 --config cleanup.policy=delete
docker exec -it broker-1 opt/kafka/bin/kafka-topics.sh --bootstrap-server broker-1:19092,broker-2:19092,broker-3:19092 --create --topic events --partitions 10 --replication-factor 2 --config retention.ms=2000000 --config cleanup.policy=delete
