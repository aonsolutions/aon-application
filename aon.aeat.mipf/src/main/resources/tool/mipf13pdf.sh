#!/bin/sh

cd /usr/share/java/aon.mipf
Xvfb -ac :99 &
PID=$!
export DISPLAY=:99
java -Xmx256m -Dfile.encoding=ISO-8859-1 -classpath ./mipf13pdf.jar es.aeat.mipf.mi13.Mipfj $1 $2 $3 $4 $5 $6
kill -9 $PID



