jar xf ala-jms-1.0-SNAPSHOT-assembly.jar lib lib

export CLASSPATH=ala-jms-1.0-SNAPSHOT-assembly.jar

echo "JMS Listener : running ActiveMQ JMS Listener $('date')"
java -classpath $CLASSPATH org.ala.jms.SpringClient
