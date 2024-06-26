FROM jelastic/maven:3.9.5-openjdk-21
COPY . /app
WORKDIR /app
CMD mvn exec:java -Dexec.mainClass="quizbot.Application"
