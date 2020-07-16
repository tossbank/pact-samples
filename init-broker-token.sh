#!/bin/bash

BROKER_TOKEN=$1
FILES="frontend/publish.js server1/build.gradle.kts server2/build.gradle.kts server2/src/test/resources/application.yml"

# linux 에서는 -i 뒤의 ''를 제거해야한다.
sed -i '' "s/<pact-broker-token>/$BROKER_TOKEN/g" $FILES
