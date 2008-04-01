package com.code.aon.ldap;

import java.util.HashMap;

public class Entry extends HashMap<String,Object> { 

    // Keys and Values will be in the hashtable, but
    // we will store the DN separately.
    private String dn; 

    public Entry() {
        super();
    }

    public Entry(Entry entry) { 
        super( entry );
        setDN(entry.getDN());   
    }

    public void setDN(String dn) { 
        this.dn = dn;
    }

    public String getDN() {
        return dn;
    }
}

