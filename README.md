# [APACHE IGNITE](https://ignite.apache.org) щупаем ручками
## 1 Кластер
* автоматический мониторинг нод кластера (добавление/смерть нод нипочем)
* поиск и добавление в сети по маске
## 2 Распределенный вызов
пишем функцию, а выполняется на любой ноде кластера
полноценная балансировка нагрузки и тд
## 3 Заставить все ноды выполнить что-то
## 4 АОП не нужно есть встроенные механизмы событий
можно отслеживать вызов методов.
например добавление нового значения в кеш могут поймать все и выполнить некую логику
## 5 асинхронный API, почти весь API можно вызвать в асинхронном режиме
например вызвать функцию получить future что-то продолжать делать и по завершению проверить, а не завершилась та функция или дождаться ее выполнения или добавить слушателя
## 6 поддержка транзакций
## 7 распределенный кеш 
* держится в памяти
* можно настроить чтобы сохранялся (в данном примере данные сбрасываются на диск по накоплению изменений или/и по времени)
* настройка уровня надежности
* поддержка SQL

Работа с БД требует наличие postgres и выполнить скрипты из init.sql (настройки соединения см. com.zemrow.test.ignite.MyDataSource.java)

За основу многих примеров были использованы [исходники](https://github.com/apache/ignite)
