pet-проект.
School Social Network - это соцаильная сеть с уклоном на помощь в образовательном процеесе. 

Для работы необходимо запустить 2 докер  контейнера:
1. image файл по School-Social-Network/docker/docker-compose/(я предпочитаю postgress версию)
2. docker run --name chats-service-db -p 5432:5432 -e POSTGRES_DB=chats -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=123 postgres:16

Далее запустить Socket-broker-service и chats-service, а так же клиент web-client. (keyloak-service еще в разработке).
