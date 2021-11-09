endpoint:
    mvn spring-boot:run -f endpoint/pom.xml

# $ just start endpoint
start app:
    mvn package -f {{app}}/pom.xml
    java -jar {{app}}/target/{{app}}-1.0-SNAPSHOT.jar

clean:
    mvn clean install -U

