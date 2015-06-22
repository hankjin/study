CLASSPATH=:../../lib/groovy-2.4.3.jar:../../lib/groovy-jmx-2.4.3.jar
JMX_OPTS=-Dcom.sun.management.jmxremote -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.port=8090 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
compile:
	javac -cp ${CLASSPATH} *.java

run:
	java ${JMX_OPTS} SysConfigMgr

client:
	java -cp ${CLASSPATH} Client

