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

	public String getAsString( String key ) {
		return (String) get(key);
	}

	public Integer getAsInteger( String key ) {
		return (Integer) get(key);
	}

	public byte[] getAsByteArray( String key ) {
		return (byte[]) get(key);
	}

	public Boolean getAsBoolean( String key ) {
		return (Boolean) get(key);
	}
	
}

