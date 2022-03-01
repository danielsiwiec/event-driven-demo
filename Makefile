setup:
	ln -sf ../../tools/pre-commit.sh .git/hooks/pre-commit
build-all:
	./gradlew clean bootJar
up: build-all
	docker-compose up
test:
	./gradlew clean test
test-request:
	curl -X POST -H "Content-Type: application/json" http://localhost:8080/orders -d "{\"items\":[123, 456]}"