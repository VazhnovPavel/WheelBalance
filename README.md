# WheelBalance
Телеграм бот, помогающий отслеживать прогресс ваших сфер жизни.
![imgonline-com-ua-Resize-S91DluQ9tkDWa1g](https://user-images.githubusercontent.com/112971826/213795475-508ee744-f59f-4911-a59c-b106b7d02bac.jpg)

Краткое описание бота:

Каждый день пользователь в заданное им время получает по 3 легких вопроса, на которые он должен ответить.
Например вопрос "Насколько ты сегодня оцениваешь состояние своего здоровья?" и варианты ответов от 1 до 10.

У пользователя есть возможность узнать статистику за последнюю неделю/месяц/год о том, как меняются его показатели. Например, пользователь стал оценивать
свое здоровье на 2 балла выше относительно предыдущей недели, или наоборот, сфера "Общение с друзьями" упала на 3 балла. 

Статистика будет еще дополняться, в планах реализовать статистику в визуальном формате, дать возможность пользователям самим выбирать сферы жизни 
и на сколько вопросов они будут отвечать. 

# Разработка
Бот написан на Java Spring.
Для подключения к базам данных использовались Hibernate и JDBC. 
База данных PostgreSQL.
Библиотеки с помощью Maven: telegrambots, lombok, emoji-java.



