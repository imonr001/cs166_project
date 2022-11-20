#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$CLASSPATH:/Users/ivan/Downloads/postgresql-42.5.0.jar

# compile the java program
javac -d $DIR/../classes $DIR/../src/Retail.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/Users/ivan/Downloads/postgresql-42.5.0.jar Retail "ivan_db" "5432" "ivan"

