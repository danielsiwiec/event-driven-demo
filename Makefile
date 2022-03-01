build-all:
	./gradlew clean bootJar
up: build-all
	docker-compose up
test-request:
	curl -X POST -H "Content-Type: application/json" http://localhost:8080/orders -d "{\"items\":[123, 456]}"