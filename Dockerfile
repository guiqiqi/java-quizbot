FROM jelastic/maven:3.9.5-openjdk-21
COPY . /app
WORKDIR /app
RUN mvn package -Dmaven.test.skip
CMD mvn exec:java -Dexec.mainClass="quizbot.Application"
