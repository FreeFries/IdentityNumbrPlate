@echo Clear out results folder of old screen shots ...
cd result
del *.*
cd ..
java -Dlog4j2.debug -Dlog4jDefaultStatusLevelDONTUSE -Dlog4j.configurationFile=//C:/Users/Internet_Use_Adult/IdeaProjects/IdentityNumbrPlate/config/log4j2.xml -Dwebdriver.gecko.driver=//C:/Users/Internet_Use_Adult/IdeaProjects/IdentityNumbrPlate/gecko/geckodriver.exe -jar ./target/IdentityNumbrPlate-1.0-SNAPSHOT.jar
