#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.aonException import AonException
from aonAdmin import aon
from optparse import OptionParser
from aonAdmin.arguments import Arguments
import MySQLdb
import sys

class ListDomains(object):
    
    def __init__(self,arguments):
        self.__arguments=arguments
        
    def report(self):
        conn = None;
        try: 
            
            system_domain_modules = ("MARKETING","COMERCIAL","GESTION","TESORERIA"
                               ,"ALMACEN","EXPEDIENTES","CONTABILIDAD","FISCAL","LABORAL","DOCUMENTAL")
            
            conn = self.__arguments.get_connection(nodatabase=True)
            
            cur = conn.cursor()
            stmt = "SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'";
            if self.__arguments.get_db() != None and self.__arguments.get_db() != "":
                stmt = stmt + " AND T.TABLE_SCHEMA = '" + self.__arguments.get_db() + "'"
            cur.execute(stmt)
            schemas = cur.fetchall()
            
            print "DATABASE|ID DOMINIO|DOMINIO PADRE|NOMBRE DOMINIO|ALMACENAMIENTO|USUARIOS|MULTIDOMINIO|EXTENSIONES|",
            for dm  in system_domain_modules:
                print dm + "|",
            print
            
            for schema in schemas:
                dom_cur = conn.cursor()
                stmt = "SELECT id,name,parent,maxTotalDocumentSize,maxDefinedUsers,domainManagement FROM `"+schema[0]+"`.`domain` ORDER BY `parent`,`name`";
                dom_cur.execute(stmt)
                domains = dom_cur.fetchall()
                for domain in domains:
                    domain_name = domain[1]
                    print schema[0] + "|"+ str(domain[0])+ "|" + domain[1] + "|" + str(domain[2])+ "|",
                    if domain[3] == None:
                        print "0" + "|",
                    else:
                        print str(domain[3]) + "|",
                    if domain[4] == None:
                        print "0" + "|",
                    else:
                        print str(domain[4]) + "|",
                    if domain[5] == None:
                        print "0" + "|",
                    else:
                        print str(domain[5]) + "|",
                        
                    mod = conn.cursor()
                    mod.execute("SELECT module FROM `"+schema[0]+"`.domain_application_module WHERE domain = %s",(str(domain[0]),))
                    modules = mod.fetchall()
                    domain_modules = ["NO","NO","NO","NO","NO","NO","NO","NO","NO","NO"]
                    for module in modules:
                        domain_modules[module[0]] = "SI"
                    mod.close
                    print str(len(modules)) + "|",
                    for dm  in domain_modules:
                        print dm + "|",
                    print      
                dom_cur.close()
            cur.close()
            conn.close()
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print aon.fail("ERROR:"),e.errno,e.errmsg
            print "Exit!"
            sys.exit(e.errno)

if __name__ == '__main__':
    arguments = Arguments()
    list_domain  = ListDomains(arguments)
    list_domain.report()

