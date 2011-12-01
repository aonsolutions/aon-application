#!/bin/sh

grep -E "dependency" -A 3 pom.xml  |\
grep -E "Id|version" | sed -e 's/<[^>]*>\([^<]*\)<\/.*>/\1/' |\
while read groupId; read artifactId; read version; do 
	echo '<include>'$groupId':'$artifactId':'$version'</include>'; 
done
