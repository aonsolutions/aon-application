#!/usr/bin/env python

from aonAdmin.aonException import AonException
from aonAdmin.arguments import Arguments
from subprocess import Popen, PIPE
import MySQLdb
import sys
import zipfile


class updateDatabases:
    
#    AON_MASTER_JAR = "/mnt/iNetServer.x86_64/usr/share/java/aon-master.jar" 
    AON_MASTER_JAR = "/home/ecastellano/.m2/repository/com/code/aon/aon-master/7.0-SNAPSHOT/aon-master-7.0-SNAPSHOT.jar"
    UPDATE_SCRIPT_PREFIX = "com/code/aon/master/update/update.database."
    UPDATE_SCRIPT_SUFFIX = ".sql"

    def __init__(self,arguments):
        self.__arguments=arguments
        self.__zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')
        self.__versions=[]
        self.__fill_update_scripts()
        self.last_index = len(self.__versions) - 1
        
    def get_versions(self):
        return self.__versions
     
    def __fill_update_scripts(self):
        for name in self.__zf.namelist():
            if name.startswith(self.UPDATE_SCRIPT_PREFIX):
                version = name.strip(self.UPDATE_SCRIPT_PREFIX)
                version = version.strip(self.UPDATE_SCRIPT_SUFFIX)
                self.get_versions().append(version) 
        
        self.get_versions().sort(self.tcmp)
        print self.get_versions()

    def tcmp(self,a, b):
        a = a.split(".")
        b = b.split(".")
        for i in range(3): 
            if int(a[i]) != int(b[i]):
                if int(a[i]) < int(b[i]):
                    return -1
                else:
                    return 1

    def update(self):
        conn = None
        try:
            print "Databases must be updated to more than ",self.get_versions()[self.last_index],"version"
            print 
                        
            conn = MySQLdb.connect(host=self.__arguments.get_host(),port=self.__arguments.get_port(),user=self.__arguments.get_user(),passwd=self.__arguments.get_passwd())
            databases = conn.cursor()
            databases.execute("show databases;")
            rows = databases.fetchall()
            for row in rows:
                database = row[0]
                print database,
                c = MySQLdb.connect(host=self.__arguments.get_host(),port=self.__arguments.get_port(),user=self.__arguments.get_user(),passwd=self.__arguments.get_passwd(),db=database)
                table_cur = c.cursor()
                table_cur.execute("show tables LIKE 'db_version'")
                table_row = table_cur.fetchone()
                table_cur.close()
                if table_row != None:
                    version_cur = c.cursor()
                    version_cur.execute("SELECT version_number FROM db_version;")
                    version_number = version_cur.fetchone()    
                    version_cur.close()
                    print " \t\t current Version ..: " + version_number[0],
                    current_index = -1
                    if self.__versions.count(version_number[0]) > 0:
                        current_index = self.__versions.index(version_number[0])
                        print "current_index..: ", current_index
                        print "first index..: ", self.__versions.index("1.0.0")
                    if current_index == -1:
                        print "    no update is needed!"
                    else:    
                        print "    update is needed!",(self.last_index + 1 - current_index),"scripts must be run." 
                        self.update_database(database,current_index)
                else:
                    print "\t\thas no 'db_version' table" 
            databases.close()
            
            if self.__arguments.is_verbose_enabled():
                print "commit ..... "
            conn.commit()
            if self.__arguments.is_verbose_enabled():
                print "    Done!"
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print "-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print e.errno,e.errmsg
            print "Exit!"
            sys.exit(e.errno)

    def update_database(self,database, current_index):
        for i in range(current_index,self.last_index + 1):
            name = self.UPDATE_SCRIPT_PREFIX + self.get_versions()[i] + self.UPDATE_SCRIPT_SUFFIX
            print self.get_versions()[i],name
            file = self.__zf.open(name)

            process = Popen('mysql -h%s -u%s -p%s %s ' % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(),database),
            stdout=PIPE, stdin=PIPE, shell=True)
            output = process.communicate(file.read())[0]
            print "-----"
            print process.returncode
            print "-----"


if __name__ == '__main__':
    arguments = Arguments()
    arguments.printInfo() 
    ud = updateDatabases(arguments)
    ud.update()
     
