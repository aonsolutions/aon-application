#!/usr/bin/env python

from aonAdmin.aonException import AonException
from aonAdmin.arguments import Arguments
from datetime import datetime
import subprocess
from subprocess import Popen, PIPE
import MySQLdb
import sys
import zipfile
from tempfile import NamedTemporaryFile
from datetime import datetime
from aonAdmin import aon
from aonAdmin.domain import newDomain

class createDatabase:
    
    AON_MASTER_JAR = "/usr/share/java/aon-master.jar" 
    CREATE_SCRIPT = "com/code/aon/master/create/create.database.sql"

    def __init__(self,arguments):
        self.__arguments=arguments
        self.__zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')

    def create(self):
        conn = None
        try:
            if self.__arguments.get_db() == "":
                raise AonException(-90,"No se ha indicado el nombre de la base de datos que se desea crear!")

            if self.__arguments.is_skip_domain_creation_enabled():
                print "Skip domain creation is set to true"
                __domain = self.__arguments.get_domain()
                if  __domain.get_domain_name() != None or __domain.get_domain_description() != None or __domain.get_domain_type() != None or __domain.get_domain_parent_id() != None or __domain.get_domain_parent_name() != None or __domain.get_domain_user() != None:
                    print
                    print aon.warning("WARNING: Se han indicado parametros referentes al dominio, pero no se va a crear un dominio (-k o --skip-domain-creation=true).")
                    if not self.__arguments.options.no_prompt:
                        req = None
                        while req != "y" and req != "n":
                            req = raw_input( "Continuar (y/n)?")
                        if req == "n":
                            print aon.green("Creacion de base de datos cancelada.")
                            sys.exit(0)
            
            if self.__arguments.is_verbose_enabled():
                print "Database creation"
            
            if self.__arguments.is_skip_domain_creation_enabled():
                if self.__arguments.options.domain_type == None or self.__arguments.options.domain_type=="":
                    self.__arguments.options.domain_type = "Parent"
            
            conn = self.__arguments.get_connection(nodatabase=True)
            cur = conn.cursor()
            cur.execute("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = %s",(self.__arguments.get_db(),))
            if int(cur.rowcount):
                raise AonException(-91,"Database '"+self.__arguments.get_db()+"' already exists!")
            if not self.__arguments.is_skip_domain_creation_enabled():
                domain = self.__arguments.get_domain()
                domain.validate( conn )
                if not domain.get_domain_type().is_parent():
                    raise AonException(-92,"No se puede crear un dominio hijo como primer dominio al crear la base de datos!")
            
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

            __sql_verbose = ""
            if self.__arguments.is_verbose_sql_enabled():
                __sql_verbose = " -v "
            __command = 'mysql '+ __sql_verbose + ' --default-character-set=latin1 -h%s -u%s -p%s -e "source %s;"'
            ret = subprocess.call(__command % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(),file.name),shell=True)
            if self.__arguments.is_verbose_enabled():
                print "\tMySQL returns",ret,"code" 
            
            self.__load_default_values()
            
            if self.__arguments.is_verbose_enabled():
                print aon.green("\tCreacion de la base de datos satisfactoria.")

            if self.__arguments.is_skip_domain_creation_enabled() == False:
                if self.__arguments.is_verbose_enabled():
                    print
                    print "Trying to create default domain"
                    print                
                nd = newDomain(self.__arguments)
                nd.create()
            else:
                if self.__arguments.is_verbose_enabled():
                    print
                    print "Skip domain creation"
                    print                
            
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),e.errno,e.errmsg
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
            raise AonException(-93,"Se ha producido un error de SQL!")            

if __name__ == '__main__':
    start = datetime.now();
    arguments = Arguments()
    ud = createDatabase(arguments)
    ud.create()
    if arguments.is_verbose_enabled():
        print "Script end [",str((datetime.now() - start)),"]"
     
