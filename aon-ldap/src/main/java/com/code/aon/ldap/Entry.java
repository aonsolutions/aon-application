package com.code.aon.ldap;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Name;

public class Entry implements ILdapConstants, IAonObjectClasses { 

	private static final long serialVersionUID = -3592232487775900337L;
	
	private Name dn;
	
	private Name searchDN;
	
	private Map<String,List<Object>> values;

    public Entry( Name dn ) {
        this.dn = dn;
        this.values = new HashMap<String, List<Object>>();
    }

	public Name getDN() {
		return dn;
	}

	public void setDN(Name dn) {
		this.dn = dn;
	}

	public Name getSearchDN() {
		return searchDN;
	}

	public void setSearchDN(Name searchDN) {
		this.searchDN = searchDN;
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

	public Number getAsNumber( String key ) {
		return (Number) getAsObject(key);
	}
	
	public byte[] getAsByteArray( String key ) {
		return (byte[]) getAsObject(key);
	}

	public Boolean getAsBoolean( String key ) {
		return (Boolean) getAsObject(key);
	}

	public Date getAsDate( String key ) {
		return (Date) getAsObject(key);
	}
	
	public boolean hasObjectClass( String name ) {
		List<Object> objectClasses = get(OBJECT_CLASS_ATTRIBUTE);
		return ( objectClasses != null ) ? objectClasses.contains(name) : false;
	}
	
}

