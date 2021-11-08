endpoint:
    mvn spring-boot:run -f endpoint/pom.xml

start app:
    mvn package -f {{app}}/pom.xml
    java -jar {{app}}/target/{{app}}-1.0-SNAPSHOT.jar

