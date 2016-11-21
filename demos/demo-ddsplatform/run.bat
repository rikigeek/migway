@echo off
@echo Starting: First Try Chat application...
SET JAVAC=javac
SET JAVA=java
SET PRISMTECH_LIB=c:\Program Files\PrismTech\Vortex_v2\Device\VortexOpenSplice\6.6.2p1\HDE\x86_64.win64\jar
::pushd target\classes
::%JAVA% -cp ".;%PRISMTECH_LIB%\dcpssaj5.jar" sebi.dds.PlatformApp
::popd
::java -cp ".;slf4j-api-1.7.5.jar;slf4j-log4j12-1.7.5.jar;log4j-1.2.17.jar" -jar target\DdsiPingPong-0.0.1-SNAPSHOT.jar
java -jar target\DemoDdsPlatform.jar
dir "%PRISMTECH_LIB%"
::java -cp ".;%PRISMTECH_LIB%\dcpssaj5.jar" -jar target\migway-demo-ddsplatform-0.6.0-SNAPSHOT.jar 
pause