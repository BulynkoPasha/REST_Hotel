# Property View: Hotels REST API

RESTful-приложение для работы с отелями.

**Стек:** Java 21, Spring Boot 3.3, Spring Data JPA, Liquibase, H2, MapStruct, springdoc (Swagger).

## Запуск

```bash
mvn spring-boot:run
```

- Порт: `8092`
- Общий префикс всех методов: `/property-view`
- Swagger UI: http://localhost:8092/property-view/swagger-ui.html
- H2-консоль: http://localhost:8092/property-view/h2-console
  (JDBC URL: `jdbc:h2:mem:hotels`, пользователь `sa`, пароль пустой)


## API

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/property-view/hotels` | Список всех отелей (краткая информация) |
| GET | `/property-view/hotels/{id}` | Расширенная информация об отеле |
| GET | `/property-view/search` | Поиск по `name`, `brand`, `city`, `country`, `amenities` |
| POST | `/property-view/hotels` | Создание отеля |
| POST | `/property-view/hotels/{id}/amenities` | Добавление списка удобств к отелю |
| GET | `/property-view/histogram/{param}` | Количество отелей по `brand`, `city`, `country`, `amenities` |


## Демо-данные

При старте Liquibase может загрузить 3 тестовых отеля (Minsk, Moscow, Mogilev) с удобствами.
Они лежат в `db/002-seed-demo-data.yaml` и помечены контекстом `demo`.

В `application.yml` сейчас стоит:

```yaml
spring:
  liquibase:
    contexts: demo
```

Поэтому **демо-данные загружаются при каждом запуске**, и GET-методы сразу возвращают результат.
Новые отели, созданные через `POST`, получают `id` начиная с 4, а демо-отели учитываются в гистограммах и поиске.

Чтобы запускать приложение с пустой базой, замените значение на любое другое, например:

```yaml
spring:
  liquibase:
    contexts: prod
```


## Переключение на PostgreSQL

Схема описана средствами Liquibase без H2-специфики, поэтому она работает на PostgreSQL без изменений.
Для перехода нужно поменять конфигурацию.

1. В `pom.xml` заменить драйвер на PostgreSQL:

```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
```

2. В `application.yml` изменить параметры подключения:

```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/hotels
       username: postgres
       password: postgres
       driver-class-name: org.postgresql.Driver
```
