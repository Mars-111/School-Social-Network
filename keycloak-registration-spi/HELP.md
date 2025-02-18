cd C:\develop\School-Social-Network\keycloak-registration-spi
mvn clean package
docker cp C:\develop\School-Social-Network\keycloak-registration-spi\target\keycloak-custom-authenticator-1.0-SNAPSHOT.jar docker-compose-keycloak-1:/opt/keycloak/providers/keycloak-custom-authenticator-1.0-SNAPSHOT.jar
/opt/keycloak/bin/kc.sh build