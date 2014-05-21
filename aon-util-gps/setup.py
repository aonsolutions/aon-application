#!/usr/bin/env python

from distutils.core import setup
import os

#
# SETUP 
#
print
print( "Building aon.utils.gps (setup.py)" )
print( "-------------------" )
print
try:
	version = os.environ['AON_VERSION']
except KeyError, e:
	version = "UnknownVersion"
	
setup(
      name='aon.utils.gps',
      version=version,
      author='Raúl Trepiana',
      author_email='rtrepiana@esferalia.com',
      packages= ['aonAdmin',],
      scripts=['fte_csv2sql.py'],
      url='http://www.esferalia.com',
      license='LICENSE.txt',
      description='Useful aon gps stuff.',
      long_description=open('README.txt').read()
)
