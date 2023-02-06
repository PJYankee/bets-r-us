@echo off


java -jar -Xdebug  -agentlib:jdwp="transport=dt_socket,server=y,suspend=n,address=5005" spring-boot-0.0.1-SNAPSHOT.jar

