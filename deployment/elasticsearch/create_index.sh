#!/bin/bash

errorFlag=0
if [ -z "$1" ]
  then
    errorFlag=1
    echo "No server adress supplied! E.g.: 127.0.0.1"
fi


if [ -z "$2" ]
  then
    errorFlag=1
    echo "No elasticsearch port supplied! E.g.: 9200"
fi


if [ -z "$3" ]
  then
    errorFlag=1
    echo "No language code supplied! E.g.: de"
fi

if [ "$errorFlag" -eq 0 ]
  then
    curl -vX PUT http://$1:$2/$3 -d @index_settings_$3.json --header "Content-Type: application/json"
else
    echo "./create_index.sh <HOST> <PORT> { de, en, es }"
fi
