#!/bin/bash

while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8080/actuator)" != "200" ]]; do sleep 5; done