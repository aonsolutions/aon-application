'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.aonException import AonException
import re
import hashlib
import base64

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
        self.__domainName = None
        self.__domainDescription = None
        self.__domainParentId = None
        self.__domainType = None
        self.__userId = None
        self.__domainUser = None
        self.__userPassword = None
        self.__encriptedUserPassword = None

    def get_domain_id(self):
        return self.__domainId

    def get_domain_name(self):
        return self.__domainName

    def get_domain_description(self):
        return self.__domainDescription

    def get_domain_type(self):
        return self.__domainType

    def get_domain_parent_id(self):
        return self.__domainParentId

    def get_domain_user(self):
        return self.__domainUser

    def get_user_password(self):
        return self.__userPassword

    def get_user_id(self):
        return self.__userId


    def set_domain_id(self, value):
        self.__domainId = value

    def set_domain_name(self, value):
        self.__domainName = value

    def set_domain_description(self, value):
        self.__domainDescription = value

    def set_domain_type(self, value):
        self.__domainType = value

    def set_domain_parent_id(self, value):
        self.__domainParentId = value

    def set_domain_user(self, value):
        self.__domainUser = value

    def set_user_password(self, value):
        self.__userPassword = value
        # Codificacion de la clave, se pasa a SHA1 y luego a base64
        if self.get_user_password() == None:
            self.__encriptedUserPassword = None
        else:
            h =  hashlib.sha1(self.get_user_password())
            self.__encriptedUserPassword = base64.encodestring(h.digest()).strip()

    def set_user_id(self, value):
        self.__userId = value

    def is_verbose_enabled(self):
        return self.__verbose
    def set_verbose(self, value):
        self.__verbose = value
    
    def insert(self,db):

        self.__validate(db)
        self.__insert_domain(db)
        self.__insert_domain_application(db)
        if self.get_domain_type().is_parent():
            self.__insert_user(db)
            self.__insert_application_user(db)
            self.__insert_application_user_profile(db)

    def __validate(self,db):
        '''
        Validates the name,description and parentId data.
        '''
        # validating Domain Type
        domainType = self.get_domain_type()
        if domainType == None:
            raise AonException(-31,"Domain Type is required! ("+DomainTypes().print_info()+")")
        
        if not domainType.is_parent() and self.get_domain_parent_id() == None:
            raise AonException(-32,"If you want to create a child domain, you must supply a parent domain ID")
         
        if domainType == DomainTypes.GT:
            raise AonException(-33," Not yet supported!")
        if domainType == DomainTypes.PMS:
            raise AonException(-33," Not yet supported!")

        # Validacion de la creacion de un dominio padre.
        if domainType.is_parent():
            self.autenticate_user();        

        # Validacion de la creacion de un dominio hijo.
        if not domainType.is_parent():        
            # validating Domain Parent
            if self.get_domain_parent_id() != None and not self.get_domain_parent_id().isdigit():
                raise AonException(-34,"Domain parent must be a positive integer!")
         
            # Validacion de la existencias del parent domain y en su caso, de la propiedad multidominio
            cur = db.cursor()
            cur.execute("SELECT domainManagement FROM domain WHERE id = %s",(self.get_domain_parent_id(),))
            if not int(cur.rowcount):
                raise AonException(-26,"The parent domain '"+self.get_domain_parent_id()+"' can not be found!")
            domain_management = cur.fetchone()[0]
            if not domain_management:
                raise AonException(-27,"Expected a multi-domain parent domain, but domain '"+self.get_domain_parent_id()+"' has this capability disabled!")
            
            # Validacion del usuario dentro del dominio parent    
            if self.get_domain_user() == None:
                raise AonException(-28,"If domainParentId parameter is provided, domainUser must be a valid admin user, now is empty!")
            cur = db.cursor()
            cur.execute("SELECT 1 FROM user WHERE login = %s and password = %s and domain = %s",(self.get_domain_user(),self.__encriptedUserPassword,self.get_domain_parent_id()))
            if int(cur.rowcount) == False:
                raise AonException(-29,"User '"+self.get_domain_user()+"' does not exists or can not be autenticated on parent domain '"+self.get_domain_parent_id()+"'")
 

        # validating Domain Name
        if self.get_domain_name() == None:
            raise AonException(-30,"Domain name is required!")
        else:
            # Se valida que no exista el "name" en la base de datos
            cur = db.cursor()
            cur.execute("SELECT 1 FROM domain WHERE name = %s",(self.get_domain_name(),))
            if int(cur.rowcount):
                raise AonException(-31,"Domain name '"+self.get_domain_name()+"' already exists!")
            
            # Se valida que sea un nombre de host valido
            if self.__isValidHostname(self.get_domain_name()) == False:
                raise AonException(-32,"Domain name '"+self.get_domain_name()+"' is not a valid host name, it must match '(?!-)[A-Z\d-]{1,63}(?<!-)$' regexp!")
        

        # validating Domain Description
        if self.get_domain_description() == None:
            raise AonException(-33,"Domain description is required!")
        
        
        if self.is_verbose_enabled():
            print "validation ..... ok!"

    def __isValidHostname(self,hostname):
        if len(hostname) > 255:
            return False
        if hostname[-1:] == ".":
            hostname = hostname[:-1] # strip exactly one dot from the right, if present
        allowed = re.compile("(?!-)[A-Z\d-]{1,63}(?<!-)$", re.IGNORECASE)
        return all(allowed.match(x) for x in hostname.split("."))

    def __insert_domain(self,db):
        stmt = db.cursor()
        if self.is_verbose_enabled():
            print "    try to insert domain (",self.get_domain_name(),",",self.get_domain_description(),",",self.get_domain_parent_id(),")"  
        stmt.execute("INSERT INTO domain (name,description,parent,domainManagement,userManagement) VALUES (%s,%s,%s,%s,%s)"
                       ,(self.get_domain_name(),self.get_domain_description(),self.get_domain_parent_id(),self.get_domain_type().is_multidomain(),1))
        self.set_domain_id( db.insert_id() )
        
        if self.is_verbose_enabled():
            print "    domain inserted with id=",self.__domainId
    
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
                       ,(self.get_domain_id(),self.get_domain_user(),self.get_domain_user(),self.__encriptedUserPassword))
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
