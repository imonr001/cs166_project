#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# compile the java program
javac -d $DIR/../classes $DIR/../src/Retail.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar Retail $USER"_DB" $PGPORT $USER

