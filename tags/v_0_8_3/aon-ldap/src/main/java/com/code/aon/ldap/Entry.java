package com.code.aon.ldap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Entry implements ILdapConstants { 

	private static final long serialVersionUID = -3592232487775900337L;
	
	private DistinguishedName dn;
	
	private Map<String,List<Object>> values;

    public Entry( String dn ) {
        this.dn = new DistinguishedName( dn );
        this.values = new HashMap<String, List<Object>>();
    }

	public DistinguishedName getDN() {
		return dn;
	}

	public void setDN(DistinguishedName dn) {
		this.dn = dn;
	}

	public Set<Map.Entry<String,List<Object>>> entrySet() {
		return this.values.entrySet();
	}
	
	public List<Object> get( String key ) {
		return this.values.get(key);
	}
	
	public boolean containsKey( String key) {
		return this.values.containsKey(key);
	}

	public void addObjectClass( String objectClass ) {
		put( OBJECT_CLASS_ATTRIBUTE, objectClass );
	}
	
	public void addObjectClasses( String[] objectClasses ) {
		for( String objectClass : objectClasses ) {
			addObjectClass( objectClass );
		}
	}
	
	public void put( String key, Object value ) {
		List<Object> list = get(key);
		if ( list == null ) {
			list = new LinkedList<Object>();
			this.values.put(key, list);
		}
		list.add( value );
	}
	
	public Object getAsObject( String key ) {
		return get(key).get(0);
	}
	
	public String getAsString( String key ) {
		return (String) getAsObject(key);
	}

	public Integer getAsInteger( String key ) {
		return (Integer) getAsObject(key);
	}

	public byte[] getAsByteArray( String key ) {
		return (byte[]) getAsObject(key);
	}

	public Boolean getAsBoolean( String key ) {
		return (Boolean) getAsObject(key);
	}
	
}

