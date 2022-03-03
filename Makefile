setup:
	ln -sf ../../tools/pre-commit.sh .git/hooks/pre-commit
build-all:
	./gradlew clean bootJar
start-in-background: build-all
	docker-compose up -d
logs:
	docker-compose logs --follow
up: start-in-background logs
down:
	docker-compose down
service-tests:
	./gradlew clean test
wait-for-service:
	./tools/wait-for-service.sh
run-e2e:
	./gradlew clean cucumber
e2e-tests: start-in-background wait-for-service run-e2e down
test-request:
	curl -X POST -H "Content-Type: application/json" http://localhost:8080/orders -d "{\"items\":[123, 456]}"
run-perf:
	./gradlew gatlingRun
perf-tests: start-in-background wait-for-service run-perf down