#!/bin/sh
clear
echo "... check JDK installed"
JAVAC_VER=$(javac -version 2>&1 | sed 's/javac \(.*\)\.\(.*\)\..*/\1\2/; 1q')
[ "$JAVAC_VER" -ge 16 ] && { 
	echo "ok, JDK is 1.6 or newer"
	echo "...make directory structure"
	mkdir filestorage
	cp myserver/*.java filestorage/

	echo "...compile source"
	javac -verbose myserver/*.java

	echo "...create MANIFEST file"
	echo "Main-Class: myserver.java_http_server" > manifest.txt

	echo "...build jar file"
	jar -cvfm MyServer.jar manifest.txt myserver/*.class

	echo "...cleanig"
	rm *.txt myserver/*.class

	echo "...done!\n"
	echo "Current directory:"
  pwd
	ls -all *.jar
	echo "\nNow you can start server: $ java -jar MyServer.jar\n" 
} || {
	echo "\nNo JDK or it's too old... Please install JDK >= 1.6!\n"
}


