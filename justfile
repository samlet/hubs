endpoint:
    mvn spring-boot:run -f endpoint/pom.xml

# $ just start endpoint
start app:
    mvn package -f {{app}}/pom.xml
    java -jar {{app}}/target/{{app}}-1.0-SNAPSHOT.jar

clean:
    mvn clean install -U

# $ just secas inventory
secas keyword:
	python -m sagas.ofbiz.secas all_secas | grep -i {{keyword}} | xargs -I {} python -m sagas.ofbiz.secas get_secas {}
all_services:
	python -m sagas.ofbiz.tools all_services
service name:
	python -m sagas.ofbiz.tools service_model {{name}}

c-prefabs:
	mvn compile -f prefabs/pom.xml
inst:
	mvn install -DskipTests -f common/pom.xml 

