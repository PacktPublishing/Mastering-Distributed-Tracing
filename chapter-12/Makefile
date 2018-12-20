run:
	./mvnw package exec:java -Dexec.mainClass=tracefeatures.SpanCountJob

es-create-mapping:
	curl \
		--header "Content-Type: application/json" \
		-X PUT \
		-d @es-create-mapping.json \
		http://127.0.0.1:9200/trace-summaries

es-drop-index:
	curl -X DELETE http://127.0.0.1:9200/trace-summaries
