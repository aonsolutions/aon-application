package com.code.aon.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
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
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapSession implements ILdapConstants, IAonObjectClasses {

	public static final String TRUE_VALUE = "TRUE";
	
	public static final String FALSE_VALUE = "FALSE";

	private static final String NUMERIC_OID = "NUMERICOID";

	private static final String BOOLEAN_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.7";

	private static final String INTEGER_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.27";

	private static final String GENERALIZED_TIME_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.24";
	
	private static final String DISTINGUISHED_NAME_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.12";
	
	private static final SimpleDateFormat GENERALIZED_TIME_FORMAT = new SimpleDateFormat( "yyyyMMddHHmmss'Z'" );

	private final static Logger LOGGER = LoggerFactory.getLogger(LdapSession.class);

	private DirContext dc;
	
	private Name baseDN;

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
			String name = StringUtils.trimToEmpty( dc.getNameInNamespace() );
			this.baseDN = NameResolver.getName(name);
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
	
	private Object convertValue(Object value, DirContext syntax)
			throws NamingException {
		Object result = value;
		if ((value != null) && (syntax != null)) {
			Attributes attributes = syntax.getAttributes("",
					new String[] { NUMERIC_OID });
			String oid = (String) attributes.get(NUMERIC_OID).get();
			if (oid.equals(INTEGER_SYNTAX)) {
				result = NumberUtils.createNumber(value.toString());
			} else if (oid.equals(DISTINGUISHED_NAME_SYNTAX)) {
				result = NameResolver.getName(value.toString());
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

	public Name getBaseDN() {
		return this.baseDN;
	}
	
	public Name getFullDN( Name dn ) {
		if (! this.baseDN.isEmpty() ) {
			if (! dn.startsWith(this.baseDN) ) {
				return NameResolver.getName( dn, this.baseDN );	
			}
		}
		return dn;
	}
	
	private Name resolveBase(Name base) {
		if ( (!this.baseDN.isEmpty()) && base.startsWith(this.baseDN) ) {
			return base.getSuffix(this.baseDN.size());
		}
		return base;
	}
	
	private Attribute getAttribute( Name dn, String attributeId ) throws NamingException {
		Attributes attributes = dc.getAttributes( resolveBase(dn), new String[]{attributeId} );
		return attributes.get(attributeId);
	}
	
	private DirContext getSyntax( Entry entry, Attribute attribute ) {
		DirContext syntax = null;
		try {
			syntax = attribute.getAttributeSyntaxDefinition();
		} catch (NamingException e) {
			try {
				Attribute attr = getAttribute(entry.getDN(), attribute.getID());
				syntax = attr.getAttributeSyntaxDefinition();
			} catch (NamingException ne) {
				LOGGER.debug(e.getMessage(), ne);
			}
		}
		return syntax;
	}
	
	private void addAttribute( Entry entry, Attribute attribute ) throws NamingException {
		String name = attribute.getID();
		NamingEnumeration<?> values = attribute.getAll();
		DirContext syntax = getSyntax(entry, attribute);
		while (values.hasMore()) {
			Object value = values.nextElement();
			entry.put(name, convertValue(value, syntax));
		}
	}
	
	private Name getName( Name base, Scope scope, SearchResult sr ) {
		Name result = null;
		if ( scope == Scope.OBJECT_SCOPE ) {
			result = base;
		} else {
			if ( sr.isRelative() ) {
				Name name = NameResolver.getName(sr.getName());				
				if (! name.isEmpty() ) {
					if (! base.isEmpty() ) {
						result = NameResolver.getName( name, base );
					} else {
						result = name;	
					}
				} else {
					result = base;	
				}
			}
		}
		return result;
	}

	private Entry getEntry(Name base, Scope scope, SearchResult sr) throws NamingException {
		Entry entry = new Entry(NameResolver.getName(sr.getNameInNamespace()));
		Name name = getName(base, scope, sr);
		if ( (name != null) && (!name.isEmpty()) ) {
			Name searchDN = getFullDN(name);
			if (! searchDN.equals(entry.getDN()) ) {			
				entry.setSearchDN( searchDN );
			}
		}
		Attributes at = sr.getAttributes();
		NamingEnumeration<? extends Attribute> ane = at.getAll();
		while (ane.hasMore()) {
			Attribute attribute = ane.next();
			addAttribute( entry, attribute );
		}
		return entry;
	}

	public List<Entry> search(Name base, String filter, Scope scope,
			String... attributes) throws LdapException {
		List<Entry> results = new ArrayList<Entry>();
		try {
			SearchControls sc = getSearchControls(scope, attributes);
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base),
					filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				results.add(getEntry(base, scope, sr));
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

	public boolean exists(Name base, String filter) throws LdapException {
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
	
	private Entry get(Name base, String filter, Scope scope,
			String... attributes) throws LdapException {
		Entry entry = null;
		try {
			SearchControls sc = getSearchControls(scope, attributes);
			NamingEnumeration<SearchResult> ne = dc.search(resolveBase(base),
					filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();
				if (entry == null) {
					entry = getEntry(base, scope, sr);
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

	public Entry get(Name base, String filter, String... attributes)
			throws LdapException {
		return this.get(base, filter, Scope.OBJECT_SCOPE, attributes);
	}

	public Entry searchOne(Name base, String filter, String... attributes)
			throws LdapException {
		return this.get(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public List<Entry> search(Name base, String filter, String... attributes)
			throws LdapException {
		return this.search(base, filter, Scope.ONELEVEL_SCOPE, attributes);
	}

	public int getCount(Name base, String filter, Scope scope,
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

	public int getCount(Name base, String filter, String... attributes)
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
			dc.createSubcontext( resolveBase(entry.getDN()), attributes);
		} catch (NameAlreadyBoundException nabe) {
			throw new LdapException("Entry Already Exists: " + entry.getDN(),
					nabe);
		} catch (InvalidAttributesException iae) {
			throw new LdapException("Invalid Attributes", iae);
		} catch (NamingException ne) {
			throw new LdapException("Error in add. " + ne.getMessage(), ne);
		}
	}

	public void delete(Name dn) throws LdapException {
		try {
			dc.destroySubcontext( resolveBase(dn) );
		} catch (NamingException ne) {
			throw new LdapException("Error in delete: " + dn + ", " + ne.getMessage(), ne);
		}
	}
	
	private List<Entry> getList( Name dn ) throws LdapException {
		return search( dn, NameResolver.getObjectClass("*"), OBJECT_CLASS_ATTRIBUTE);
	}
	
	public void deleteDepth(Name dn, boolean selfDelete) throws LdapException {	
		Name fullDN = getFullDN(dn);
		List<Entry> list = getList(dn);
		for( Entry child : list ) {
			Name childDN = child.getDN();
			if ( child.hasObjectClass(REFERRAL) ) {
				delete(childDN);
			} else if ( childDN.startsWith(fullDN) ) {
				deleteDepth(childDN, true);	
			} else {
				LOGGER.debug( "Skipping {}", childDN );
			}				
		}
		if ( selfDelete ) {
			delete(dn);	
		}
	}

	public void rename(Name dn, Name newDN) throws LdapException {
		try {
			dc.rename( resolveBase(dn), resolveBase(newDN) );
		} catch (NamingException ne) {
			throw new LdapException("Error in rename. " + ne.getMessage(), ne);
		}
	}

	public void removeAttributes(Name dn, String attribute, String ... moreAttributes ) throws LdapException {
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
	
	public void addAttribute(Name dn, String name, Object value) throws LdapException {
		try {
			ModificationItem[] items = new ModificationItem[1];

			Attribute attribute = getAttribute(name, value);
			items[0] = new ModificationItem( DirContext.ADD_ATTRIBUTE, attribute );
			dc.modifyAttributes( resolveBase(dn), items );
		} catch (NamingException ne) {
			throw new LdapException("Error in add Attribute. " + ne.getMessage(), ne);
		}
	}

	public void replaceAttribute(Name dn, String name, Object value ) throws LdapException {
		try {
			ModificationItem[] items = new ModificationItem[1];

			Attribute attribute = getAttribute(name, value);
			items[0] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attribute );
			dc.modifyAttributes( resolveBase(dn), items );
		} catch (NamingException ne) {
			throw new LdapException("Error in replace Attribute. " + ne.getMessage(), ne);
		}
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

	public void updateAttribute( Name dn, String name, Object oldValue, Object newValue ) throws LdapException {
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
