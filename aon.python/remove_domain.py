#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''
from optparse import OptionParser
from aonAdmin.arguments import Arguments
from aonAdmin.aonException import AonException
from aonAdmin.domain import Domain, DomainTypes, ConsoleColors
import MySQLdb
import sys

class RemoveDomain(object):
    
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
        
        # Valida que el dominio a borrar no tenga hijos
        cur = conn.cursor()
        cur.execute("SELECT name,description FROM domain WHERE parent = "+str(domain.get_domain_id()))
        children  = cur.fetchall()
        if len(children) > 0:
            print
            msg = "\tThe domain %s has dependencies with the following domains:" % domain.get_domain_name()
            print msg
            print "\t" + ("-" * len(msg)) 
            for child in children:
                print "\t %s --- %s" % ((child[0],child[1]))
            print
            raise AonException(-48,("The domain %s has dependencies with other domains:" % domain.get_domain_name()))
        cur.close()
        
        if self.__is_verbose_enabled():
            print "\tValidation ..... ",green("ok!")
        return domain
        
    def execute(self):
        retValue = 0
        conn = None;
        self.__foreignKeysChanged = False 
        try:
            conn = self.__arguments.get_connection()
            conn.set_character_set( "latin1" )
            domain = self.__validate(conn)
            
            if self.__arguments.options.no_prompt == False:
                req = None
                while req != "y" and req != "n":
                    print
                    req = raw_input( "\tDelete domain '%s' (id=%d) ?: (y/n) " % (domain.get_domain_name(),domain.get_domain_id()))

                if req == "n":
                    print
                    print green("Borrado de dominio cancelado.")
                    sys.exit(0)

            stmt = conn.cursor()
            s = "SET FOREIGN_KEY_CHECKS=0;"
            if self.__arguments.options.verbose_sql:
                print s
            stmt.execute(s)
            stmt.close()
            self.__foreignKeysChanged = True
    

            
            cur = conn.cursor()
            cur.execute("SELECT T.TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS as T WHERE T.TABLE_SCHEMA = %s AND T.COLUMN_NAME = %s",(self.__arguments.get_db(),"domain"))
            tables  = cur.fetchall()
            for table in tables:
                delete_stmt = "DELETE FROM " + table[0] + " WHERE domain = " + str(domain.get_domain_id())  
                if self.__arguments.options.verbose_sql:
                    print "\t" + delete_stmt
                delete = conn.cursor()
                ret = delete.execute(delete_stmt)
                delete.close()
                if self.__arguments.options.verbose_sql and ret > 0:
                    print green("\t" + str(ret) + " rows removed!")
            cur.close()
            
            delete_stmt = "DELETE FROM domain WHERE id = " + str(domain.get_domain_id())
            delete = conn.cursor()
            if self.__arguments.options.verbose_sql:
                print "\t" + delete_stmt,
            ret = delete.execute(delete_stmt)
            delete.close()
            if self.__arguments.options.verbose_sql:
                print green(" [OK]")
            
            conn.commit()
            print
            print green("Dominio borrado satisfactoriamente!")
            print
            retValue = 0
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            retValue = -20
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),e.errno,e.errmsg
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
#        cur = conn.cursor()
#        stmt = "SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain' AND T.TABLE_SCHEMA = '%s'",self.__arguments.get_db()
#        cur.execute(stmt)
#        schemas = cur.fetchall()
#        for schema in schemas:
#            print schema[0]
#            dom_cur = conn.cursor()
#            stmt = "SELECT `name` FROM `"+schema[0]+"`.`domain`";
#            dom_cur.execute(stmt)
#            domains = dom_cur.fetchall()
#            for domain in domains:
#                domain_name = domain[0]
#                print schema[0] + " " + domain[0] 
#            dom_cur.close()
#        cur.close()
        
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

if __name__ == '__main__':
    arguments = Arguments()
    list_domain  = RemoveDomain(arguments)
    retValue = list_domain.execute()
    sys.exit(retValue)

