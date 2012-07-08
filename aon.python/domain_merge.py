#!/usr/bin/env jython
'''
Created on 16/04/2012

@author: ecastellano
'''
import sys

def main():
    
    # Add jars to classpath
    jars = [
            "/usr/share/java/aon-dbutils.jar",
            "/usr/share/java/mysql-connector-java.jar",
            "/usr/share/java/commons-lang.jar",
    ]
    for jar in jars:
        sys.path.append(jar)
    

    from com.code.aon.dbutils import AonDomainMerger
    from org.gjt.mm.mysql import Driver
    from java.sql import Connection
    from java.sql import DriverManager
    
    # El parametro 0 es el propio script
    targetURL = sys.argv[1];
    
    targetUser = "dbuser";
    if len(sys.argv) > 2:
        targetUser = sys.argv[2]  
    
    targetPassword = "serubd2000";
    if len(sys.argv) > 3:
        targetPassword = sys.argv[4]  
    
    
    driver = Driver()
    target = DriverManager.getConnection(targetURL,targetUser,targetPassword);
    
    for line in sys.stdin:
        if (line != None and line.strip() != ""):
            words = line.split()
            sourceURL = words[0]
            domainName = words[len(words)-1]
            sorceUser = "dbuser"
            if len(words)>2:
                sorceUser = words[1];
            sourcePassword = "serubd2000"
            if len(words)>3:
                sourcePassword = words[2]
                
            if (sourceURL != None and sourceURL.strip() != ""):
                print
                print "Trying to connect to",sourceURL,
                source = DriverManager.getConnection(sourceURL.strip(),sorceUser.strip(),sourcePassword.strip())
                print "........ connected!"
                print "Domain:",domainName
                print "--------------------------------------"        
            
             
                merger = AonDomainMerger(source, target, domainName.strip())
                merger.execute()
                source.close()
    
    target.close()


if __name__ == '__main__':
    main()
