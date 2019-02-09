GROUP_ID := com.packt.distributed-tracing-chapter-11
HELLO_SVC := spring-boot:run -pl $(GROUP_ID):hello-1 -Dlogstash.host=localhost
FORMATTER_SVC := spring-boot:run -pl $(GROUP_ID):formatter-1 -Dlogstash.host=localhost
CLIENT_SVC := spring-boot:run -pl $(GROUP_ID):client-1 -Dlogstash.host=localhost
CLIENT_V1 := $(CLIENT_SVC) \
	-Dclient.version=v1 \
	-Dfailure.location=hello-1 \
	-Dfailure.rate=0.2
CLIENT_V2 := $(CLIENT_SVC) \
	-Dclient.version=v2 \
	-Dfailure.location=formatter-1 \
	-Dfailure.rate=0.1

dockerhost:
	@echo export DOCKERHOST=$$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $$2 }' | cut -f2 -d: | head -n1)

install:
	./mvnw install

hello:
	./mvnw $(HELLO_SVC)

formatter:
	./mvnw $(FORMATTER_SVC)

client1:
	./mvnw $(CLIENT_V1)

client2:
	./mvnw $(CLIENT_V2)

index-pattern:
	curl -XPOST 'http://localhost:5601/api/saved_objects/index-pattern' \
		-H 'Content-Type: application/json' \
		-H 'kbn-version: 6.2.3' \
		-d '{"attributes":{"title":"logstash-*","timeFieldName":"@timestamp"}}'
