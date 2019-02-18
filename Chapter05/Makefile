#all: compose chatapi webapp storage giphy

KSEND ?= sync

GROUP_ID := com.packt.distributed-tracing-chapter-05
JAEGER := \
		-DJAEGER_SAMPLER_TYPE=const \
		-DJAEGER_SAMPLER_PARAM=1
JAEGER_CHAT_API := -DJAEGER_SERVICE_NAME=chat-api-1 $(JAEGER) -DKSEND=$(KSEND)
JAEGER_STORAGE  := -DJAEGER_SERVICE_NAME=storage-service-1 $(JAEGER)
JAEGER_GIPHY    := -DJAEGER_SERVICE_NAME=giphy-service-1 $(JAEGER)

LIB := lib/target/lib-0.0.1-SNAPSHOT.jar

compose:
	docker-compose up
chatapi: lib
	./mvnw $(JAEGER_CHAT_API) spring-boot:run -pl $(GROUP_ID):chat-api-1
chatapi-async1: lib
	$(MAKE) chatapi KSEND=async1
chatapi-async2: lib
	$(MAKE) chatapi KSEND=async2
storage: lib
	./mvnw $(JAEGER_STORAGE) spring-boot:run -pl $(GROUP_ID):storage-service-1
giphy: lib
	./mvnw $(JAEGER_GIPHY) spring-boot:run -pl $(GROUP_ID):giphy-service-1
build-webapp:
	rm -rf webapp/dist webapp/public/*
	(cd webapp && yarn && yarn build)
	cp webapp/dist/{app.*.js,favicon.*.ico,index.html} webapp/public/

lib: $(LIB)

$(LIB):
	./mvnw install

