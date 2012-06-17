#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.aonException import AonException
from aonAdmin.domain import ConsoleColors
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
            conn = self.__arguments.get_connection(nodatabase=True)
            cur = conn.cursor()
            stmt = "SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'";
            if self.__arguments.get_db() != None and self.__arguments.get_db() != "":
                stmt = stmt + " AND T.TABLE_SCHEMA = '" + self.__arguments.get_db() + "'"
            cur.execute(stmt)
            schemas = cur.fetchall()
            for schema in schemas:
                dom_cur = conn.cursor()
                stmt = "SELECT `name` FROM `"+schema[0]+"`.`domain`";
                dom_cur.execute(stmt)
                domains = dom_cur.fetchall()
                for domain in domains:
                    domain_name = domain[0]
                    print schema[0] + " " + domain[0] 
                dom_cur.close()
            cur.close()
            conn.close()
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),"-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print "Rollback ..... "
            if conn != None:
                conn.rollback();
            print fail("ERROR:"),e.errno,e.errmsg
            print "Exit!"
            sys.exit(e.errno)

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
    list_domain  = ListDomains(arguments)
    list_domain.report()

