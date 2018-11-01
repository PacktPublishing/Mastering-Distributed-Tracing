FROM openjdk:alpine

ENV APP_HOME /app/

COPY exercise1/hello/target/hello-1-0.0.1-SNAPSHOT.jar $APP_HOME/hello.jar
COPY exercise1/formatter/target/formatter-1-0.0.1-SNAPSHOT.jar $APP_HOME/formatter.jar

WORKDIR $APP_HOME

EXPOSE 8080

# Env variables must be set when starting the container:
# Always:
#     app_name = hello | formatter
# Hello:
#     formatter_host = localhost
#     formatter_port = 8080
# Formatter:
#     professor = false | true
CMD java \
    -DJAEGER_SERVICE_NAME=${app_name} \
    -DJAEGER_PROPAGATION=b3 \
    -DJAEGER_ENDPOINT=http://jaeger-collector.istio-system:14268/api/traces \
    -Dformatter.host=${formatter_host:-formatter} \
    -Dformatter.port=${formatter_port:-8080} \
    -Dprofessor=${professor:-false} \
    -jar ${app_name:?'app_name must be set'}.jar
