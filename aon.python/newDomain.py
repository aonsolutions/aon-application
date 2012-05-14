#!/usr/bin/env python
'''
Created on 16/04/2012

@author: ecastellano
'''

from aonAdmin.aonException import AonException
from aonAdmin.arguments import Arguments
from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from subprocess import Popen, PIPE
import MySQLdb
import smtplib
import sys
import zipfile
import subprocess


class newDomain:
    
    #AON_MASTER_JAR = "/home/ecastellano/.m2/repository/com/code/aon/aon-master/7.0-SNAPSHOT/aon-master-7.0-SNAPSHOT.jar"
    AON_MASTER_JAR = "/home/ecastellano/AON-7/7.x/aon-master/target/aon-master-7.0-SNAPSHOT.jar"

    def __init__(self,arguments):
        self.__arguments=arguments
    

    def create(self):
        conn = None
        try: 
            conn  = self.__arguments.get_connection()
            domain = self.__arguments.get_domain()
            domain.insert(conn)
            self.__load_default_values(domain)
            if self.__arguments.is_verbose_enabled():
                print "commit ..... "
            conn.commit()
            self.send_mail()
            print "Dominio creado satisfactoriamente!"
        except MySQLdb.DatabaseError, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print "-20 - Se ha producido un error SQL", e
            print "Exit!"
            sys.exit(-20)
        except AonException, e:
            if self.__arguments.is_verbose_enabled():
                print " rollback ..... "
            if conn != None:
                conn.rollback();
            print e.errno,e.errmsg
            print "Exit!"
            sys.exit(e.errno)

    def send_mail(self):
        if self.__arguments.get_user_mail()!=None and self.__arguments.get_user_mail()!="":
            text = "Dominio creado satisfactoriamente"
            to = self.__arguments.get_user_mail() 
            me = "ecastellano@esferalia.com"
    
            msg = MIMEMultipart()
            msg['Subject'] = text
            msg['To'] = to
            msg['From'] = me
            part = MIMEText('text', "plain")
            part.set_payload(text)
            msg.attach(part)        
            s = smtplib.SMTP("pod51016.outlook.com",587)
            if self.__arguments.is_verbose_enabled():
                s.set_debuglevel(1)
            else:
                s.set_debuglevel(0)
            s.ehlo()
            s.starttls()
            s.ehlo()            
    #        s.login(me,"costarica2000")
            s.login(me,"GGhh%123")
            s.sendmail(me, to , msg.as_string())
            s.quit()

    def __load_default_values(self,domain):
        if self.__arguments.is_verbose_enabled():
            print " loading default values for domain  ..... ",domain.get_domain_id()
        zf = zipfile.ZipFile(self.AON_MASTER_JAR, 'r')
        sql_script = "com/code/aon/master/defaults/insert.database.aon.domain.sql"
        temp_path = "/tmp/__aon.python.insert.database.aon.domain" + datetime.now().strftime("%Y%m%d-%H%M%S")
        if self.__arguments.is_verbose_enabled():
            print " SQL Script Temp Path  ..... ",domain.get_domain_id()
        zf.extract(sql_script, temp_path, None)
        sql_script = temp_path + "/" + sql_script
        if self.__arguments.is_verbose_enabled():
            print " Domain Defaults SQL Script Temp Path  ..... ",sql_script
        ret = subprocess.call('mysql --default-character-set=latin1 -h%s -u%s -p%s %s -e "SET @Domain=%s; source %s;"' % (self.__arguments.get_host(), self.__arguments.get_user(), self.__arguments.get_passwd(),self.__arguments.get_db(),domain.get_domain_id(),sql_script),shell=True)
        if self.__arguments.is_verbose_enabled():
            print " Defaults script returns code   ..... ",ret

if __name__ == '__main__':
    arguments = Arguments()
    arguments.printInfo() 
    newDomain = newDomain(arguments)
    newDomain.create()

