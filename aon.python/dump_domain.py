#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''
from optparse import OptionParser
from aonAdmin.arguments import Arguments
from aonAdmin.aonException import AonException
from aonAdmin  import aon
from aonAdmin.domain import Domain, DomainTypes
import MySQLdb
import subprocess
import sys

class DumpDomain(object):
    
    def __init__(self,arguments):
        self.__arguments=arguments
        
    def __is_verbose_enabled(self):
        return self.__arguments.is_verbose_enabled()
    
    def __get_domain(self):
        return self.__arguments.get_domain()
    
    def __validate(self,conn):
        if self.__is_verbose_enabled():
            print
            print "Starting input validation"
            
        domain = self.__get_domain();

        # Valida la existencia de la variable domain_name
        if domain.get_domain_name() == None:
            raise AonException(-46,"Domain name is required!")
        
        # Valida la existencia del dominio
        cur = conn.cursor()
        cur.execute("SELECT id FROM domain WHERE name = %s",(domain.get_domain_name(),))
        if int(cur.rowcount) == False:
            raise AonException(-47,("Domain '%s' not found!" % domain.get_domain_name()))
        domain.set_domain_id(cur.fetchone()[0])
        cur.close()
        return domain
        
    def execute(self):
        retValue = 0
        conn = None;
        self.__foreignKeysChanged = False 
        try:
            conn = self.__arguments.get_connection()
            conn.set_character_set( "latin1" )
            domain = self.__validate(conn)
            
            # Se busca y borra todas las tablas que tengan un campo domain y cuyo valor coincida con el dominio a borrar.
            cur = conn.cursor()
            cur.execute("SELECT T.TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS as T WHERE T.TABLE_SCHEMA = %s AND T.COLUMN_NAME = %s",(self.__arguments.get_db(),"domain"))
            tables  = cur.fetchall()
            domain_tables = " "
            for table in tables:
                domain_tables = domain_tables + table[0] + " "
            cur.close()
            
            __command = ('mysqldump'+
                         ' --hex-blob'+                  # Dump binary columns using hexadecimal notation (for example, 'abc' becomes 0x616263). The affected data types are BINARY, VARBINARY, the BLOB types, and BIT. 
                         ' --compact'+                   # Produce more compact output. This option enables the --skip-add-drop-table, --skip-add-locks, --skip-comments, --skip-disable-keys, and --skip-set-charset options. 
                         ' --no-create-info'+            # Do not write CREATE TABLE statements that re-create each dumped table. 
                         ' --quick'+                     # This option is useful for dumping large tables. It forces mysqldump to retrieve rows for a table from the server a row at a time rather than retrieving the entire row set and buffering it in memory before writing it out.
                         ' --complete-insert'+           # Use complete INSERT statements that include column names
                         ' -h%s'+                        # Host to connect to (IP address or hostname) 
                         ' -u%s'+                        # The MySQL user name to use when connecting to the server
                         ' -p%s'+                        # The password to use when connecting to the server
                         ' %s'+                          # Database to dump
                         ' %s'+                          # Tables to dump
                         ' --where=domain=%s')           # Dump only rows selected by the given WHERE condition.
             
            ret = subprocess.call(__command % (self.__arguments.get_host(),
                                               self.__arguments.get_user(),
                                               self.__arguments.get_passwd(),
                                               self.__arguments.get_db(),
                                               domain_tables,
                                               domain.get_domain_id()),shell=True)
            conn.commit()
            retValue = 0
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            retValue = -20
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),e.errno,e.errmsg
            print "Exit!"
            retValue = e.errno
        finally:
            if self.__foreignKeysChanged:
                stmt = conn.cursor()
                s = "SET FOREIGN_KEY_CHECKS=1;"
                if self.__arguments.options.verbose_sql:
                    print s
                stmt.execute(s)
                stmt.close()
            if conn != None:
                conn.close()
        return retValue
        
if __name__ == '__main__':
    arguments = Arguments()
    dump_domain  = DumpDomain(arguments)
    retValue = dump_domain.execute()
    sys.exit(retValue)

