'''
Created on 16/04/2012

@author: ecastellano
'''

class ConsoleColors:
    HEADER = '\033[95m'
    BOLD = "\033[1m"
    BLUE = '\033[94m'
    GREEN = '\033[32m'
    RED = '\033[91m'
    ENDC = '\033[0m'

def warning(text):
    return ConsoleColors.BOLD + text + ConsoleColors.ENDC 
def fail(text):
    return ConsoleColors.RED + text + ConsoleColors.ENDC 
def bold(text):
    return ConsoleColors.BOLD + text + ConsoleColors.ENDC 
def header(text):
    return ConsoleColors.HEADER + text + ConsoleColors.ENDC 
def green(text):
    return ConsoleColors.GREEN + text + ConsoleColors.ENDC 
