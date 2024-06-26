# FROM debian:latest

# # Install components
# RUN apt-get update && apt-get install -y wget
# RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb
# RUN dpkg -i jdk-21_linux-x64_bin.deb
# RUN apt-get install -y mariadb-server maven

# # Define MySQL root password for application using
# ENV MYSQL_ROOT_USER=root
# ENV MYSQL_ROOT_PASSWORD=toor
# ENV MYSQL_DATABASE=quizbot

# # Copy source code file
# COPY . /app
# WORKDIR /app

# # Start application
# CMD service mariadb start && \
#     mvn exec:java -Dexec.mainClass="quizbot.Application"
FROM jelastic/maven:3.9.5-openjdk-21
COPY . /app
WORKDIR /app
CMD mvn exec:java -Dexec.mainClass="quizbot.Application"
