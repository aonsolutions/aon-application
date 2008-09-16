package com.code.aon.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributesException;
import javax.naming.directory.InvalidSearchControlsException;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapSession implements ILdapConstants {

	public static final String TRUE_VALUE = "TRUE";
	
	public static final String FALSE_VALUE = "FALSE";

	private static final String NUMERIC_OID = "NUMERICOID";

	private static final String BOOLEAN_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.7";

	private static final String INTEGER_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.27";

	private static final String GENERALIZED_TIME_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.24";
	
	private static final String DISTINGUISHED_NAME_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.12";
	
	private static final SimpleDateFormat GENERALIZED_TIME_FORMAT = new SimpleDateFormat( "yyyyMMddHHmmss'Z'" );

	private static final Log LOGGER = LogFactory.getLog(LdapSession.class
			.getName());

	private DirContext dc;
	
	private String baseDN;

	public void open(String host, int port, String user, String password,
			boolean ssl) throws LdapException {
		Properties properties = new Properties();
		String url = "ldap://" + host;
		if (port > 0) {
			url += ":" + port;
		}
		properties.put(DirContext.PROVIDER_URL, url);
		if (user != null) {
			properties.put(DirContext.SECURITY_PRINCIPAL, user);
			properties.put(DirContext.SECURITY_CREDENTIALS, password);
		}
		if (ssl) {
			properties.put(Context.SECURITY_PROTOCOL, "ssl");
		}
		open(properties);
	}

	public void open(String host, String user, String password)
			throws LdapException {
		this.open(host, -1, user, password, false);
	}

	public void open(Properties properties) throws LdapException {
		try {
			this.dc = new InitialDirContext(properties);
			this.baseDN = StringUtils.trimToNull( dc.getNameInNamespace() );
		} catch (NamingException e) {
			throw new LdapException("Error opening LDAP connection", e);
		}
	}

	public void close() throws LdapException {
		try {
			dc.close();
		} catch (NamingException ne) {
			throw new LdapException("Error in close", ne);
		}
	}

	public DirContext getDc() {
		return dc;
	}

	public static String getObjectClass( String objectClass ) {
		return "(" + OBJECT_CLASS_ATTRIBUTE + "=" + objectClass +  ")";
	}

	public static String getCommonName( String cn ) {
		return "(" + COMMON_NAME_ATTRIBUTE + "=" +  cn + ")";
	}
	
	private Object convertValue(Object value, DirContext syntax)
			throws NamingException {
		Object result = value;
		if ((value != null) && (syntax != null)) {
			Attributes attributes = syntax.getAttributes("",
					new String[] { NUMERIC_OID });
			String oid = (String) attributes.get(NUMERIC_OID).get();
			if (oid.equals(INTEGER_SYNTAX)) {
				result = Integer.valueOf(value.toString());
			} else if (oid.equals(DISTINGUISHED_NAME_SYNTAX)) {
				result = new DistinguishedName(value.toString());
			} else if (oid.equals(BOOLEAN_SYNTAX)) {
				result = TRUE_VALUE.equals(value) ? Boolean.TRUE
						: Boolean.FALSE;
			} else if (oid.equals(GENERALIZED_TIME_SYNTAX)) {
				try {
					result = GENERALIZED_TIME_FORMAT.parse( value.toString() );
				} catch (ParseException e) {
					LOGGER.error( "Error parsing Generalized Time: " + value, e );
				}
			}
		}
		return result;
	}

	private SearchControls getSearchControls(Scope scope, String[] attributes) {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(scope.getScope());

		// Reduce data provided by the LDAP server by listing
		// only those attributes we want to return.

		if (!ArrayUtils.isEmpty(attributes)) {
			sc.setReturningAttributes(attributes);
		}
		return sc;
	}

	public String getBaseDN() {
		return this.baseDN;
	}
	
	public DistinguishedName getFullDN( DistinguishedName dn ) {
		if ( this.baseDN != null ) {
			return new DistinguishedName( dn, this.baseDN );
		}
		return dn;
	}
	
	private String resolveBase(String base) throws NamingException {
		if ( (this.baseDN != null) && (base.endsWith(this.baseDN)) ) {
			return base.substring(0, base.length() - this.baseDN.length() - 1);
		}
		return base;
	}
	
	private void addAttribute( Entry entry, Attribute attribute ) throws NamingException {
		String name = attribute.getID();
		NamingEnumeration<?> values = attribute.getAll();
		DirContext syntax = null;
		try {
			syntax = attribute.getAttributeSyntaxDefinition();
		} catch (NamingException e) {
			LOGGER.debug(e.getMessage(), e);
		}
		while (values.hasMore()) {
			Object value = values.nextElement();
			entry.put(name, convertValue(value, syntax));
		}
	}

	private Entry getEntry(String base, SearchResult sr) throws NamingException {
		String name = sr.getName();
		if (!StringUtils.isEmpty(base)) {
			name += ',' + base;
		}
		Entry entry = new Entry(name);

		Attributes at = sr.getAttributes();
		NamingEnumeration<? extends Attribute> ane = at.getAll();
		while (ane.hasMore()) {
			Attribute attribute = ane.next();
			addAttribute( entry, attribute );
		}
		return entry;
	}

	public List<Entry> search(String base, String filter, Scope scope,
			String... attributes) throws LdapException {
		List<Entry> results = new ArrayList<Entry>();
		try {
			SearchControls sc = getSearchControls(scope, attributes);
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base),
					filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				results.add(getEntry(base, sr));
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException("Search Filter Invalid: " + filter, isfe);
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException("Control Filter Invalid", isce);
		} catch (NamingException ne) {
			throw new LdapException("Error in search. " + ne.getMessage(), ne);
		}
		return results;
	}

	public boolean exists(String base, String filter) throws LdapException {
		try {
			SearchControls sc = getSearchControls(Scope.OBJECT_SCOPE, new String[]{OBJECT_CLASS_ATTRIBUTE});
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base), filter, sc);
			while (ne.hasMore()) {
				return true;
			}
		} catch (NameNotFoundException nnfe) {
			LOGGER.debug( "Name not found: " + base, nnfe );
		} catch (NamingException ne) {
			throw new LdapException("Error in get. " + ne.getMessage(), ne);
		}
		return false;
	}
	
	private Entry get(String base, String filter, Scope scope,
			String... attributes) throws LdapException {
		Entry entry = null;
		try {
			SearchControls sc = getSearchControls(scope, attributes);
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base),
					filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				if (entry == null) {
					entry = getEntry(base, sr);
				} else {
					return null;
				}
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException("Search Filter Invalid: " + filter, isfe);
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException("Control Filter Invalid", isce);
		} catch (NamingException ne) {
			throw new LdapException("Error in get. " + ne.getMessage(), ne);
		}
		return entry;
	}

	public Entry get(String base, String filter, String... attributes)
			throws LdapException {
		return this.get(base, filter, Scope.OBJECT_SCOPE, attributes);
	}

	public Entry searchOne(String base, String filter, String... attributes)
			throws LdapException {
		return this.get(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public List<Entry> search(String base, String filter, String... attributes)
			throws LdapException {
		return this.search(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public int getCount(String base, String filter, Scope scope,
			String... attributes) throws LdapException {
		int count = -1;
		try {
			SearchControls sc = getSearchControls(scope, attributes);
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base),
					filter, sc);
			count = 0;
			while (ne.hasMore()) {
				ne.next();
				count++;
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException("Search Filter Invalid: " + filter, isfe);
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException("Control Filter Invalid", isce);
		} catch (NamingException ne) {
			throw new LdapException("Error in getCount. " + ne.getMessage(), ne);
		}
		return count;
	}

	public int getCount(String base, String filter, String... attributes)
			throws LdapException {
		return this.getCount(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public void add(Entry entry) throws LdapException {
		try {
			Attributes attributes = new BasicAttributes();
			for (Map.Entry<String, List<Object>> mapEntry : entry.entrySet()) {
				Attribute attribute = new BasicAttribute(mapEntry.getKey());
				for (Object object : mapEntry.getValue()) {
					if ( object instanceof byte[] ) {
						attribute.add(object);						
					} else {
						attribute.add(object.toString());
					}
				}
				attributes.put(attribute);
			}
			dc.createSubcontext( resolveBase(entry.getDN().toString()), attributes);
		} catch (NameAlreadyBoundException nabe) {
			throw new LdapException("Entry Already Exists: " + entry.getDN(),
					nabe);
		} catch (InvalidAttributesException iae) {
			throw new LdapException("Invalid Attributes", iae);
		} catch (NamingException ne) {
			throw new LdapException("Error in add. " + ne.getMessage(), ne);
		}
	}

	public void delete(DistinguishedName dn) throws LdapException {
		this.delete(dn.toString());
	}

	public void delete(String dn) throws LdapException {
		try {
			dc.destroySubcontext( resolveBase(dn) );
		} catch (NamingException ne) {
			throw new LdapException("Error in delete. " + ne.getMessage(), ne);
		}
	}

	public void rename(String dn, String newDN) throws LdapException {
		try {
			dc.rename( resolveBase(dn), resolveBase(newDN) );
		} catch (NamingException ne) {
			throw new LdapException("Error in rename. " + ne.getMessage(), ne);
		}
	}

	public void removeAttributes(String dn, String attribute, String ... moreAttributes ) throws LdapException {
		try {
			ModificationItem[] items = new ModificationItem[1+moreAttributes.length];

			items[0] = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(attribute) );
			int i = 1;
			for( String value : moreAttributes ) {
				items[i++] = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(value) );
			}
			dc.modifyAttributes( resolveBase(dn), items );
		} catch (NamingException ne) {
			throw new LdapException("Error in remove Attribute. " + ne.getMessage(), ne);
		}
	}

	public void removeAttributes(DistinguishedName dn, String attribute, String ... moreAttributes ) throws LdapException {
		this.removeAttributes(dn.toString(), attribute, moreAttributes);
	}

	@SuppressWarnings("unchecked")
	private Attribute getAttribute( String name, Object value ) {
		Attribute attribute = new BasicAttribute(name);
		if ( List.class.isAssignableFrom(value.getClass()) ) {
			for( Object _value : (List<Object>) value ) {
				attribute.add(_value);
			}				
		} else if ( Boolean.class.isAssignableFrom(value.getClass()) ) {
			Boolean b = (Boolean) value;
			attribute.add( b ? TRUE_VALUE : FALSE_VALUE );
		} else if ( Date.class.isAssignableFrom(value.getClass()) ) {
			String date = GENERALIZED_TIME_FORMAT.format( (Date) value );
			attribute.add( date );
		} else {
			attribute.add(value);
		}
		return attribute;
	}
	
	public void addAttribute(String dn, String name, Object value) throws LdapException {
		try {
			ModificationItem[] items = new ModificationItem[1];

			Attribute attribute = getAttribute(name, value);
			items[0] = new ModificationItem( DirContext.ADD_ATTRIBUTE, attribute );
			dc.modifyAttributes( resolveBase(dn), items );
		} catch (NamingException ne) {
			throw new LdapException("Error in add Attribute. " + ne.getMessage(), ne);
		}
	}

	public void addAttribute(DistinguishedName dn, String name, Object value ) throws LdapException {
		this.addAttribute(dn.toString(), name, value);
	}

	public void replaceAttribute(String dn, String name, Object value ) throws LdapException {
		try {
			ModificationItem[] items = new ModificationItem[1];

			Attribute attribute = getAttribute(name, value);
			items[0] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attribute );
			dc.modifyAttributes( resolveBase(dn), items );
		} catch (NamingException ne) {
			throw new LdapException("Error in replace Attribute. " + ne.getMessage(), ne);
		}
	}

	public void replaceAttribute(DistinguishedName dn, String name, Object value ) throws LdapException {
		this.replaceAttribute(dn.toString(), name, value);
	}
	
	@SuppressWarnings("unchecked")
	private Object getRealValue( Object value ) {
		if ( value != null ) {
			if ( List.class.isAssignableFrom(value.getClass()) ) {
				List<Object> list = (List<Object>) value;
				if ( list.isEmpty() ) {
					return null;
				} else if ( list.size() == 1 ) {
					return list.get(0);
				}
			}
		}
		return value;
	}

	public void updateAttribute( Entry entry, String name, Object newValue ) throws LdapException {
		Object oldValue = entry.containsKey(name) ? entry.get(name) : null;
		this.updateAttribute(entry.getDN(), name, oldValue, newValue);
	}

	public void updateAttribute( DistinguishedName dn, String name, Object oldValue, Object newValue ) throws LdapException {
		Object _oldValue = getRealValue(oldValue);
		Object _newValue = getRealValue(newValue);
		if ( _oldValue != null ) {
			if ( _newValue != null ) {
				if (! ObjectUtils.equals(_newValue, _oldValue) ) {
					replaceAttribute( dn, name, _newValue);	
				}				
			} else {
				removeAttributes( dn, name);
			}						
		} else {
			if ( _newValue != null ) {
				addAttribute( dn, name, _newValue);						
			}						
		}		
	}
	
}
