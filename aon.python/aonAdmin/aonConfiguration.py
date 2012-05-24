'''
Created on 16/04/2012

@author: ecastellano
'''
import MySQLdb
import xml.dom.minidom

class AonConfiguration(object):
    '''
    Class to manage database connection.
    '''
    CONFIGURATION_FILE_PATH ="/etc/jbossas/default/aon.workspace/deployed.xml"
    NAME_ATTRIBUTE = "name"
    VALUE_ATTRIBUTE = "value"
    OPTION_TAG = "option"
    CONNECTION_URL_ATTRIBUTE = "hibernate.connection.url"
    CONNECTION_USERNAME_ATTRIBUTE = "hibernate.connection.username"
    CONNECTION_PASSWORD_ATTRIBUTE = "hibernate.connection.password"

    def __init__(self):
        '''
        Constructor
        '''
        self.__connectionUrl=None
        self.__connectionUsername=None
        self.__connectionPassword=None
        self.read_configuration()

    def get_connection_url(self):
        return self.__connectionUrl

    def get_connection_username(self):
        return self.__connectionUsername

    def get_connection_password(self):
        return self.__connectionPassword
    
    def set_connection_url(self, value):
        self.__connectionUrl = value

    def set_connection_username(self, value):
        self.__connectionUsername = value

    def set_connection_password(self, value):
        self.__connectionPassword = value

    

    def read_configuration(self):
        doc=xml.dom.minidom.parse(self.CONFIGURATION_FILE_PATH)
        options = doc.getElementsByTagName(self.OPTION_TAG)
        for option in options:
            if option.getAttribute(self.NAME) == self.CONNECTION_URL_ATTRIBUTE:
                self.set_connection_url( option.getAttribute(self.VALUE) )
            if option.getAttribute(self.NAME) == self.CONNECTION_USERNAME_ATTRIBUTE:
                self.set_connection_username( option.getAttribute(self.VALUE) )
            if option.getAttribute(self.NAME) == self.CONNECTION_PASSWORD_ATTRIBUTE:
                self.set_connection_password( option.getAttribute(self.VALUE) )

    def get_connection(self,arguments):
        '''
        Se conecta a la base de datos indicada en los parametros
        '''
        if arguments.is_verbose_enabled():
            print "    connecting to database",
        db = MySQLdb.connect(host=arguments.get_host(),port=arguments.get_port(),user=arguments.get_user(),passwd=arguments.get_passwd(),db=arguments.get_db())
        if arguments.is_verbose_enabled():
            print "] .....connected!"
        return db 

        