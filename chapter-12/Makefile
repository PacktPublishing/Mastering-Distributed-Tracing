MICROSIM_VER  = yurishkuro/microsim:0.2.0
MICROSIM_ARGS = -v $(PWD):/ch12:ro --net host
MICROSIM_ORIGINAL = -c /ch12/hotrod-original.json
MICROSIM_REDUCED  = -c /ch12/hotrod-reduced.json

run-span-count-job:
	./mvnw package exec:java -Dexec.mainClass=tracefeatures.SpanCountJob

es-create-mapping:
	curl \
		--header "Content-Type: application/json" \
		-X PUT \
		-d @es-create-mapping.json \
		http://127.0.0.1:9200/trace-summaries

es-drop-index:
	curl -X DELETE http://127.0.0.1:9200/trace-summaries

kibana-create-index-pattern:
	curl -XPOST 'http://localhost:5601/api/saved_objects/index-pattern' \
		-H 'Content-Type: application/json' \
		-H 'kbn-version: 6.2.3' \
		-d '{"attributes":{"title":"trace-summaries","timeFieldName":"@timestamp"}}'

microsim-help:
	docker run $(MICROSIM_VER) -h

microsim-run-once:
	docker run $(MICROSIM_ARGS) \
		$(MICROSIM_VER) \
		$(MICROSIM_ORIGINAL) \
		-w 1 -r 1

microsim-run-original:
	docker run $(MICROSIM_ARGS) \
		$(MICROSIM_VER) \
		$(MICROSIM_ORIGINAL) \
		-w 1 -s 500ms -d 5m

microsim-run-reduced:
	docker run $(MICROSIM_ARGS) \
		$(MICROSIM_VER) \
		$(MICROSIM_REDUCED) \
		-w 1 -s 500ms -d 5m
