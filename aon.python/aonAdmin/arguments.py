'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.domain import Domain, DomainTypes
from aonAdmin.connection import Connection
from optparse import OptionParser

class Arguments(object):
    '''
    Class to manage database connection.
    '''


    def __init__(self):
        '''
        Constructor
        '''
        use = "Usage: command [options]"
        parser = OptionParser(usage = use)
        parser.add_option("-v", "--verbose", action="store_true", default=False, help="Set mode to verbose.")
        parser.add_option("-o", "--host",  default="127.0.0.1", help="Host to connect.")
        parser.add_option("-u", "--user", default="dbuser", help="User to connect as.")
        parser.add_option("-p", "--passwd", default="serubd2000", help="Password to use.")
        parser.add_option("-r", "--port", default="3306", help="TCP/IP port to connect to.")
        parser.add_option("-d", "--db", default="", help="Database to use.")
        
        parser.add_option("-n", "--domain-name",dest="domain_name", help="Name of the domain to be created.")
        parser.add_option("-e", "--domain-description",dest="domain_description", help="Description of the domain to be created.")
        parser.add_option("-t", "--domain-type",dest="domain_type", help="Type of the domain to be created. ('Parent'.'Child','Simple','PMS','GT')")
        
        parser.add_option("-a", "--domain-parent-id",dest="domain_parent_id", help="Parent domain of the domain to be created.")
        
        parser.add_option("-s", "--domain-user",dest="domain_user", help="Parent domain admin user.")
        parser.add_option("-w", "--domain-password",dest="domain_password", help="Parent domain admin user's password.")
        parser.add_option("-f", "--load-defaults-from-parent",action="store_true", default=False,dest="load_defaults_from_parent", help="true if defaults values are inserted from parent domain, false if SQL script are used.")
    
        parser.add_option("-i", "--user-mail",dest="user_mail",help="eMail to give the response.")
        
        self.options, self.args = parser.parse_args()

    def printInfo(self):
        if self.options.verbose:
            print
            print "Mode is set to verbose."
            print
            print "   DOMAIN OPTIONS" 
            print "   --------------" 
            print "   Domain  Name .........: ", self.options.domain_name
            print "   Domain Description ...: ", self.options.domain_description
            print "   Domain Type ..........: ", self.options.domain_type
            print "   Domain parent ID .....: ", self.options.domain_parent_id
            print "   Domain user ..........: ", self.options.domain_user
            print
            print "   eMail ................: ", self.options.user_mail
            print
            
    
    def get_domain(self):
        domain = Domain()
        domain.set_domain_name(self.options.domain_name)
        description = self.options.domain_description
        if description == None or description == "":
            description = self.options.domain_name
            print "WARNING: No se ha indicado la descripcion del dominio. Se utilizara el nombre como descripcion"
        domain.set_domain_description(description)
        domain.set_domain_parent_id(self.options.domain_parent_id)
        domain.set_domain_user(self.options.domain_user)
        domain.set_user_password(self.options.domain_password)
        domain.set_domain_type( DomainTypes().get_domain_type(self.options.domain_type) )
        domain.set_verbose(self.options.verbose)
        domain.set_load_defaults_from_parent(self.options.load_defaults_from_parent)
        return domain

    def is_verbose_enabled(self):
        return self.options.verbose
    
    def get_connection(self):
        conn = Connection()
        return conn.connect(self)
    
    def get_host(self):
        return self.options.host
    
    def get_port(self):
        return int(self.options.port)

    def get_user(self):
        return self.options.user
        
    def get_passwd(self):
        return self.options.passwd
    
    def get_db(self):
        return self.options.db
    
    def get_user_mail(self):
        return self.options.user_mail

    def is_load_defaults_from_parent(self):
        return self.options.load_defaults_from_parent

    def get_domain_name(self):
        return self.options.domain_name
