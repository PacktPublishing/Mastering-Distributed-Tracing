#all: compose chatapi webapp storage giphy

CH6_EX ?= 1
KSEND ?= sync

GROUP_ID := com.packt.distributed-tracing-chapter-05
JAEGER := \
		-DJAEGER_SAMPLER_TYPE=const \
		-DJAEGER_SAMPLER_PARAM=1
JAEGER_CHAT_API := -DJAEGER_SERVICE_NAME=chat-api-$(CH6_EX) $(JAEGER) -DKSEND=$(KSEND)
JAEGER_STORAGE  := -DJAEGER_SERVICE_NAME=storage-service-$(CH6_EX) $(JAEGER)
JAEGER_GIPHY    := -DJAEGER_SERVICE_NAME=giphy-service-$(CH6_EX) $(JAEGER)

compose:
	docker-compose up
chatapi:
	mvn $(JAEGER_CHAT_API) spring-boot:run -pl $(GROUP_ID):chat-api-$(CH6_EX)
storage:
	mvn $(JAEGER_STORAGE) spring-boot:run -pl $(GROUP_ID):storage-service-$(CH6_EX)
giphy:
	mvn $(JAEGER_GIPHY) spring-boot:run -pl $(GROUP_ID):giphy-service-$(CH6_EX)
build-webapp:
	rm -rf webapp/dist webapp/public/*
	(cd webapp && yarn && yarn build)
	cp webapp/dist/{app.*.js,favicon.*.ico,index.html} webapp/public/
