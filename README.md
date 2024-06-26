# Telegram Quiz Bot

Telegram Quiz Bot is a quiz bot based on Spring JDBC and Telegram Bot API. This bot allows users to participate in random quizzes and tracks their scores.

## Features

- `/help` - Displays help message.
- `/add` - Adds a new quiz.
- `/finish` - Finishes adding a new quiz.
- `/random` - Starts a random quiz.
- `/random <tag>` - Starts a random quiz with a specified tag.
- `/score` - Queries your current score.
- `/score <tag>` - Queries the scores accumulated from questions with a specified tag.
- `/clear` - Resets your current score to 0.

## Quick Start

### Deployment

This project supports deployment using `docker-compose` or just Dockerfile if you have your own MySQL server.

1. **Deploy using Docker Compose**:
   ```
   docker-compose up -d
   ```

2. **Deploy using Dockerfile** (Requires configuring MySQL server):
   ```
   docker build -t quiz-bot .
   docker run -d quiz-bot -v <your config file path>:/app/target/classes/config.properties
   ```
   You will need to modify your config file before booting up Docker container, detail listed below.

### Configuration File

Before deploying, ensure the `config.properties` file is correctly configured:

```
database.driver = com.mysql.cj.jdbc.Driver
database.url = jdbc:mysql://localhost:3306/quizbot
database.username = root
database.password = toor
telegram.bot.name = telegram-quizbot
telegram.bot.token = 12345:ABCDE
```

### Compile from Source

If you need to deploy from source, use Maven:

```bash
mvn clean install
```

Note that before compiling, you need to edit the `./src/main/resources/development.properties` file according to the actual environment.

The project includes unit tests for the Model, DAO, and Service layers to ensure the correctness of functionalities.

## Architecture

The project uses an MVC architecture, divided into the following layers:

- **Model layer** - Defines data models.
- **DAO layer** - Data Access Object layer, used for database interactions.
- **Service layer** - Business logic layer, processes business requests.
- **Controller layer** - Control layer, receives user commands and calls the corresponding services.

## Technology Stack

- **Spring JDBC** - Used for database operations.
- **Telegram Bot API** - Used to interact with Telegram.
- **Spring Task** - Used for managing scheduled tasks.
- **Spring IoC** - Manages Beans using Java configuration files.

## Links

Here are links to this repository and related resources:

- Telegram Bot: [@PolytechQuizbot](https://t.me/PolytechQuizbot)
- GitHub Repository: [guiqiqi/PolytechQuizbot](https://github.com/PolytechQuizbot)
- Docker Hub: [dogegui/polytech-quizbot](https://hub.docker.com/repository/docker/dogegui/polytech-quizbot)

## Examples

Here are some examples:

- Help information and hint when no random questions could been found:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/no-question.png?raw=true" style="zoom: 50%;" />

- Add a new question and corresponded options:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/add-question.png?raw=true" style="zoom: 50%;" />

- Answer question selected randomly:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/answer-question.png?raw=true" style="zoom: 50%;" />

- Add another question:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/add-question-1.png?raw=true" style="zoom: 50%;" />

- Asnwer another question selected with tag randomly:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/answer-question-1.png?raw=true" style="zoom: 50%;" />

- Query and clear score:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/score.png?raw=true" style="zoom:50%;" />

- Unit test result:

  <img src="https://github.com/guiqiqi/java-quizbot/blob/master/images/test.png?raw=true" style="zoom: 33%;" />
