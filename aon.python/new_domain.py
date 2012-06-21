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
    newDomain.create()
    if arguments.is_verbose_enabled():
        print "Script end [",str((datetime.now() - start)),"]"

