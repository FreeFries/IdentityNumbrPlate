#!/usr/bin/env bash
mvn versions:display-dependency-updates
read -p "Press Any Key to continue MavenBuild"
mvn validate clean compile package


