'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.aonException import AonException
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from subprocess import Popen, PIPE
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

    def __init__(self):
        '''
        Constructor
        '''
        self.__verbose  = False
        self.__domain_name = None
        self.__domain_description = None
        self.__domain_parent_id = None
        self.__domain_type = None
        self.__user_id = None
        self.__domain_user = None
        self.__user_password = None
        self.__encripted_user_password = None
        self.__load_defaults_from_parent = False

    def get_domain_id(self):
        return self.__domain_id

    def get_domain_name(self):
        return self.__domain_name

    def get_domain_description(self):
        return self.__domain_description

    def get_domain_suffix(self):
        if self.get_domain_type().is_parent():
            pos =self.get_domain_name().find(".")
            if pos != -1:
                return self.get_domain_name()[pos+len("."):]
        return None
    
    def get_domain_type(self):
        return self.__domain_type

    def get_domain_parent_id(self):
        return self.__domain_parent_id

    def get_domain_user(self):
        return self.__domain_user

    def get_user_password(self):
        return self.__user_password

    def get_user_id(self):
        return self.__user_id

    def is_load_defaults_from_parent(self):
        return self.__load_defaults_from_parent

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

    def is_verbose_enabled(self):
        return self.__verbose
    def set_verbose(self, value):
        self.__verbose = value
    
    def insert(self,db):

        self.validate(db)
        self.__insert_domain(db)
        self.__insert_domain_application(db)
        if self.get_domain_type().is_parent():
            self.__insert_user(db)
            self.__insert_application_user(db)
            self.__insert_application_user_profile(db)

    def validate(self,db):
        '''
        Validates the name,description and parentId data.
        '''
        # validating Domain Type
        domain_type = self.get_domain_type()
        if domain_type == None:
            raise AonException(-31,"Domain Type is required! ("+DomainTypes().print_info()+")")
        
        if not domain_type.is_parent() and self.get_domain_parent_id() == None:
            raise AonException(-32,"If you want to create a child domain, you must supply a parent domain ID")
         
        if domain_type == DomainTypes.GT:
            raise AonException(-33," Not yet supported!")
        if domain_type == DomainTypes.PMS:
            raise AonException(-33," Not yet supported!")

        if self.get_domain_user() == None:
            raise AonException(-34," User domain is required!")
        
        # Validacion de la creacion de un dominio padre.
        if domain_type.is_parent():
            self.autenticate_user();        

        # Validacion de la creacion de un dominio hijo.
        if not domain_type.is_parent():
                    
            # validating Domain Parent
            if self.get_domain_parent_id() != None and not self.get_domain_parent_id().isdigit():
                raise AonException(-35,"Domain parent must be a positive integer!")
         
            # Validacion de la existencias del parent domain y en su caso, de la propiedad multidominio
            cur = db.cursor()
            cur.execute("SELECT domainManagement FROM domain WHERE id = %s",(self.get_domain_parent_id(),))
            if not int(cur.rowcount):
                raise AonException(-36,"The parent domain '"+self.get_domain_parent_id()+"' can not be found!")
            domain_management = cur.fetchone()[0]
            if not domain_management:
                raise AonException(-37,"Expected a multi-domain parent domain, but domain '"+self.get_domain_parent_id()+"' has this capability disabled!")
            
            # Validacion del usuario dentro del dominio parent    
            if self.get_domain_user() == None:
                raise AonException(-38,"If domain-parent-id parameter is provided, domain-user must be a valid admin user, now is empty!")
            cur = db.cursor()
            cur.execute("SELECT 1 FROM user WHERE login = %s and password = %s and domain = %s",(self.get_domain_user(),self.__encripted_user_password,self.get_domain_parent_id()))
            if int(cur.rowcount) == False:
                raise AonException(-39,"User '"+self.get_domain_user()+"' does not exists or can not be autenticated on parent domain '"+self.get_domain_parent_id()+"'")
 

        # validating Domain Name
        if self.get_domain_name() == None:
            raise AonException(-30,"Domain name is required!")
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
                    raise AonException(-31,"Domain name '"+self.get_domain_name()+"' already exists in database '"+schema[0]+"'!")
            cur.close()
            
            # Se valida que sea un nombre de host valido
            if self.is_valid_hostname(self.get_domain_name()) == False:
                raise AonException(-32,"Domain name '"+self.get_domain_name()+"' is not a valid host name, it must match '(?!-)[A-Z\d-]{1,63}(?<!-)$' regexp!")
        

        # validating Domain Description
        if self.get_domain_description() == None:
            raise AonException(-33,"Domain description is required!")
        
        
        if self.is_verbose_enabled():
            print "validation ..... ok!"

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
            print "    try to insert domain (",self.get_domain_name(),",",self.get_domain_description(),",",self.get_domain_parent_id(),self.get_domain_suffix(),")"  
        stmt.execute("INSERT INTO domain (name,description,parent,domainManagement,userManagement,subDomainSuffix) VALUES (%s,%s,%s,%s,%s,%s)"
                       ,(self.get_domain_name(),self.get_domain_description(),self.get_domain_parent_id(),self.get_domain_type().is_multidomain(),1,self.get_domain_suffix()))
        self.set_domain_id( db.insert_id() )
        
        if self.is_verbose_enabled():
            print "    domain inserted with id=",self.__domain_id
    
    def __insert_domain_application(self,db):
        cur = db.cursor()
        cur.execute("SELECT id FROM application WHERE name= 'aon-aio'")
        if not int(cur.rowcount):
            raise AonException(-25,"The application 'aon-aio' can not be found in application table")
        self.__application_id = cur.fetchone()[0]
        if self.is_verbose_enabled():
            print "    application 'aon-aio' found with id ",self.__application_id
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "    try to insert domain application (",self.get_domain_id(),self.__application_id,")",
        stmt.execute("INSERT INTO domain_application (domain,application) VALUES (%s,%s)"
                       ,(self.get_domain_id(),self.__application_id,))
        self.domain_application = db.insert_id()
        if self.is_verbose_enabled():
            print " ...... inserted with id ",self.domain_application
        
    def __insert_user(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "    try to insert user (",self.get_domain_user(),")"
        stmt.execute("INSERT INTO user (domain,name,login,password) VALUES (%s,%s,%s,%s)"
                       ,(self.get_domain_id(),self.get_domain_user(),self.get_domain_user(),self.__encripted_user_password))
        self.set_user_id( db.insert_id() )
        if self.is_verbose_enabled():
            print "    user  inserted with id=",self.get_user_id()

    def __insert_application_user(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "    try to insert application_user (",self.get_user_id(),self.domain_application,")"  
        stmt.execute("INSERT INTO application_user (user_id,domain_application) VALUES (%s,%s)"
                       ,(self.get_user_id(),self.domain_application))
        self.__application_user = db.insert_id()
        if self.is_verbose_enabled():
            print "    application_user inserted with id=",self.__application_user

    def __insert_application_user_profile(self,db):
        cur = db.cursor()
        cur.execute("SELECT id FROM profile WHERE name= 'Administrador' and application = %s",(self.__application_id,))
        if not int(cur.rowcount):
            raise AonException(-24,"The profile 'Administrador' for application 'aon-aio' can not be found in profile table")
        self.__admin_profile_id = cur.fetchone()[0]
        if self.is_verbose_enabled():
            print "    profile 'Administrador' found with id ",self.__admin_profile_id
            
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "    try to insert application_user_profile (",self.__application_user,self.__admin_profile_id,")"  
        stmt.execute("INSERT INTO application_user_profile (application_user,profile) VALUES (%s,%s)"
                       ,(self.__application_user,self.__admin_profile_id))
        self.__application_user_profile = db.insert_id()
        if self.is_verbose_enabled():
            print "    application_user_profile inserted with id=",self.__application_user_profile
            
    # *******************************************
    # ************* TODO ************************
    # *******************************************
    def autenticate_user(self):
        return True
    # *******************************************
    # *******************************************
    # *******************************************


class newDomain:
    
    #AON_MASTER_JAR = "/home/ecastellano/.m2/repository/com/code/aon/aon-master/7.0-SNAPSHOT/aon-master-7.0-SNAPSHOT.jar"
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
            self.__load_default_values(domain)

            if self.__arguments.is_verbose_enabled():
                print "commit ..... "
            conn.commit()
            self.send_mail()
            print "Dominio creado satisfactoriamente!"
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

    def __load_default_values(self,domain):
        if self.__arguments.is_verbose_enabled():
            print " loading default values for domain  ..... ",domain.get_domain_id()
        zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')
        
        if not domain.get_domain_type().is_parent() and self.__arguments.is_load_defaults_from_parent():
            sql_script = "com/code/aon/master/defaults/insert.database.aon.domain.from.parent.sql"
        else:
            if self.__arguments.is_load_defaults_from_parent():
                print "WARNING: Se indico la carga de valores desde el dominio padre, pero el dominio a crear es padre. Se ignora."
            sql_script = "com/code/aon/master/defaults/insert.database.aon.domain.sql"
        if self.__arguments.is_verbose_enabled():
            print sql_script
        temp_path = "/tmp/__aon.python.insert.database.aon.domain" + datetime.now().strftime("%Y%m%d-%H%M%S")
        if self.__arguments.is_verbose_enabled():
            print " SQL Script Temp Path  ..... ",domain.get_domain_id()
        zf.extract(sql_script, temp_path, None)
        sql_script = temp_path + "/" + sql_script
        if self.__arguments.is_verbose_enabled():
            print " Domain Defaults SQL Script Temp Path  ..... ",sql_script
        ret = subprocess.call('mysql -v --default-character-set=latin1 -h%s -u%s -p%s %s -e "SET @Domain=%s; source %s;"' % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(),self.__arguments.get_db(),domain.get_domain_id(),sql_script),shell=True)
        if self.__arguments.is_verbose_enabled():
            print " Defaults script returns code   ..... ",ret
