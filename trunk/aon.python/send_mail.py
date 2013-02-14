#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''

from datetime import datetime
from aonAdmin.arguments import Arguments
from aonAdmin.domain import newDomain

if __name__ == '__main__':
    start = datetime.now();
    arguments = Arguments()
    newDomain = newDomain(arguments)
    domain = arguments.get_domain()  
    domain.validate( arguments.get_connection() )
    newDomain.send_mail(domain)

