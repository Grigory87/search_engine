<div align="center">

# search_engine
## Создание индекса сайтов. Поиск страниц по запросу
</div>
<div>
  
### Используются технологии: SpringBoot, SQL, Hibernate, Lombok, ForkJoinPool, Thread.

>#### Обязательные настройки в файле application.yaml:
  1. Подключение к базе MySQL, пример:
  - server:
    - port: 8080
  - spring:
    - datasource:
      - url: jdbc:mysql://localhost:3306/search_engine2
      - username: root
      - password: 1111
  - jpa:
    - generate-ddl: true
    - properties:
      - hibernate:
        - dialect: org.hibernate.dialect.MySQL8Dialect
        - ddl-auto: update
  
  2. Список сайтов, пример:
  - indexing-settings:
    - sites:
      - -url: https://www.playback.ru
      - name: PlayBack.Ru
  
  3. UserAgent:
  - connect:
    - user-agent: Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6
    - referrer: http://www.google.com
    - cookie:
      - auth: auth
      - token: token
    - timeout: 100000
    - ignoreHttpErrors: true
    - ignoreContentType: true
    - maxBodySize: 0

>#### Настройки SQL.
  - Создать базу.
    - CREATE DATABASE search_engine2;
  - Таблицы создаст программа при запуске
</div>
<div>
  
>#### Работа программы.
После запуска программа работает по адресу http://localhost:8080
##### Страницы:
  - Страница DASHBOARD - Информация общая и по сайтам.
  - Страница MANAGEMENT - Старт / Стоп индексации, проиндексировать отдельную страницу.
  - Страница SEARCH - Поиск по индексу.
</div>
