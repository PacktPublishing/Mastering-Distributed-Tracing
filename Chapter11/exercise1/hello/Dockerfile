FROM openjdk:alpine

ENV APP_HOME /app/

COPY target/hello-1-0.0.1-SNAPSHOT.jar $APP_HOME/hello.jar

EXPOSE 8080

# Env variables must be set when starting the container:
#     jaeger_host = logstash
#     logstash_host = logstash
#     formatter_host = formatter-svc
#     formatter_port = 8082
CMD java \
    -DJAEGER_ENDPOINT=http://${jaeger_host:-jaeger}:14268/api/traces \
    -Dlogstash.host=${logstash_host:-logstash} \
    -Dformatter.host=${formatter_host:-formatter-1} \
    -Dformatter.port=${formatter_port:-8082} \
    -jar ${APP_HOME}/hello.jar
