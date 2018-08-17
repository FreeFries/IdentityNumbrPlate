@echo Clear out results folder of old screen shots ...
cd result
del *.*
cd ..
java -Dlog4j2.debug -Dlog4jDefaultStatusLevelDONTUSE -Dlog4j.configurationFile=./config/log4j2.xml -Dwebdriver.gecko.driver=./gecko/geckodriver.exe -jar ./target/IdentityNumbrPlate-1.0-SNAPSHOT.jar
