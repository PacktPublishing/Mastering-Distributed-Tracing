build-app:
	./mvnw install
	docker build -t hello-app:latest .
	@echo '*** make sure the right docker repository is used'
	@echo '*** on minikube run this first: eval $$(minikube docker-env)'

deploy-app:
	istioctl kube-inject -f app.yml | kubectl apply -f -
	kubectl apply -f gateway.yml
	istioctl create -f routing.yml

delete-app:
	istioctl delete -f routing.yml
	kubectl delete -f app.yml

hostport:
	@echo export GATEWAY_URL=$$(minikube ip):$$(kubectl \
		get service -n istio-system istio-ingressgateway \
		-o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')

jaeger:
	kubectl port-forward -n istio-system $$(kubectl \
		get pod -n istio-system -l app=jaeger \
		-o jsonpath='{.items[0].metadata.name}') 16686:16686

service-graph:
	kubectl -n istio-system port-forward $$(kubectl \
		get pod -n istio-system -l app=servicegraph \
		-o jsonpath='{.items[0].metadata.name}') 8088:8088

logs-hello:
	kubectl logs $$(kubectl get pod -l app=hello-svc \
		-o jsonpath='{.items[0].metadata.name}') hello-svc

logs-formatter-v1:
	kubectl logs $$(kubectl get pod -l app=formatter-svc -l version=v1 \
		-o jsonpath='{.items[0].metadata.name}') formatter-svc

logs-formatter-v2:
	kubectl logs $$(kubectl get pod -l app=formatter-svc -l version=v2 \
		-o jsonpath='{.items[0].metadata.name}') formatter-svc
