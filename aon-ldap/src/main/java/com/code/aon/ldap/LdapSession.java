package com.code.aon.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
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
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapSession {

	private static final String TRUE_VALUE = "TRUE";

	private static final String NUMERIC_OID = "NUMERICOID";

	private static final String BOOLEAN_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.7";

	private static final String INTEGER_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.27";
	
	private static final String DISTINGUISHED_NAME_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.12";

	private static final Log LOGGER = LogFactory.getLog(LdapSession.class
			.getName());

	private DirContext dc;

	public void open(String host, int port, String user, String password, boolean ssl) throws LdapException {
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
		open( properties );
	}
	
	public void open(String host, String user, String password) throws LdapException {
		this.open(host, -1, user, password, false);
	}
	
	public void open( Properties properties ) throws LdapException {
		try {
			this.dc = new InitialDirContext(properties);
		} catch (NamingException e) {
			throw new LdapException( "Error opening LDAP connection", e );
		}
	}

	public void close() throws LdapException {
		try {
			dc.close();
		} catch (NamingException ne) {
			throw new LdapException( "Error in close", ne );
		}
	}

	public DirContext getDc() {
		return dc;
	}
	
	private Object convertValue( Object value, DirContext syntax ) throws NamingException {
		Object result = value;
		if ( (value != null) && (syntax != null) ) {
			Attributes attributes = syntax.getAttributes("", new String[]{NUMERIC_OID});
			String oid = (String) attributes.get(NUMERIC_OID).get();
			if ( oid.equals(INTEGER_SYNTAX) ) {
				result = Integer.valueOf(value.toString());
			} else if ( oid.equals(DISTINGUISHED_NAME_SYNTAX) ) {
				result = new DistinguishedName(value.toString());
			} else if ( oid.equals(BOOLEAN_SYNTAX) ) {
				result = TRUE_VALUE.equals(value) ? Boolean.TRUE : Boolean.FALSE;
			}
		}
		return result;
	}
	
	private Object getValues( Attribute attribute ) throws NamingException {
		NamingEnumeration<?> values = attribute.getAll();
		DirContext syntax = null;
		try {
			syntax = attribute.getAttributeSyntaxDefinition();
		} catch ( NamingException e ) {
			LOGGER.debug( e.getMessage(), e );
		}
		List<Object> list = new ArrayList<Object>();
		while (values.hasMore()) {
			Object value = values.nextElement();
			list.add( convertValue(value, syntax) );
		}
		return ( list.isEmpty() ) ? null : list;
	}

	private SearchControls getSearchControls( Scope scope, String[] attributes ) {
		SearchControls sc = new SearchControls();
		sc.setSearchScope(scope.getScope());

		// Reduce data provided by the LDAP server by listing
		// only those attributes we want to return.

		if (! ArrayUtils.isEmpty(attributes) ) {
			sc.setReturningAttributes(attributes);
		}
		return sc;
	}
	
	private String resolveBase( String base ) throws NamingException {
		String name = dc.getNameInNamespace();
		if ( base.endsWith(name) ) {
			return base.substring(0, base.length()-name.length()-1 );
		}
		return base;
	}
	
	private Entry getEntry( String base, SearchResult sr ) throws NamingException {
		String name = sr.getName();
		if (! StringUtils.isEmpty(base) ) {
			name += ',' + base;
		}
		Entry entry = new Entry(name);

		Attributes at = sr.getAttributes();
		NamingEnumeration<? extends Attribute> ane = at.getAll();
		while (ane.hasMore()) {
			Attribute attribute = ane.next();
			String attrType = attribute.getID();
			entry.put(attrType, getValues(attribute));
		}
		return entry;
	}
	
	public List<Entry> search(String base, String filter, Scope scope, String ... attributes) throws LdapException {
		List<Entry> results = new ArrayList<Entry>();
		try {
			SearchControls sc = getSearchControls( scope, attributes );
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base), filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				results.add( getEntry(base, sr) );
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException( "Search Filter Invalid: " + filter, isfe );
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException( "Control Filter Invalid", isce );
		} catch (NamingException ne) {
			throw new LdapException("Error in search. " + ne.getMessage(), ne);
		}
		return results;
	}

	private Entry get(String base, String filter, Scope scope, String ... attributes) throws LdapException {
		Entry entry = null;
		try {
			SearchControls sc = getSearchControls( scope, attributes );
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base), filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				if ( entry == null ) {
					entry = getEntry(base, sr);					
				} else {
					return null;
				}
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException( "Search Filter Invalid: " + filter, isfe );
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException( "Control Filter Invalid", isce );
		} catch (NamingException ne) {
			throw new LdapException("Error in get. " + ne.getMessage(), ne);
		}
		return entry;
	}

	public Entry get(String base, String filter, String ... attributes) throws LdapException {
		return this.get(base, filter, Scope.OBJECT_SCOPE, attributes);
	}
	
	public Entry searchOne(String base, String filter, String ... attributes) throws LdapException {
		return this.get(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}
	
	public List<Entry> search(String base, String filter, String ... attributes) throws LdapException {
		return this.search(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public int getCount(String base, String filter, Scope scope, String ... attributes) throws LdapException {
		int count = -1;
		try {
			SearchControls sc = getSearchControls( scope, attributes );
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base), filter, sc);
			count = 0;
			while (ne.hasMore()) {
				ne.next();
				count++;
			}
		} catch (InvalidSearchFilterException isfe) {
			throw new LdapException( "Search Filter Invalid: " + filter, isfe );
		} catch (InvalidSearchControlsException isce) {
			throw new LdapException( "Control Filter Invalid", isce );
		} catch (NamingException ne) {
			throw new LdapException("Error in getCount. " + ne.getMessage(), ne);
		}
		return count;
	}
		
	public int getCount(String base, String filter, String ... attributes) throws LdapException {
		return this.getCount(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public void add(Entry entry) throws LdapException {
		try {
			Attributes attributes = new BasicAttributes();
			for( Map.Entry<String,List<Object>> mapEntry : entry.entrySet() ) {
				Attribute attribute = new BasicAttribute( mapEntry.getKey() );
				for( Object object : mapEntry.getValue() ) {
					attribute.add( object );
				}
				attributes.put(attribute);
			}
			dc.createSubcontext(entry.getDN().toString(), attributes);
		} catch (NameAlreadyBoundException nabe) {
			throw new LdapException("Entry Already Exists: " + entry.getDN(), nabe);
		} catch (InvalidAttributesException iae) {
			throw new LdapException("Invalid Attributes", iae);
		} catch (NamingException ne) {
			throw new LdapException("Error in add. " + ne.getMessage(), ne);
		}			
	}

	public void delete( DistinguishedName dn ) throws LdapException {
		this.delete( dn.toString() );
	}
	
	public void delete( String dn ) throws LdapException {
		try {
			dc.destroySubcontext( dn );			
		} catch (NamingException ne) {
			throw new LdapException("Error in delete. " + ne.getMessage(), ne);
		}			
	}
	
}
