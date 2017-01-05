#!/usr/bin/env bash

#
# command line runner for the Credit Suisse Trial app
#

function cleanup() {
#    kill ${SERVER_PID} ${CLIENT_PID}
    kill -9 ${SERVER_PID}
}

trap cleanup EXIT

# mvn -DskipTests compile
mvn compile test

mvn jetty:run & SERVER_PID=$!

while ! nc localhost 8080 > /dev/null 2>&1 < /dev/null; do
    echo "$(date) - waiting for server at localhost:8080..."
	echo "$SERVER_PID"
    sleep 1  
done

while ! nc localhost 8080 > /dev/null 2>&1 < /dev/null; do
	curl -i -X GET -H Accept:application/json "http://localhost:8080/credit_suisse_trial/"
   sleep 5  
done
#CLIENT_PID=$!
cleanup
