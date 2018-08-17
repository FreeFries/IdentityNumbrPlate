#!/usr/bin/env bash

#jar -vtf ./target/IdentityNumbrPlate-1.0-SNAPSHOT.jar

echo Clear out results folder of old screen shots ...
rm -rI ./result/*
java -Dlog4j2.debugDISABLED -Dlog4jDefaultStatusLevelDONTUSE -Dlog4j.configurationFile=./config/log4j2.xml -Dwebdriver.gecko.driver=./gecko/geckodriver -jar ./target/IdentityNumbrPlate-1.0-SNAPSHOT.jar


#target/IdentityNumbrPlate-1.0-SNAPSHOT.jar
