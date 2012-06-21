#!/usr/bin/env python

from distutils.core import setup
import xml.dom.minidom
import os

def get_pom_version():
    print "Reading pom ..."
    doc=xml.dom.minidom.parse("pom.xml")
    versionNode = doc.getElementsByTagName("version")[0]
    version = versionNode.firstChild.nodeValue
    return version


#
# SETUP 
#
print
print( "Building aon.python (setup.py)" )
print( "-------------------" )
print
setup(
      name='aon.python',
      version="7.0-SNAPSHOT",
      author='Euke Castellano',
      author_email='ecastellano@esferalia.com',
      packages= ['aonAdmin',],
      scripts=['new_domain.py'
               ,'remove_domain.py'
               ,'update_databases.py'
               ,'create_database.py'
               ,'list_domains.py'
               ,'domain_merge.py'
               ,'dump_domain.py'],
      url='http://www.esferalia.com',
      license='LICENSE.txt',
      description='Useful aon admin stuff.',
      long_description=open('README.txt').read()
)
