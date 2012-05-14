'''
Created on 17/04/2012

@author: ecastellano
'''

class AonException(Exception):
    """General errors raised by command modules to user scripts.
    
    This exception class is used to report errors from Aon scripts utilities
    command modules and are used to communicate known errors to the user.
    """
    
    def __init__(self, errno=0,message=None):
        self.args = (message, errno)
        self.errmsg = message
        self.errno = errno
