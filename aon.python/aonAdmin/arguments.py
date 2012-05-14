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
        use = "Usage: newDomain [options]"
        parser = OptionParser(usage = use)
        parser.add_option("-v", "--verbose", action="store_true", default=False, help="Set mode to verbose.")
        parser.add_option("-o", "--host",  default="127.0.0.1", help="Host to connect.")
        parser.add_option("-u", "--user", default="dbuser", help="User to connect as.")
        parser.add_option("-p", "--passwd", default="serubd2000", help="Password to use.")
        parser.add_option("-r", "--port", default="3306", help="TCP/IP port to connect to.")
        parser.add_option("-d", "--db", default="", help="Database to use.")
        
        parser.add_option("-n", "--domainName", help="Name of the domain to be created.")
        parser.add_option("-e", "--domainDescription", help="Description of the domain to be created.")
        parser.add_option("-t", "--domainType", help="Type of the domain to be created. ('Parent'.'Child','Simple','PMS','GT')")
        
        parser.add_option("-a", "--domainParentId", help="Parent domain of the domain to be created.")
        
        parser.add_option("-s", "--domainUser", help="Parent domain admin user.")
        parser.add_option("-w", "--domainPassword", help="Parent domain admin user's password.")
    
        parser.add_option("-i", "--userMail",help="eMail to give the response.")
        
        self.options, self.args = parser.parse_args()

    def printInfo(self):
        if self.options.verbose:
            print
            print "Mode is set to verbose."
            print
            print "   DOMAIN OPTIONS" 
            print "   --------------" 
            print "   Domain  Name .........: ", self.options.domainName
            print "   Domain Description ...: ", self.options.domainDescription
            print "   Domain Type ..........: ", self.options.domainType
            print "   Domain parent ID .....: ", self.options.domainParentId
            print "   Domain user ..........: ", self.options.domainUser
            print
            print "   eMail ................: ", self.options.userMail
            print
            
    
    def get_domain(self):
        domain = Domain()
        domain.set_domain_name(self.options.domainName)
        domain.set_domain_description(self.options.domainDescription)
        domain.set_domain_parent_id(self.options.domainParentId)
        domain.set_domain_user(self.options.domainUser)
        domain.set_user_password(self.options.domainPassword)
        domain.set_domain_type( DomainTypes().get_domain_type(self.options.domainType) )
        domain.set_verbose(self.options.verbose)
        return domain

    def is_verbose_enabled(self):
        return self.options.verbose
    
    def get_connection(self):
        conn = Connection()
        return conn.connect(self)
    
    def print_connection_info(self):
        return self.options.user+"@"+self.options.host+":"+self.options.port+"/"+self.options.db
    
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
        return self.options.userMail
