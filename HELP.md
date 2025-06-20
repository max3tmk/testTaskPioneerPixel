Pioneer Pixel

Проект представляет собой backend-сервис, реализующий учёт пользователей, их аккаунтов, телефонов и email-адресов, с возможностью фильтрации, поиска, денежного перевода и индексации данных в Elasticsearch.


Запуск проекта

1. Установите Docker и Docker Compose

Убедитесь, что Docker установлен и работает:
docker --version
docker-compose --version

2. Поднимите инфраструктуру

docker-compose up -d
Будут запущены:

- PostgreSQL (localhost:5432)
- Elasticsearch (localhost:9200)
- Redis (localhost:6379)


3. Перезапустите схему БД вручную

Удалите схему в БД (если была), затем запустите приложение в среде (например, IntelliJ) через PioneerPixelApplication.

Миграции (Flyway) автоматически создадут таблицы и данные.

4. Проверьте доступ

API будет доступно по адресу: http://localhost:8080
Elasticsearch: http://localhost:9200

5. Используемые технологии:
Язык		Java 17
Backend Framework	Spring Boot 3.5
ORM		Spring Data JPA (Hibernate)
БД		PostgreSQL
Миграции	Flyway
Кэш		Spring Cache (SimpleCacheManager)
Безопасность	JWT (на основе email/phone + password)
Документация	Swagger (springdoc-openapi)
Генерация данных	Java Faker
Поиск		Elasticsearch (REST-клиент + Spring Data Elasticsearch)
Docker	PostgreSQL + Elasticsearch
Тесты 	JUnit (Spring Boot Starter Test)

6. Основной функционал

3 слоя: API → Service → DAO (Repository)

Пользователь:

имеет строго 1 аккаунт

может иметь множество email и телефонов (минимум 1 каждого)

Поиск пользователей с фильтрами: name, email, phone, dateOfBirth + пагинация

Автоматическая индексация пользователей в Elasticsearch

Выбор механизма поиска (jpa или elastic) через application.yml

Повышение баланса каждые 30 секунд на +10% (макс. 207% от начального)

Безопасный банковский перевод между пользователями с полной валидацией

Операции над email/телефоном: добавление, удаление (принадлежащих текущему пользователю)

7. Конфигурация application.yml

search:
  type: jpa  # или elastic

8. Принятые решения и особенности

Нет регистрации через API: пользователи создаются через миграции.

Автоматическая индексация: при изменении email или телефона данные пересохраняются в Elasticsearch.

Изоляция поиска: используется паттерн делегирования (DelegatingUserSearchService), переключающийся между JPA и Elasticsearch.

Docker для локальной разработки: минимальная конфигурация, Elasticsearch запускается в single-node режиме.

Кэширование: Redis - включено на уровне поиска пользователей (с возможностью расширения).

Swagger UI: доступен по адресу http://localhost:8080/swagger-ui.html

Дополнительная колонка initial_balance: введена в таблицу ACCOUNT, чтобы правильно рассчитывать прирост до 207% от начальной суммы.

Для генерации пользователей используется Java Faker. Случайным образом генерируется имя и дата рождения. Пароль ренерируется в виде password<User_id>, email - email<User_id>@email.com, phone: 37520..0<User_id> - количество цифр  вместе с <User_id> = 7.