#!/usr/bin/env python

from distutils.core import setup
import os

#
# SETUP 
#
print
print( "Building aon.python (setup.py)" )
print( "-------------------" )
print
try:
	version = os.environ['AON_VERSION']
except KeyError, e:
	version = "UnknownVersion"
	
setup(
      name='aon.python',
      version=version,
      author='Euke Castellano',
      author_email='ecastellano@esferalia.com',
      packages= ['aonAdmin',],
      scripts=['new_domain.py'
               ,'remove_domain.py'
               ,'update_databases.py'
               ,'create_database.py'
               ,'list_domains.py'
               ,'report_domains.py'
               ,'domain_merge.py'
               ,'domain_switcher.py'
               ,'dump_domain.py'],
      url='http://www.esferalia.com',
      license='LICENSE.txt',
      description='Useful aon admin stuff.',
      long_description=open('README.txt').read()
)
