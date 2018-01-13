[![Build Status](https://travis-ci.org/floschne/NLP_ProjectLSIRES.svg?branch=master)](https://travis-ci.org/floschne/NLP_ProjectLSIRES)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/018e4ba82d1b45999f59ba5a693b94c8)](https://www.codacy.com/app/floschne/NLP_Project?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=floschne/NLP_Project&amp;utm_campaign=Badge_Grade)

# NLP Project LSIRES
Natural Language Project - Language Specific Information Retrieval with ElasticSearch
This is students M.Sc. project on NLP lecture from University of Hamburg
## Basic Idea
The basic idea is to detect the language of a query and get query results of ElasticSearch only in the detected language.

## How To Run the Webinterface
* Run ```cd dev/web/ && mvn spring-boot:run```
* Open [http://127.0.0.1:8080](http://127.0.0.1:8080)

## Testing
Run ```mvn clean verify```
