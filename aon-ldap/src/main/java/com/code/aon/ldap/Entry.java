package com.code.aon.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Entry implements ILdapConstants, IAonObjectClasses { 

	private final static Logger LOGGER = LoggerFactory.getLogger(Entry.class);
	
	public static final String TRUE_VALUE = "TRUE";
	
	public static final String FALSE_VALUE = "FALSE";

	private static final SimpleDateFormat GENERALIZED_TIME_FORMAT = new SimpleDateFormat( "yyyyMMddHHmmss'Z'" );
	
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> getObjectClasses() {
		return (List) get(OBJECT_CLASS_ATTRIBUTE);
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

	public Integer toInteger( String key ) {
		Object value = getAsObject(key);
		return NumberUtils.createInteger(value.toString());
	}
	
	public Integer getAsInteger( String key ) {
		return (Integer) getAsObject(key);
	}
	
	public Number toNumber( String key ) {
		Object value = getAsObject(key);
		return NumberUtils.createNumber(value.toString());
	}

	public Number getAsNumber( String key ) {
		return (Number) getAsObject(key);
	}
	
	public byte[] getAsByteArray( String key ) {
		return (byte[]) getAsObject(key);
	}

	public Boolean toBoolean( String key ) {
		Object value = getAsObject(key);
		return TRUE_VALUE.equals(value) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public Boolean getAsBoolean( String key ) {
		return (Boolean) getAsObject(key);
	}

	public Date toDate( String key ) {
		Object value = getAsObject(key);
		return convertToDate(value.toString());
	}
	
	public Date getAsDate( String key ) {
		return (Date) getAsObject(key);
	}
	
	public boolean hasObjectClass( String name ) {
		List<Object> objectClasses = get(OBJECT_CLASS_ATTRIBUTE);
		return ( objectClasses != null ) ? objectClasses.contains(name) : false;
	}
	
	public static Date convertToDate( String value ) {
		try {
			return GENERALIZED_TIME_FORMAT.parse( value.toString() );
		} catch (ParseException e) {
			LOGGER.debug( e.getMessage(), e );
		}
		return null;		
	}

	public static String convertToString( Date value ) {
		return GENERALIZED_TIME_FORMAT.format( value );
	}

	public static Boolean convertToBoolean( String value ) {
		return StringUtils.equals( TRUE_VALUE, value);		
	}

	public static String convertToString( Boolean value ) {
		return value ? TRUE_VALUE : FALSE_VALUE;
	}	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("dn: ").append(dn).append(SystemUtils.LINE_SEPARATOR);
		for( Map.Entry<String,List<Object>> entry : values.entrySet() ) {
			for( Object value : entry.getValue() ) {
				sb.append(entry.getKey()).append(": ").append(value).append(SystemUtils.LINE_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
}