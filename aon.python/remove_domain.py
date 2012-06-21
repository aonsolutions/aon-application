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
            print "\tValidation ..... ",aon.green("ok!")
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
                    print aon.green("Borrado de dominio cancelado.")
                    sys.exit(0)
            
            # Borrado de domain_application y sus dependencias
            cur = conn.cursor()
            cur.execute("SELECT id FROM domain_application WHERE domain = %s",(domain.get_domain_id(),))
            domain_applications = cur.fetchall()
            for domain_application in domain_applications:
                domain_application_id = domain_application[0]
                if self.__arguments.options.verbose_sql:
                    print "\tDomain_application_id --> ", domain_application_id
                
                # Borrado de "domain_application_module"
                delete_stmt = "delete from domain_application_module where domain_application = %s" % domain_application_id 
                if self.__arguments.options.verbose_sql:
                    print "\t\t" + delete_stmt,
                delete = conn.cursor()    
                ret = delete.execute(delete_stmt)
                delete.close
                if self.__arguments.options.verbose_sql:
                    print aon.green("["+str(ret) + " rows removed!]")    

                # Borrado de "application_user" y sus dependencias
                cur_au = conn.cursor()
                cur_au.execute("SELECT id FROM application_user WHERE domain_application = %s",(domain_application_id,))
                application_users = cur_au.fetchall()
                for application_user in application_users:
                    application_user_id = application_user[0]
                    if self.__arguments.options.verbose_sql:
                        print
                        print "\t\tapplication_user id -->",application_user_id
                    
                    # Borrado de "application_user_profile"    
                    delete_stmt = "delete from application_user_profile where application_user = %s" % application_user_id 
                    if self.__arguments.options.verbose_sql:
                        print "\t\t\t" + delete_stmt,
                    delete = conn.cursor()    
                    ret = delete.execute(delete_stmt)
                    delete.close
                    if self.__arguments.options.verbose_sql:
                        print aon.green("["+str(ret) + " rows removed!]")    
                        
                    # Borrado de "application_user"    
                    delete_stmt = "delete from application_user where id = %s" % application_user_id 
                    if self.__arguments.options.verbose_sql:
                        print "\t\t" + delete_stmt,
                    delete = conn.cursor()    
                    ret = delete.execute(delete_stmt)
                    delete.close
                    if self.__arguments.options.verbose_sql:
                        print aon.green("["+str(ret) + " rows removed!]")    
                cur_au.close
                
                # Borrado de "profile" y sus dependencias    
                cur_pr = conn.cursor()
                cur_pr.execute("SELECT id FROM profile WHERE domain = %s",(domain.get_domain_id(),))
                profiles = cur_pr.fetchall()
                for profile in profiles:
                    profile_id = profile[0]
                    if self.__arguments.options.verbose_sql:
                        print
                        print "\t\tprofile id -->",profile_id
                
                    # Borrado de "profile_module_denied"    
                    delete_stmt = "delete from profile_module_denied where profile = %s" % profile_id 
                    if self.__arguments.options.verbose_sql:
                        print "\t\t\t" + delete_stmt,
                    delete = conn.cursor()    
                    ret = delete.execute(delete_stmt)
                    delete.close
                    if self.__arguments.options.verbose_sql:
                        print aon.green("["+str(ret) + " rows removed!]")    
                        
                    # Borrado de "profile_role"    
                    delete_stmt = "delete from profile_role where profile = %s" % profile_id 
                    if self.__arguments.options.verbose_sql:
                        print "\t\t\t" + delete_stmt,
                    delete = conn.cursor()    
                    ret = delete.execute(delete_stmt)
                    delete.close
                    if self.__arguments.options.verbose_sql:
                        print aon.green("["+str(ret) + " rows removed!]")    

                    # Borrado de "profile"    
                    delete_stmt = "delete from profile where id = %s" % profile_id 
                    if self.__arguments.options.verbose_sql:
                        print "\t\t" + delete_stmt,
                    delete = conn.cursor()    
                    ret = delete.execute(delete_stmt)
                    delete.close
                    if self.__arguments.options.verbose_sql:
                        print aon.green("["+str(ret) + " rows removed!]")    
                cur_pr.close

                # Borrado de "domain_application"    
                delete_stmt = "delete from domain_application where id = %s" % domain_application_id 
                if self.__arguments.options.verbose_sql:
                    print "\t" + delete_stmt,
                delete = conn.cursor()    
                ret = delete.execute(delete_stmt)
                delete.close
                if self.__arguments.options.verbose_sql:
                    print aon.green("["+str(ret) + " rows removed!]")    
            cur.close

            stmt = conn.cursor()
            s = "SET FOREIGN_KEY_CHECKS=0;"
            if self.__arguments.options.verbose_sql:
                print s
            stmt.execute(s)
            stmt.close()
            self.__foreignKeysChanged = True
            # Fin del borrado de domain_application y sus dependencias

            
            # Se busca y borra todas las tablas que tengan un campo domain y cuyo valor coincida con el dominio a borrar.
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
                    print aon.green("\t" + str(ret) + " rows removed!")
            cur.close()
            
            delete_stmt = "DELETE FROM domain WHERE id = " + str(domain.get_domain_id())
            delete = conn.cursor()
            if self.__arguments.options.verbose_sql:
                print "\t" + delete_stmt,
            ret = delete.execute(delete_stmt)
            delete.close()
            if self.__arguments.options.verbose_sql:
                print aon.green(" [OK]")
            
            conn.commit()
            print
            print aon.green("Dominio borrado satisfactoriamente!")
            print
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
    list_domain  = RemoveDomain(arguments)
    retValue = list_domain.execute()
    sys.exit(retValue)

