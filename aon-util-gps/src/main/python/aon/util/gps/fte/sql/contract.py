'''
Created on May 22, 2014

@author: rtrepiana
'''
import re
import time

DNI_PATTERN = re.compile('[0-9]{8}[A-Z]', re.IGNORECASE)
NIE_PATTERN = re.compile('[XYZ][0-9]{7}[A-Z]', re.IGNORECASE)
CIF_PATTERN = re.compile('[ABCDEFGHJKLMNPQRSVW][0-9]{2}[0-9]{5}[A-Z0-9]', re.IGNORECASE)

def replace(hotel, document, fullname, performance={}, fte={}, pax={}, category=None, job=None, domain="@DOMAIN", data={} ):
    name = __split(fullname)
    return  u"""--
-- Employee '%(fullname)s' '%(document)s'
-- 
/*! SET @REGISTRY = IFNULL((SELECT `id` FROM `registry` WHERE `domain`=1 AND `document`='%(document)s'), IFNULL((SELECT `id` + 1 FROM `registry` ORDER BY `id` DESC LIMIT 1 FOR UPDATE),1))*/;
REPLACE INTO `registry` ( id, domain, document, document_type, name, type ) VALUES( @REGISTRY, %(domain)s ,'%(document)s', %(document_type)d , '%(fullname)s', %(type)d );
REPLACE INTO `person` ( registry, domain, name, first_surname, second_surname ) VALUES (@REGISTRY, %(domain)s , %(name)s, %(first_surname)s, %(second_surname)s );
/*! SET @WORKPLACE = IFNULL((SELECT id FROM `workplace` WHERE description RLIKE ".*%(hotel)s.*"),-1)*/;
/*! SET @CONTRACT = IFNULL((SELECT `id` FROM `contract` WHERE `person`=@REGISTRY AND `workplace`=@WORKPLACE), IFNULL((SELECT `id` + 1 FROM `contract` ORDER BY `id` DESC LIMIT 1 FOR UPDATE),1))*/;
REPLACE INTO `contract` ( id, domain, person, workplace, start_date, description, category_description ) VALUES (@CONTRACT, %(domain)s , @REGISTRY, @WORKPLACE, @START_DATE, %(job)s, %(category)s );
""" % { 
           'hotel': hotel,
           'domain': domain,
           'document':document,
           'fullname':unicode(fullname.upper()) ,
           'document_type':__document_type(document),
           'name': "'%s'" % name[0] if name[0] else "NULL",
           'first_surname':"'%s'" % name[1] if name[1] else "NULL" ,
           'second_surname':"'%s'" % name[2] if name[2] else "NULL" ,
           'type':__registry_type(document),
           'job' : "'%s'" % job if job else "NULL",
           'category' : "'%s'" % category if category else "NULL",
           } + __data (domain=domain , data=data ) + __contract_data (domain=domain, name=u"DESEMPE\xd1O", data=performance.iteritems() )+__contract_data (domain=domain, name="FTE",data=fte.iteritems() ) + __contract_data (domain=domain, name="PAX", data=pax.iteritems() )



'''
public enum DocumentType implements IResourceable {

    NIF,
    CIF,
    NIE,
    PASSPORT,
    WORK_PERMIT,
    COMMUNITY_CARD,
    OTHER;

'''

def __split(fullname):
    try :
        start = fullname.rindex(",")
    except ValueError : 
        return [None, None, None ]
    
    name = fullname[start + 1:].strip()
    surnames = fullname[0:start].strip()
    try :
        start = surnames.index(" ")
    except ValueError :
        return [name, surnames, None ]
    
    first_surname = surnames[0:start].strip()
    second_surname = surnames[start:].strip() 
    
    return [name, first_surname, second_surname]
    

def __document_type(document):
    if DNI_PATTERN.match(document):
        return 0;
    if NIE_PATTERN.match(document):
        return 2;
    if CIF_PATTERN.match(document):
        return 1;
    return 6;
'''
public enum RegistryType implements IResourceable {

        /** NATURAL. */
        NATURAL,

        /** LEGAL. */
        LEGAL;
'''
def __registry_type(document): 
    return 0;

def __data (domain, data):
    return reduce(lambda sql,item: (
"""%(sql)s/*! SET @CONTRACT_DATA = IFNULL((SELECT `id` FROM `contract_data` WHERE `contract`=@CONTRACT AND `name`='%(name)s' ), IFNULL((SELECT `id` + 1 FROM `contract_data` ORDER BY `id` DESC LIMIT 1 FOR UPDATE),1))*/;
REPLACE INTO `contract_data` (id, contract, domain, name, start_date, end_date, expression ) VALUES (@CONTRACT_DATA, @CONTRACT, %(domain)s, '%(name)s', @START_DATE, NULL, '%(value)s');
""" % { 'sql':sql, 
   'name':item[0],
   'domain':domain,
   'value': item[1]}) if item[1] else ( """%(sql)s""" % {'sql': sql} ) , sorted(data), "")

def __contract_data (domain, name, data):
    return reduce(lambda sql,item: (
"""%(sql)s/*! SET @CONTRACT_DATA = IFNULL((SELECT `id` FROM `contract_data` WHERE `contract`=@CONTRACT AND `name`='%(name)s' AND `start_date`='%(date)s'), IFNULL((SELECT `id` + 1 FROM `contract_data` ORDER BY `id` DESC LIMIT 1 FOR UPDATE),1))*/;
REPLACE INTO `contract_data` (id, contract, domain, name, start_date, end_date, expression ) VALUES (@CONTRACT_DATA, @CONTRACT, %(domain)s, '%(name)s', '%(date)s', '%(date)s', '%(value)s');
""" % { 'sql':sql, 
   'name':name,
   'domain':domain,
   'date': time.strftime("%Y-%m-%d", item[0] ),
   'value': item[1]}) if item[1] else ( """%(sql)s""" % {'sql': sql} ) , sorted(data), "")



