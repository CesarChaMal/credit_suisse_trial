#!/usr/bin/env bash

#
# command line runner for the Credit Suisse Trial
#

function cleanup() {
    kill ${SERVER_PID} ${CLIENT_PID}
}

trap cleanup EXIT

mvn compile test

mvn jetty:run & SERVER_PID=$$

while ! nc localhost 8080 > /dev/null 2>&1 < /dev/null; do
    echo "$(date) - waiting for server at localhost:8080..."
    sleep 5  
done

curl -i -X GET -H Accept:application/json "http://localhost:8080/credit_suisse_trial/"
CLIENT_PID=$$
cleanup
