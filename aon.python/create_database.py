#!/usr/bin/env python

from aonAdmin.aonException import AonException
from aonAdmin.arguments import Arguments
from subprocess import Popen, PIPE
import MySQLdb
import sys
import zipfile
import fileinput
from tempfile import TemporaryFile, NamedTemporaryFile, mkdtemp
from datetime import datetime
from aonAdmin.domain import DomainTypes
from aonAdmin.domain import newDomain
from aonAdmin.domain import ConsoleColors
from aonAdmin.connection import Connection


def warning(text):
    return ConsoleColors.BOLD + text + ConsoleColors.ENDC 
def fail(text):
    return ConsoleColors.RED + text + ConsoleColors.ENDC 
def bold(text):
    return ConsoleColors.BOLD + text + ConsoleColors.ENDC 
def header(text):
    return ConsoleColors.HEADER + text + ConsoleColors.ENDC 
def green(text):
    return ConsoleColors.GREEN + text + ConsoleColors.ENDC 

class createDatabase:
    
    AON_MASTER_JAR = "/usr/share/java/aon-master.jar" 
#    AON_MASTER_JAR = "/home/ecastellano/.m2/repository/com/code/aon/aon-master/7.0-SNAPSHOT/aon-master-7.0-SNAPSHOT.jar"
    CREATE_SCRIPT = "com/code/aon/master/create/create.database.sql"

    def __init__(self,arguments):
        self.__arguments=arguments
        self.__zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')

    def create(self):
        conn = None
        try:
            if self.__arguments.is_verbose_enabled():
                print "Database creation"

            if self.__arguments.get_db() == "":
                raise AonException(-90,"No se ha indicado el nombre de la base de datos que se desea crear!")

            if self.__arguments.options.domain_type == None or self.__arguments.options.domain_type=="":
                self.__arguments.options.domain_type = "Parent"
            
            connection = Connection()
            conn = connection.connect(self.__arguments, nodatabase=True )
            cur = conn.cursor()
            cur.execute("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = %s",(self.__arguments.get_db(),))
            if int(cur.rowcount):
                raise AonException(-31,"Database '"+self.__arguments.get_db()+"' already exists!")
        
            domain = self.__arguments.get_domain()
            if not domain.get_domain_type().is_parent():
                raise AonException(-91,"No se puede crear un dominio hijo como primer dominio al crear la base de datos!")
            
            domain.validate( conn )
            
            sql_script = self.__zf.open(self.CREATE_SCRIPT)
            file = NamedTemporaryFile(mode="r+")
            for lines in sql_script:
                lines = lines.replace("`aon_master`","`"+self.__arguments.get_db()+"`")
                file.flush()
                file.write(lines)
            file.seek(0)
            if self.__arguments.is_verbose_enabled():
                print
                print "Running creation tables SQL script ....."

            process = Popen('mysql -v -h%s -u%s -p%s ' % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd()),
            stdout=PIPE, stdin=PIPE, stderr=PIPE, shell=True)
            output = process.communicate(file.read())[0]
            if self.__arguments.is_verbose_enabled():
                print "\tMySQL returns",process.returncode,"code" 
            
            self.__load_default_values()
            
            if self.__arguments.is_verbose_enabled():
                print green("\tCreacion de la base de datos satisfactoria.")

            if self.__arguments.is_verbose_enabled():
                print
                print "Trying to create default domain"
                print                
            nd = newDomain(self.__arguments)
            nd.create()
            
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),e.errno,e.errmsg
            print "Exit!"
            sys.exit(e.errno)

    def __load_default_values(self):
        if self.__arguments.is_verbose_enabled():
            print
            print "Loading default values for new database  ..... "
        zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')
        sql_script = "com/code/aon/master/defaults/insert.database.aon.sql"
        temp_path = "/tmp/__aon.python.insert.database.aon.domain" + datetime.now().strftime("%Y%m%d-%H%M%S")
        zf.extract(sql_script, temp_path, None)
        sql_script = temp_path + "/" + sql_script
        file = NamedTemporaryFile(mode="r+")
        for lines in open(sql_script,"r"):
            lines = lines.replace("`aon_master`","`"+self.__arguments.get_db()+"`")
            file.flush()
            file.write(lines)
        file.flush()
        file.seek(0)
        
        
        process = Popen('mysql -v -h%s -u%s -p%s %s' % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(), self.__arguments.get_db()),
        stdout=PIPE, stdin=PIPE, shell=True)
        output = process.communicate(file.read())[0]
        if self.__arguments.is_verbose_enabled():
            print "\tDefaults script returns code   ..... ",process.returncode
        if process.returncode != 0:
            raise AonException(-90,"Se ha producido un error de SQL!")            

if __name__ == '__main__':
    arguments = Arguments()
    arguments.printInfo() 
    ud = createDatabase(arguments)
    ud.create()
     
