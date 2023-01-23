# WheelBalance
Телеграм бот, помогающий отслеживать прогресс ваших сфер жизни.


  <img src="https://user-images.githubusercontent.com/112971826/213796674-7dbaa8a8-1634-4f70-aa52-503de95da7c7.jpg" width="500" title="WheelBalance">



## Краткое описание бота:

Каждый день пользователь в заданное им время получает по 3 легких вопроса, на которые он должен ответить.
Например вопрос "Насколько ты сегодня оцениваешь состояние своего здоровья?" и варианты ответов от 1 до 10.

У пользователя есть возможность узнать статистику за последнюю неделю/месяц/год о том, как меняются его показатели. Например, пользователь стал оценивать
свое здоровье на 2 балла выше относительно предыдущей недели, или наоборот, сфера "Общение с друзьями" упала на 3 балла. 

Статистика будет еще дополняться, в планах реализовать статистику в визуальном формате, дать возможность пользователям самим выбирать сферы жизни 
и на сколько вопросов они будут отвечать. 

## Разработка
Бот написан на **Java Spring**.

Для подключения к базам данных использовались **Hibernate** и **JDBC**.

База данных **PostgreSQL**.

Библиотеки с помощью **Maven**: **telegrambots**, **lombok**, **emoji-java**.

## Реализация
За основу работы была взятя библиотека Telegram Api, откуда через Update мы получаем всю информацию о пользователях. 
После получения разрешения на дальнейшую работу, мы обращаемся к пользователю с уточняющим вопросом, во сколько ему удобно получать вопросы. 
Извлекая из маски **:** число, мы добавляем в базу данных число в формате cron. 

Далее с помощью аннотации @Scheduler мы делаем проверку каждую минуту на наличие совпадений текущего времени со временем cron всех пользователей.
Если такое совпадение есть, то бот приступает к этапу задавания вопросов. 
```java @Scheduled(cron = "0 * * * * *")
        public void schedulerService () {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            String cronExpression = user.getTimeToQuestions();
            Long chat_id = user.getChatId();
            try {
                CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                Date nextExecutionTime = generator.next(new Date());
                Date currentDate = new Date();
                if (nextExecutionTime != null && nextExecutionTime.getMinutes() == currentDate.getMinutes()
                        && nextExecutionTime.getHours() == currentDate.getHours()) {
                    log.info("Время cron соответствует текущему времени");
                    checkDateAndChatId(chat_id);
                }
            } catch (IllegalArgumentException e) {
                log.info("Error: " + e);
            }
        }
    }
```

Прежде всего нам необходимо отфильтровать те вопросы, на которые пользователь уже недавно отвечал. 
Ну и конечно по его уникальному chat_id.В базу данных задается SELECT запрос, где фильтруются все 
значения по chat_id, и по тем вопросам, на которые не отвечали сегодня,вчера и
позавчера. Столбцы с датой генерируются каждый день отдельным методом с помощью аннотации @Scheduler.

После чего мы получаем список из всех вопросов, на которые наш пользователь не отвечал последние 3 дня. 
Далее берем рандомный вопрос из этого списка и задаем его пользователю. После ответа пользователя
с помощью нашей цифровой клавиатуры, 
происходит запись информации об этом вопросе и из базы данных берется следующий. Всего 3 вопроса.



> "Почему не взять сразу 3 вопроса и сделать всего 1 запрос в базу данных?" 

Изначально так и планировалось, но возникла сложность в том, где держать информацию о 1 и о 2 вопросе,
 пока пользователь отвечает например на 3? Были идеи создать временный буфер в базе данных или 
отдельную переменную, или поток, но у всех этих способов также есть свои недостатки, поэтому было 
принято решение сохранять в БД каждый вопрос по отдельности.

После сохранения вопросов пользователю высвечивается завершающее сообщение и предложение посмотреть 
статистику. В статистике пока все очень просто: среднее арифметическое за последнюю неделю. 
В дальнейшем планирую добавить сравнение недель и анализ изменений.


