# Dictionary — REST API для управления словарями

Spring Boot приложение для создания и управления настраиваемыми словарями и их записями.

## 🚀 Технологии
- Java 17  
- Spring Boot  
- Spring Data JPA  
- MS SQL Server / PostgreSQL  
- Docker  

## 🔧 Возможности
- CRUD для словарей  
- CRUD для записей  
- Гибкая структура словаря (строки, числа, boolean)  
- JSON API  

## 📄 Пример запроса (создание словаря)
```json
POST /dictionaries
{
  "name": "products",
  "structure": "{\"fields\": [{\"name\":\"productName\",\"type\":\"string\"},{\"name\":\"price\",\"type\":\"number\"}]}"
}

## 🐳 Запуск через Docker
bash
Копировать код
docker build -t dictionary-app .
docker run -p 8080:8080 dictionary-app

## ▶ Локальный запуск
bash
Копировать код
mvn spring-boot:run

## 📞 Контакты
Лев Ермачков — levermackov78390@gmail.com
