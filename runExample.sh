#!/bin/bash


gradle build jar 

java -jar build/libs/phyTreeSearcher.jar treesearch-example.properties

