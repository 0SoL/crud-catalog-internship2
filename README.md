# Catalog
## Сборка в jar
### Сборка будет хранится в targer/.jar 
mvn -DskipTests package 
### Чтобы запустить проект через jar 
java -jar target/catalog-0.0.1-SNAPSHOT.jar 
### Для передачи переменных для datasource, я использовал export
export DATASOURCE_HOST=localhost \
export DATASOURCE_PORT=5432 \
export DATASOURCE_NAME=db_name \
export DATASOURCE_USERNAME=username \
export DATASOURCE_PASSWORD=secret 
### Для тестирования и ознокамления с endpoint'ами можно воспользоваться Swagger UI 
http://localhost:8080/swagger-ui/index.html

## Запуск приложения в Docker
### Для сборки образа надо воспользоваться командой, Докер сам соберет образы и запустит контейнер
docker compose up 

### Проверка логов
docker compose logs -f app 
### Остановка 
docker compose down

### либо проверить в приложении Docker
### Чтобы не хардкодить значения , они хранятся у меня в .env




