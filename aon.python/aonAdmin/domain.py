'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.aonException import AonException
from aonAdmin import aon
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import MySQLdb
import base64
import hashlib
import re
import smtplib
import subprocess
import sys
import zipfile

class DomainType:
    
    def __init__(self,name,parent,multidomain):
        self.__name = name
        self.__parent  = parent
        self.__multidomain  = multidomain
    
    def get_name(self):
        return self.__name

    def is_parent(self):
        return self.__parent

    def is_multidomain(self):
        return self.__multidomain

class DomainTypes:
    
    PARENT  = DomainType("Parent",True,True)
    CHILD   = DomainType("Child",False,False)
    SIMPLE  = DomainType("Simple",True,False)
    PMS     = DomainType("PMS",True,False)
    GT      = DomainType("GT",True,False)

    values = [PARENT,CHILD,SIMPLE,PMS,GT]
    
    def get_domain_type(self,key):
        for k in self.values:
            if (k.get_name()== key):
                return k
        return None
    
    def print_info(self):
        s = ""
        for k in self.values:
            if s != "":
                s += ","    
            s += "'"+k.get_name()+"'"
        return s

class Domain(object):
    '''
    Class to manage Domain object
    '''

    __system_domain_modules = ("marketing","commercial","management","treasury"
                             ,"warehouse","groupware","accounting","fiscal","payroll","document")
    
    def __init__(self):
        '''
        Constructor
        '''
        self.__verbose  = False
        self.__verbose_sql  = False
        self.__domain_name = None
        self.__domain_description = None
        self.__domain_parent_id = None
        self.__domain_parent_name = None
        self.__domain_type = None
        self.__user_id = None
        self.__domain_user = None
        self.__user_password = None
        self.__encripted_user_password = None
        self.__load_defaults_from_parent = False
        self.__database_name = None
        self.__domain_max_defined_users = None
        self.__domain_modules = None

    def get_domain_id(self):
        return self.__domain_id

    def get_domain_name(self):
        return self.__domain_name

    def get_domain_description(self):
        return self.__domain_description

    def get_domain_suffix(self):
        if self.get_domain_type().is_parent():
            return self.get_domain_name()
        return None
    
    def get_domain_type(self):
        return self.__domain_type

    def get_domain_parent_id(self):
        return self.__domain_parent_id

    def get_domain_parent_name(self):
        return self.__domain_parent_name

    def get_database_name(self):
        return self.__database_name

    def get_domain_user(self):
        return self.__domain_user

    def get_user_password(self):
        return self.__user_password

    def get_user_id(self):
        return self.__user_id

    def is_load_defaults_from_parent(self):
        return self.__load_defaults_from_parent
    
    def get_domain_max_defined_users(self):
        return self.__domain_max_defined_users
    
    def get_domain_modules(self):
        return self.__domain_modules

    def set_domain_id(self, value):
        self.__domain_id = value

    def set_domain_name(self, value):
        self.__domain_name = value

    def set_domain_description(self, value):
        self.__domain_description = value

    def set_domain_type(self, value):
        self.__domain_type = value

    def set_domain_parent_id(self, value):
        self.__domain_parent_id = value

    def set_domain_parent_name(self, value):
        self.__domain_parent_name = value

    def set_database_name(self, value):
        self.__database_name = value

    def set_domain_user(self, value):
        self.__domain_user = value

    def set_user_password(self, value):
        self.__user_password = value
        # Codificacion de la clave, se pasa a SHA1 y luego a base64
        if self.get_user_password() == None:
            self.__encripted_user_password = None
        else:
            h =  hashlib.sha1(self.get_user_password())
            self.__encripted_user_password = base64.encodestring(h.digest()).strip()

    def set_user_id(self, value):
        self.__user_id = value

    def set_load_defaults_from_parent(self, value):
        self.__load_defaults_from_parent = value

    def set_domain_max_defined_users(self, value):
        if (value == None):
            value = 1
        self.__domain_max_defined_users = value
    
    def set_domain_modules(self, value):
        self.__domain_modules = value

    def is_verbose_enabled(self):
        return self.__verbose
    def set_verbose(self, value):
        self.__verbose = value

    def is_verbose_sql_enabled(self):
        return self.__verbose_sql
    def set_verbose_sql(self, value):
        self.__verbose_sql = value
    
    def insert(self,db):
        self.validate(db)
        if self.is_verbose_enabled():
            print
            print "Inserting data"

        self.__insert_domain(db)
        if self.get_domain_type().is_parent():
            self.__insert_user(db)

        # TODO Estos datos se deberian poder pasar como parametros
        aon_aio_profiles = ("Administrador",) 
        applications = ("aon-aio",)
        profiles = (aon_aio_profiles,)
        # end TODO 
        
        x = 0
        for application in applications:
            if self.is_verbose_enabled():
                print "\tRegistering application '"+application+"'"
            self.__insert_domain_application(db,application)
            if self.get_domain_modules() != None and self.get_domain_modules() != "":
                self.__insert_domain_application_module(db) 
            if self.get_domain_type().is_parent():
                self.__insert_application_user(db)
                for profile in profiles[x]:
                    if self.is_verbose_enabled():
                        print "\t\tRegistering profile '"+profile+"'"
                    self.__insert_application_user_profile(db,profile)
                    
            x = x +1
                
        if self.is_verbose_enabled():
            print "\tInsertions ..... ",aon.green("ok!")

    def validate(self,db):
        '''
        Validates the name,description and parentId data.
        '''
        if self.is_verbose_enabled():
            print
            print "Starting input validation"

        # validating Domain Type
        domain_type = self.get_domain_type()
        if domain_type == None:
            raise AonException(-31,"Domain Type is required! ("+DomainTypes().print_info()+")")
        
        if not domain_type.is_parent() and self.get_domain_parent_id() == None and self.get_domain_parent_name() == None:
            raise AonException(-32,"If you want to create a child domain, you must supply a parent domain ID or parent domain Name")
        
        if not domain_type.is_parent() and self.get_domain_parent_name() != None:
            # Se valida que sea un nombre de host valido
            if self.is_valid_hostname(self.get_domain_parent_name()) == False:
                raise AonException(-33,"Parent Domain name '"+self.get_domain_name()+"' is not a valid host name, it must match '(?!-)[A-Z\d-]{1,63}(?<!-)$' regexp!")
         
        if domain_type == DomainTypes.GT:
            raise AonException(-34," Not yet supported!")
        if domain_type == DomainTypes.PMS:
            raise AonException(-35," Not yet supported!")

        if self.get_domain_user() == None:
            raise AonException(-36," User domain is required!")
        
        # Validacion de la creacion de un dominio padre.
        if domain_type.is_parent():
            self.autenticate_user();        

        # Validacion del numero maximo de usuarios
        if self.get_domain_max_defined_users() != None and not self.get_domain_max_defined_users() > 0:
            raise AonException(-37,"Domain max defined users must be a non zero positive integer!")
            
        # Validacion de los modulos
        modules = self.get_domain_modules().split(",")
        for mod in modules:
            found = False
            for system_module  in self.__system_domain_modules:
                if (system_module == mod):
                    found = True
                    break
            if not found:
                raise AonException(-38,"Module '"+mod+"' not found in system modules!")    
            
        # Validacion de la creacion de un dominio hijo.
        if not domain_type.is_parent():
                    
            # validating Domain Parent
            if self.get_domain_parent_id() != None and not self.get_domain_parent_id().isdigit():
                raise AonException(-37,"Domain parent must be a positive integer!")
         
            if self.get_domain_parent_name() != None:
                # Se valida que exista el "parent name" en la base de datos
                cur = db.cursor()
                cur.execute("SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'")
                schemas = cur.fetchall()
                parent_schema = None;
                for schema in schemas:
                    dom_cur = db.cursor()
                    stmt = "SELECT `id` FROM `"+schema[0]+"`.`domain` WHERE name = '" + self.get_domain_parent_name() + "'";
                    dom_cur.execute(stmt)
                    if int(dom_cur.rowcount):
                        parent_schema = schema[0]
                        parent_domain_id = dom_cur.fetchone()[0]
                cur.close()

                if parent_schema == None: 
                    raise AonException(-38,"Parent Domain name '"+self.get_domain_parent_name()+"' not found")
                
                # Se valida que exista el schema del "parent name" coincida con la base de datos indicada
                if parent_schema != None and parent_schema != self.get_database_name():
                    raise AonException(-39,"Parent Domain '"+self.get_domain_parent_name()+"' is not in Database '"+ self.get_database_name()+"'")
                
                # Se valida que se haya encontrado un ID del parent
                if parent_domain_id == None:
                    raise AonException(-40,"Parent Domain '"+self.get_domain_parent_name()+"' does not retrieve a valid ID")
                
                if self.get_domain_parent_id() == None:
                    if self.is_verbose_enabled():
                        print "\t\t Domain Parent ID assigned ",parent_domain_id
                    self.set_domain_parent_id(str(parent_domain_id))
                
                # Se valida que el parant ID suministrado coincida con el real
                if self.get_domain_parent_id() != str(parent_domain_id):
                    raise AonException(-41,"Parent Domain name '"+self.get_domain_parent_name()+"' does not match with Parent Domain ID '"+self.get_domain_parent_id()+"'")

            # Validacion de la existencias del parent domain y en su caso, de la propiedad multidominio
            cur = db.cursor()
            cur.execute("SELECT domainManagement FROM domain WHERE id = %s",(self.get_domain_parent_id(),))
            if not int(cur.rowcount):
                raise AonException(-42,"The parent domain '"+self.get_domain_parent_id()+"' can not be found!")
            domain_management = cur.fetchone()[0]
            if not domain_management:
                raise AonException(-43,"Expected a multi-domain parent domain, but domain '"+self.get_domain_parent_id()+"' has this capability disabled!")
            
            # Validacion del usuario dentro del dominio parent    
            if self.get_domain_user() == None:
                raise AonException(-44,"If domain-parent-id parameter is provided, domain-user must be a valid admin user, now is empty!")
            cur = db.cursor()
            cur.execute("SELECT 1 FROM user WHERE login = %s and password = %s and domain = %s",(self.get_domain_user(),self.__encripted_user_password,self.get_domain_parent_id()))
            if int(cur.rowcount) == False:
                raise AonException(-45,"User '"+self.get_domain_user()+"' does not exists or can not be autenticated on parent domain '"+self.get_domain_parent_id()+"'")
            

        # validating Domain Name
        if self.get_domain_name() == None:
            raise AonException(-46,"Domain name is required!")
        else:
            # Se valida que no exista el "name" en la base de datos
            cur = db.cursor()
            cur.execute("SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'")
            schemas = cur.fetchall()
            for schema in schemas:
                dom_cur = db.cursor()
                stmt = "SELECT 1 FROM `"+schema[0]+"`.`domain` WHERE name = '" + self.get_domain_name() + "'";
                dom_cur.execute(stmt)
                if int(dom_cur.rowcount):
                    raise AonException(-47,"Domain name '"+self.get_domain_name()+"' already exists in database '"+schema[0]+"'!")
            cur.close()
            
            # Se valida que sea un nombre de host valido
            if self.is_valid_hostname(self.get_domain_name()) == False:
                raise AonException(-48,"Domain name '"+self.get_domain_name()+"' is not a valid host name, it must match '(?!-)[A-Z\d-]{1,63}(?<!-)$' regexp!")
        

        # validating Domain Description
        if self.get_domain_description() == None:
            raise AonException(-49,"Domain description is required!")
        
        
        if self.is_verbose_enabled():
            print "\tValidation ..... ",aon.green("ok!")

    def is_valid_hostname(self,hostname):
        if len(hostname) > 255:
            return False
        if hostname[-1:] == ".":
            hostname = hostname[:-1] # strip exactly one dot from the right, if present
        allowed = re.compile("(?!-)[A-Z\d-]{1,63}(?<!-)$", re.IGNORECASE)
        return all(allowed.match(x) for x in hostname.split("."))

    def __insert_domain(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "\tTrying to insert domain (",self.get_domain_name(),",",self.get_domain_description(),",",self.get_domain_parent_id(),",",self.get_domain_suffix(),")",  
        stmt.execute("INSERT INTO domain (name,description,parent,domainManagement,userManagement,subDomainSuffix,maxDocumentSize,maxTotalDocumentSize,maxDefinedUsers) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)"
                       ,(self.get_domain_name()
                         ,self.get_domain_description()
                         ,self.get_domain_parent_id()
                         ,self.get_domain_type().is_multidomain()
                         ,1
                         ,self.get_domain_suffix()
                         ,0
                         ,0
                         ,self.get_domain_max_defined_users()))
        self.set_domain_id( db.insert_id() )
        
        if self.is_verbose_enabled():
            print "....... inserted with id=",self.__domain_id
    
    def __insert_domain_application(self,db,application):
        cur = db.cursor()
        cur.execute("SELECT id FROM application WHERE name= %s",(application,))
        if not int(cur.rowcount):
            raise AonException(-50,"The application '"+ application + "' can not be found in application table")
        self.__application_id = cur.fetchone()[0]
        if self.is_verbose_enabled():
            print "\t\tApplication '"+application+"' found with id ",self.__application_id
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "\t\tTrying to insert domain application (",self.get_domain_id(),self.__application_id,")",
        stmt.execute("INSERT INTO domain_application (domain,application) VALUES (%s,%s)"
                       ,(self.get_domain_id(),self.__application_id,))
        self.domain_application = db.insert_id()
        if self.is_verbose_enabled():
            print " ...... inserted with id ",self.domain_application
            
    def __insert_domain_application_module(self,db):
        stmt = db.cursor()
        modules = self.get_domain_modules().split(",")
        for mod in modules:
            x = 0
            for system_module  in self.__system_domain_modules:
                if (system_module == mod):
                    if self.is_verbose_enabled():
                        print "\t\tTrying to domain_application_module (",mod + "("+str(x)+")",")",
                    stmt.execute("INSERT INTO domain_application_module (domain_application,module) VALUES (%s,%s)"
                                   ,(self.domain_application,x))
                    if self.is_verbose_enabled():
                        print "........... inserted with id=",db.insert_id()
                    break
                x = x + 1

    def __insert_user(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "\tTrying to insert user (",self.get_domain_user(),")",
        stmt.execute("INSERT INTO user (domain,name,login,password) VALUES (%s,%s,%s,%s)"
                       ,(self.get_domain_id(),self.get_domain_user(),self.get_domain_user(),self.__encripted_user_password))
        self.set_user_id( db.insert_id() )
        if self.is_verbose_enabled():
            print "........... inserted with id=",self.get_user_id()

    def __insert_application_user(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "\t\tTrying to insert application_user (",self.get_user_id(),self.domain_application,")",  
        stmt.execute("INSERT INTO application_user (user_id,domain_application) VALUES (%s,%s)"
                       ,(self.get_user_id(),self.domain_application))
        self.__application_user = db.insert_id()
        if self.is_verbose_enabled():
            print "......... inserted with id=",self.__application_user

    def __insert_application_user_profile(self,db, profile):
        cur = db.cursor()
        cur.execute("SELECT id FROM profile WHERE name= %s and application = %s",(profile,self.__application_id,))
        if not int(cur.rowcount):
            raise AonException(-51,"The profile '"+profile+"' for application '"+self.__application_id+"' can not be found in profile table")
        self.__admin_profile_id = cur.fetchone()[0]
        if self.is_verbose_enabled():
            print "\t\t\tProfile '"+profile+"' found with id ",self.__admin_profile_id
            
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "\t\t\tTrying to insert application_user_profile (",self.__application_user,self.__admin_profile_id,")",  
        stmt.execute("INSERT INTO application_user_profile (application_user,profile) VALUES (%s,%s)"
                       ,(self.__application_user,self.__admin_profile_id))
        self.__application_user_profile = db.insert_id()
        if self.is_verbose_enabled():
            print " ...... inserted with id=",self.__application_user_profile
            
    # *******************************************
    # ************* TODO ************************
    # *******************************************
    def autenticate_user(self):
        return True
    # *******************************************
    # *******************************************
    # *******************************************


class newDomain:
    
    AON_MASTER_JAR = "/usr/share/java/aon-master.jar"

    def __init__(self,arguments):
        self.__arguments=arguments
    

    def create(self):
        conn = None
        try: 
            conn  = self.__arguments.get_connection()
            domain = self.__arguments.get_domain()
            domain.insert(conn)
            conn.commit()
            self.__load_domain_default_values(domain)

            if self.__arguments.is_verbose_enabled():
                print "Commiting Transaction ..... ",
            conn.commit()
            if self.__arguments.is_verbose_enabled():
                print aon.green("ok!")
            self.send_mail()
            print
            print aon.green("Dominio creado satisfactoriamente!")
            print
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

    def send_mail(self):
        if self.__arguments.get_user_mail()!=None and self.__arguments.get_user_mail()!="":
            text = "Dominio creado satisfactoriamente"
            to = self.__arguments.get_user_mail() 
            me = "ecastellano@esferalia.com"
    
            msg = MIMEMultipart()
            msg['Subject'] = text
            msg['To'] = to
            msg['From'] = me
            part = MIMEText('text', "plain")
            part.set_payload(text)
            msg.attach(part)        
            s = smtplib.SMTP("pod51016.outlook.com",587)
            if self.__arguments.is_verbose_enabled():
                s.set_debuglevel(1)
            else:
                s.set_debuglevel(0)
            s.ehlo()
            s.starttls()
            s.ehlo()            
            s.login(me,"<password>")
            s.sendmail(me, to , msg.as_string())
            s.quit()
    
    def __load_domain_default_values(self,domain):
        if self.__arguments.is_verbose_enabled():
            print
            print "Loading default values for domain  ..... ",domain.get_domain_id()
        zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')
        
        if not domain.get_domain_type().is_parent() and self.__arguments.is_load_defaults_from_parent():
            sql_script = "com/code/aon/master/defaults/insert.database.aon.domain.from.parent.sql"
        else:
            if self.__arguments.is_load_defaults_from_parent():
                print  "\t"+aon.warning("WARNING:"),"Se indico la carga de valores desde el dominio padre, pero el dominio a crear es padre. Se ignora."
            sql_script = "com/code/aon/master/defaults/insert.database.aon.domain.sql"
            
        temp_path = "/tmp/__aon.python.insert.database.aon.domain" + datetime.now().strftime("%Y%m%d-%H%M%S")
        zf.extract(sql_script, temp_path, None)
        sql_script = temp_path + "/" + sql_script
        __sql_verbose = ""
        if self.__arguments.is_verbose_sql_enabled():
            __sql_verbose = " -v "
        __command = 'mysql '+ __sql_verbose + ' --default-character-set=latin1 -h%s -u%s -p%s %s -e "SET @Domain=%s; source %s;"'
        
        ret = subprocess.call(__command % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(),self.__arguments.get_db(),domain.get_domain_id(),sql_script),shell=True)
        if self.__arguments.is_verbose_enabled():
            print "\tDefaults script returns code   ..... ",ret
            print "\tDefault data load  ..... ",aon.green("ok!")
            print
            
