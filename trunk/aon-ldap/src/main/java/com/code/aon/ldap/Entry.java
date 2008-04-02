package com.code.aon.ldap;

import java.util.HashMap;

public class Entry extends HashMap<String,Object> { 

	private static final long serialVersionUID = -3592232487775900337L;
	
	private DistinguishedName dn; 

    public Entry( String dn ) {
        super();
        this.dn = new DistinguishedName( dn );
    }

    public Entry(Entry entry) { 
        super( entry );
        setDN(entry.getDN());   
    }

	public DistinguishedName getDN() {
		return dn;
	}

	public void setDN(DistinguishedName dn) {
		this.dn = dn;
	}

}

