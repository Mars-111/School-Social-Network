@echo off
REM Navigate to the project directory
cd C:\develop\School-Social-Network\keycloak-event-listener

REM Clean and package the Maven project
mvn clean package

REM Copy the built JAR file to the Keycloak container
REM Update the container name and paths if needed
docker cp C:\develop\School-Social-Network\keycloak-event-listener\target\keycloak-event-listener-0.0.1-SNAPSHOT.jar docker-compose-keycloak-1:/opt/keycloak/providers/keycloak-event-listener-0.0.1-SNAPSHOT.jar

REM Print completion message
echo Deployment completed successfully!

REM Pause to review output
pause