Универсальное RESTful API для управления справочниками
Это backend-приложение разработано для выполнения CRUD-операций над справочниками и их данными. Приложение поддерживает динамическое создание таблиц для новых справочников и предоставляет гибкий интерфейс для управления структурой данных.

Технологии
Java 11
Spring Boot 2.6.0
Hibernate ORM
SQL Server с Windows аутентификацией
Maven для управления зависимостями

Запуск проекта локально
Для запуска проекта локально необходимо выполнить следующие шаги:
Шаг 1: Настройка базы данных
Установите и настройте SQL Server, учитывая настройки Windows аутентификации.
Создайте базу данных DictionaryDB.
Шаг 2: Конфигурация проекта
Склонируйте репозиторий с проектом:
git clone https://github.com/Erma4k0v/Dictionary.git
cd Dictionary
Откройте файл src/main/resources/application.properties и убедитесь, что настройки базы данных указаны следующим образом:
spring.datasource.url=jdbc:sqlserver://DESKTOP-1DTLLG9:1433;databaseName=DictionaryDB;integratedSecurity=true
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
Замените DESKTOP-1DTLLG9 на имя вашего SQL Server.
Шаг 3: Запуск приложения
Откройте терминал (или командную строку) в каталоге проекта Dictionary.
Выполните команду для запуска приложения:
mvn spring-boot:run
Шаг 4: Использование API
API будет доступно по адресу http://localhost:8080. Используйте любой API клиент (например, Postman) для тестирования эндпоинтов.

Запуск проекта с использованием Docker
Если требуется запустить приложение в Docker контейнере, выполните следующие дополнительные шаги:
Шаг 1: Создание Docker образа
Убедитесь, что Docker установлен и запущен на вашем компьютере.
Откройте терминал (или командную строку) в каталоге проекта Dictionary.
Шаг 2: Сборка Docker образа
Выполните команду для сборки Docker образа:
docker build -t dictionary-api .
Шаг 3: Запуск Docker контейнера
Выполните команду для запуска Docker контейнера:
docker run -p 8080:8080 dictionary-api
Приложение будет доступно по адресу http://localhost:8080.

API Endpoints
Управление справочниками
GET /dictionaries - получить список всех справочников.
POST /dictionaries - создать новый справочник. Пример тела запроса:
json
Копировать код
{
  "name": "products",
  "structure": "{\"fields\": [{\"name\": \"productName\", \"type\": \"string\"}, {\"name\": \"price\", \"type\": \"number\"}, {\"name\": \"inStock\", \"type\": \"boolean\"}]}"
}
DELETE /dictionaries/:name - удалить справочник и все его данные.
CRUD операции над данными справочников
GET /dictionaries/:name/records - получить список всех записей в справочнике.
POST /dictionaries/:name/records - добавить новую запись в справочник.
GET /dictionaries/:name/records/:id - получить информацию о конкретной записи.
PUT /dictionaries/:name/records/:id - обновить информацию о конкретной записи.
DELETE /dictionaries/:name/records/:id - удалить запись из справочника.
