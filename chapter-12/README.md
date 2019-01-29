# Gathering Insights Through Data Mining

This exercise shows a simple Apache Flink job performing feature extraction from traces.

Full instructions are in the book, but as a quick summary:
  * `docker-compose up` brings up the Jaeger backend, Kafka, Elasticsearch, and Kibana
  * the main Java program runs the Flink job that stores extracted features back into Elasticsearch
  * the sample traces are generated with [microsim](https://github.com/yurishkuro/microsim) simulator
  * resultes can be viewed/plotted in Kibana
