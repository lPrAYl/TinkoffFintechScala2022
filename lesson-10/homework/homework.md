# Hangman

1. Игровой сервер на akka-http

Ранее в одном из предыдущих модулей мы реализовывали игру "Виселица".
Сейчас предлагается реализовать ее online версию. Для это нужно реализовать:
* Игровую логику
  1. ru.tinkoff.homework10.hangman.storage.ImMemoryGameStorage
  1. ru.tinkoff.homework10.hangman.logic.PlayerGameServiceImpl
  1. ru.tinkoff.homework10.hangman.logic.GameServiceImpl
* Json Api для игроков (`ru.tinkoff.homework10.hangman.api.GameApi`)
    
    В качестве спецификации api нужно опираться на `ru.tinkoff.homework10.hangman.api.base.GameApiSpecBase` 
    и интеграционный тест `ru.tinkoff.homework10.hangman.base.HangmanISpecBase`
  
* Json Api для администраторов (`ru.tinkoff.homework10.hangman.api.AdminApi`)

    В качестве спецификации api нужно опираться на `ru.tinkoff.homework10.hangman.api.base.AdminApiSpecBase` и
    интеграционный тест `ru.tinkoff.homework10.hangman.base.HangmanISpecBase`
  
Для реализации http-слоя **НЕОБХОДИМО** использовать akka-http.
Вместо `Future` в сервисном уровне можно использовать `monix Task`, `cats IO` или `ZIO` 

2. Игровой сервер на tapir*

*Это дополнительное задания, не обязательное для выполнения*

1. Нужно реализовать http-слой для игры hangman с использованием библиотеки tapir
2. Выставить на сервере эндпоинт со swagger-ui
3. Реализовать тесты и убрать с них аннотация `@Ignore`
  * `ru.tinkoff.homework10.hangman.api.TapirAdminApiSpec`
  * `ru.tinkoff.homework10.hangman.api.TapirGameApiSpec`

Если не делаете дополнительное задание, можно удалить все что связанно с Tapir.