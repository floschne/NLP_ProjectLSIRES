#!/bin/bash

if [ -z "$1" ]
  then
    echo "No server adress supplied! E.g.: 127.0.0.1"
fi


if [ -z "$2" ]
  then
    echo "No elasticsearch port supplied! E.g.: 9200"
fi


if [ -z "$3" ]
  then
    echo "No language code supplied! E.g.: de"
fi

curl -vX PUT http://$1:$2/$3 -d @index_settings_$3.json \
--header "Content-Type: application/json"
