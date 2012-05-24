#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''

from aonAdmin.arguments import Arguments
from aonAdmin.domain import newDomain

if __name__ == '__main__':
    arguments = Arguments()
    arguments.printInfo() 
    newDomain = newDomain(arguments)
    newDomain.create()

