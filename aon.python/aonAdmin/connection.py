'''
Created on 16/04/2012

@author: ecastellano
'''
import MySQLdb

class Connection(object):
    '''
    Class to manage database connection.
    '''


    def __init__(self):
        '''
        Constructor
        '''

    def connect(self,arguments,nodatabase=False):
        '''
        Se conecta a la base de datos indicada en los parametros
        '''
        if arguments.is_verbose_enabled():
            print "    connecting to database",
        if nodatabase:
            db = MySQLdb.connect(host=arguments.get_host(),port=arguments.get_port(),user=arguments.get_user(),passwd=arguments.get_passwd())
        else:
            db = MySQLdb.connect(host=arguments.get_host(),port=arguments.get_port(),user=arguments.get_user(),passwd=arguments.get_passwd(),db=arguments.get_db(),charset="latin1")
        if arguments.is_verbose_enabled():
            print "] .....connected!"
        return db 
        
        