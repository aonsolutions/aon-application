#!/bin/sh

cd /usr/share/java/aon.mipf
pwd
export DISPLAY=:99
java -Xmx256m -classpath ./mipf13pdf.jar es.aeat.mipf.mi13.Mipfj $1 $2 $3 $4 $5 $6



