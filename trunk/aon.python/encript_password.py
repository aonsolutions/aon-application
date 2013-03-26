#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''

import base64
import hashlib

if __name__ == '__main__':
    req = raw_input( "Password a encriptar:")
    h =  hashlib.sha1(req)
    print base64.encodestring(h.digest()).strip()

