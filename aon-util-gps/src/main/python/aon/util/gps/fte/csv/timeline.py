'''
Created on May 21, 2014

@author: rtrepiana
'''


SEP = '|'


def parse(csvfile, callback):
    
    for line in csvfile :
        cols = line.split(SEP)
        if cols[0] == "DNI" :
            break;
    
    for line in csvfile :
        values = line.split(SEP)
        callback(dict(zip(cols, values)))
