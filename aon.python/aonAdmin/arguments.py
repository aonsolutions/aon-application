'''
Created on 16/04/2012

@author: ecastellano
'''
from aonAdmin.domain import Domain, DomainTypes
from aonAdmin.connection import Connection
from aonAdmin.aonException import AonException
from optparse import OptionParser, OptionGroup, IndentedHelpFormatter

class Arguments(object):
    '''
    Class to manage database connection.
    '''


    def __init__(self):
        '''
        Constructor
        '''
        use = "Usage: command [options]"
        parser = OptionParser(usage = use,formatter = IndentedHelpFormatter(width=80,indent_increment=5,short_first=1))
        parser.add_option("-v", "--verbose", action="store_true", default=False, help="Set mode to verbose.")
        parser.add_option("-q", "--verbose-sql", action="store_true", default=False, help="Set mode to verbose on SQL Scripts.")
        parser.add_option("-x", "--no-prompt", action="store_true", default=False, help="Set prompt mode")
        parser.add_option("-k", "--skip-domain-creation", action="store_true", default=False, help="Skip Domain Creation in database creation")
        
        __databaseGroup = OptionGroup(parser,"Database Options")
        __databaseGroup.add_option("-o", "--host", help="Host to connect.")
        __databaseGroup.add_option("-u", "--user", help="User to connect as.")
        __databaseGroup.add_option("-p", "--passwd", help="Password to use.")
        __databaseGroup.add_option("-r", "--port", type="int", default="3306", help="TCP/IP port to connect to (%default by default).")
        __databaseGroup.add_option("-d", "--db", help="Database to use.")
        parser.add_option_group(__databaseGroup)
        
        __domainGroup = OptionGroup(parser,"Domain Options")
        __domainGroup.add_option("-n", "--domain-name",dest="domain_name", help="Name of the domain to be created.")
        __domainGroup.add_option("-e", "--domain-description",dest="domain_description", help="Description of the domain to be created.")
        __domainGroup.add_option("-t", "--domain-type",dest="domain_type", help="Type of the domain to be created. ('Parent'.'Child','Simple','PMS','GT')")
        __domainGroup.add_option("-s", "--domain-user",dest="domain_user", help="Parent domain admin user.")
        __domainGroup.add_option("-w", "--domain-password",dest="domain_password", help="Parent domain admin user's password.")
        
        __domainGroup.add_option("-a", "--domain-parent-id",dest="domain_parent_id", help="Parent domain ID of the domain to be created.")
        __domainGroup.add_option("-m", "--domain-parent-name",dest="domain_parent_name", help="Parent domain name of the domain to be created.")
        
        __domainGroup.add_option("", "--domain-max-defined-users",type="int", dest="domain_max_defined_users", default="1", help="Domain Max Defined Users (%default by default)")
        __domainGroup.add_option("", "--domain-modules",dest="domain_modules", help="Domain Modules")
        __domainGroup.add_option("-f", "--load-defaults-from-parent",action="store_true", default=False,dest="load_defaults_from_parent", help="true if defaults values are inserted from parent domain, false if SQL script are used.")
        parser.add_option_group(__domainGroup)        
    
        parser.add_option("-i", "--user-mail",dest="user_mail",help="eMail to give the response.")
        
        self.options, self.args = parser.parse_args()
        
        if self.options.verbose:
            self.printInfo()

    def printInfo(self):
        if self.options.verbose:
            print
            print "Mode is set to verbose."
            print
            print "DATABASE OPTIONS"
            print "----------------"
            print "\tHost .................: ", self.options.host
            if self.options.user != None:
                print "\tUser .................: ", self.options.user[0] + ("*" * (len(self.options.user) -2 )) +self.options.user[-1]
            if self.options.passwd != None: 
                print "\tPassword .............: ", "*" * len(self.options.passwd)
            print "\tPort .................: ", self.options.port
            print "\tDatabase .............: ", self.options.db
            print
            print "DOMAIN OPTIONS" 
            print "--------------" 
            print "\tDomain Name ...............: ", self.options.domain_name
            print "\tDomain Description ........: ", self.options.domain_description
            print "\tDomain Type ...............: ", self.options.domain_type
            print "\tDomain User ...............: ", self.options.domain_user
            print "\tDomain Password ...........: ", self.options.domain_password
            print "\tDomain Parent ID ..........: ", self.options.domain_parent_id
            print "\tDomain Parent Name ........: ", self.options.domain_parent_name
            print "\tDomain Max Defined Users ..:", self.options.domain_max_defined_users
            print
    
    def get_domain(self):
        domain = Domain()
        domain.set_domain_name(self.options.domain_name)
        description = self.options.domain_description
        if description == None or description == "":
            description = self.options.domain_name
        domain.set_domain_description(description)
        domain.set_domain_parent_id(self.options.domain_parent_id)
        domain.set_domain_parent_name(self.options.domain_parent_name)
        domain.set_domain_user(self.options.domain_user)
        domain.set_user_password(self.options.domain_password)
        domain.set_domain_type( DomainTypes().get_domain_type(self.options.domain_type) )
        domain.set_verbose(self.options.verbose)
        domain.set_load_defaults_from_parent(self.options.load_defaults_from_parent)
        domain.set_database_name(self.options.db)
        domain.set_domain_max_defined_users(self.options.domain_max_defined_users)
        domain.set_domain_modules(self.options.domain_modules)
        domain.set_domain_owner(self.options.user_mail)
        return domain

    def is_verbose_enabled(self):
        return self.options.verbose

    def is_verbose_sql_enabled(self):
        return self.options.verbose_sql

    def is_skip_domain_creation_enabled(self):
        return self.options.skip_domain_creation
    
    def get_connection(self,nodatabase=False):
        if self.options.host == None:
            raise AonException(-1,"Host is required!") 
        if self.options.user == None:
            raise AonException(-2,"User is required!") 
        if self.options.passwd == None:
            raise AonException(-3,"Password is required!")
        if not nodatabase:
            if self.options.db == None:
                raise AonException(-4,"Database is required!")
            
        conn = Connection()
        return conn.connect(self,nodatabase)
    
    def get_host(self):
        return self.options.host
    
    def get_port(self):
        return self.options.port

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
