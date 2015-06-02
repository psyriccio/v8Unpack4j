@echo OFF
Echo Start parsing
@java -Xms1G -Xmx2G -jar com.minimajack._v8.cf-0.0.1-SNAPSHOT-jar-with-dependencies.jar %1 ./unpacked/%1
