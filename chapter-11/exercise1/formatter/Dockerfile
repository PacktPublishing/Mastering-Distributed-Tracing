FROM openjdk:alpine

ENV APP_HOME /app/

COPY target/formatter-1-0.0.1-SNAPSHOT.jar $APP_HOME/formatter.jar

EXPOSE 8082

# Env variables must be set when starting the container:
#     jaeger_host = logstash
#     logstash_host = logstash
CMD java \
    -DJAEGER_ENDPOINT=http://${jaeger_host:-jaeger}:14268/api/traces \
    -Dlogstash.host=${logstash_host:-logstash} \
    -jar ${APP_HOME}/formatter.jar
