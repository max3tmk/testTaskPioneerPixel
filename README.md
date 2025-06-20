Pioneer Pixel

Проект представляет собой backend-сервис, реализующий учёт пользователей, их аккаунтов, телефонов и email-адресов, с
возможностью фильтрации, поиска, денежного перевода и индексации данных в Elasticsearch.

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

3. Перезапустите схему БД вручную

Удалите схему в БД (если была), затем запустите приложение в среде (например, IntelliJ) через PioneerPixelApplication.

Миграции (Flyway) автоматически создадут таблицы и данные.

4. Проверьте доступ

API будет доступно по адресу: http://localhost:8080
Elasticsearch: http://localhost:9200

Используемые технологии:
Язык Java 17
Backend Framework Spring Boot 3.5
ORM Spring Data JPA (Hibernate)
БД PostgreSQL
Миграции Flyway
Кэш Spring Cache (SimpleCacheManager)
Безопасность JWT (на основе email/phone + password)
Документация Swagger (springdoc-openapi)
Генерация данных Java Faker
Поиск Elasticsearch (REST-клиент + Spring Data Elasticsearch)
Docker PostgreSQL + Elasticsearch
Тесты JUnit (Spring Boot Starter Test)

Основной функционал

3 слоя: API -> Service -> DAO (Repository)

Пользователь:

имеет строго 1 аккаунт

может иметь множество email и телефонов (минимум 1 каждого)

Поиск пользователей с фильтрами: name, email, phone, dateOfBirth + пагинация

Автоматическая индексация пользователей в Elasticsearch

Выбор механизма поиска (jpa или elastic) через application.yml

Повышение баланса каждые 30 секунд на +10% (макс. 207% от начального)

Безопасный банковский перевод между пользователями с полной валидацией

Операции над email/телефоном: добавление, удаление (принадлежащих текущему пользователю)

Конфигурация application.yml

search:
type: jpa # или elastic

Принятые решения и особенности

Нет регистрации через API: пользователи создаются через миграции.

Автоматическая индексация: при изменении email или телефона данные пересохраняются в Elasticsearch.

Изоляция поиска: используется паттерн делегирования (DelegatingUserSearchService), переключающийся между JPA и
Elasticsearch.

Docker для локальной разработки: минимальная конфигурация, Elasticsearch запускается в single-node режиме.

Кэширование: включено на уровне поиска пользователей.

Swagger UI: доступен по адресу http://localhost:8080/swagger-ui.html

Дополнительная колонка initial_balance: введена в таблицу ACCOUNT, чтобы правильно рассчитывать прирост до 207% от
начальной суммы.

Поиск

Поиск поддерживает следующие фильтры:

- 'name' — фильтр по имени, с использованием шаблона LIKE 'value%';
- 'email' — полное совпадение;
- 'phone' — полное совпадение;
- 'dateOfBirth' — поиск пользователей, у которых дата рождения больше указанной (фильтрация '> date');
- 'page', 'size' — параметры пагинации.

Пример:
http://localhost:8080/API/users/search?name=Jo&page=0&size=10

Аутентификация

Аутентификация осуществляется по email или телефону через /api/auth/login
Для доступа к большинству API-методов требуется передавать JWT-токен в заголовке 'Authorization: Bearer <token>'.
Эндпоинт /API/users/search не требует авторизации

Swagger UI

Swagger доступен по адресу:
http://localhost:8080/swagger-ui/index.html

Генерация пользователей

При генерации пользователей используется Java Faker для генерации случайного имени и даты рождения. Email и Phone
генерируются по следующему принципу:
Email: email<user_id>@email.com
Phone: 37520..0<user_id> - нулей добавляется столько, чтобы вместе с user_id было 7 цифр
Так как генерируется определенное количество пользователей (app:user-generator:count: 50 заданное в application.yml) то
при повторной генерации они будут повторяться.

Тесты

Unit-тесты метода transfer(...) в AccountServiceImpl

1 transfer_shouldTransferMoneyBetweenAccounts. Проверяет успешный перевод денег между двумя разными пользователями:

- Ищется текущий авторизованный отправитель.
- Проверяется корректное списание и зачисление средств.
- Проверяется вызов accountDao.save() для обоих аккаунтов.
  2 transfer_shouldThrowExceptionIfSenderNotFound. Проверяет, что при отсутствии отправителя в базе выбрасывается
  EntityNotFoundException.
  3 transfer_shouldThrowExceptionIfReceiverNotFound. Проверяет, что при отсутствии получателя в базе выбрасывается
  EntityNotFoundException.
  4 transfer_shouldThrowExceptionIfInsufficientFunds. Проверяет, что при недостаточном балансе у отправителя операция
  прерывается с IllegalStateException.
  5 transfer_shouldThrowSecurityExceptionIfTransferFromNotCurrentUser. Проверяет, что пользователь может переводить
  деньги только со своего аккаунта.
- При попытке перевести с чужого аккаунта выбрасывается SecurityException.
  6 transfer_shouldThrowExceptionIfTransferToSelf. Проверяет, что перевод самому себе невозможен.
- Выбрасывается IllegalArgumentException.
  7 transfer_shouldThrowExceptionIfAmountIsNonPositive. Проверяет, что нельзя перевести 0 или отрицательную сумму.
- Выбрасывается IllegalArgumentException.

Интеграционный тест UserApiIntegrationTest

Тестирует API поиска пользователей через /API/users/search с использованием Testcontainers и MockMvc.
Проверяет следующие аспекты:

- Корректную интеграцию с PostgreSQL в контейнере.
- Отработку фильтров по имени, дате рождения, email и телефону.
- Правильную работу пагинации.
- Ожидаемый формат и содержимое ответа.
- Запуск и завершение всего Spring-контекста (с учетом безопасности, Elasticsearch и других бинов).