package com.code.aon.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LDAPSession {

	private static final String BOOLEAN_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.7";

	private static final String INTEGER_SYNTAX = "1.3.6.1.4.1.1466.115.121.1.27";

	private static final Log LOGGER = LogFactory.getLog(LDAPSession.class
			.getName());

	private DirContext dc;
	
	private String baseDN;
	
	private Scope scope;

	public void open(String host, int port, String user, String password,
			boolean ssl) throws NamingException {
		Properties properties = new Properties();
		properties.put(DirContext.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
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
		this.dc = new InitialDirContext(properties);
	}

	public void open(String host, String user, String password)
			throws NamingException {
		this.open(host, -1, user, password, false);
	}

	public void close() {
		try {
			dc.close();
		} catch (NamingException ne) {
			LOGGER.error(ne.getMessage(), ne);
		}
	}

	public DirContext getDc() {
		return dc;
	}
	
	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	private Object convertValue( Object value, DirContext syntax ) throws NamingException {
		Object result = value;
		if ( (value != null) && (syntax != null) ) {
			Attributes attributes = syntax.getAttributes("", new String[]{"NUMERICOID"});
			String oid = (String) attributes.get("NUMERICOID").get();
			if ( oid.equals(INTEGER_SYNTAX) ) {
				result = Integer.valueOf(value.toString());
			} else if ( oid.equals(BOOLEAN_SYNTAX) ) {
				result = "TRUE".equals(value) ? Boolean.TRUE : Boolean.FALSE;
			}
		}
		return result;
	}
	
	private List<Object> getValues( Attribute attribute ) throws NamingException {
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
			list.add( convertValue(value, syntax));
		}
		return list;
	}

	public List<Entry> search(String base, String filter, String ... attributes) {
		List<Entry> results = new ArrayList<Entry>();

		SearchControls sc = new SearchControls();
		sc.setSearchScope(scope.getScope());

		// Reduce data provided by the LDAP server by listing
		// only those attributes we want to return.

		if (! ArrayUtils.isEmpty(attributes) ) {
			sc.setReturningAttributes(attributes);
		}

		String searchBase = null;
		if (! StringUtils.isEmpty(this.baseDN) ) {
			searchBase = this.baseDN;
		}
		if (! StringUtils.isEmpty(base) ) {
			searchBase = base + ((searchBase != null) ? "," : "") + searchBase;
		}
		
		try {
			NamingEnumeration<SearchResult> ne = dc.search(searchBase, filter, sc);
			while (ne.hasMore()) {
				SearchResult sr = ne.next();

				String name = sr.getName();
				Entry entry = new Entry();
				if (! StringUtils.isEmpty(searchBase) ) {
					entry.setDN(name + "," + searchBase);
				} else {
					entry.setDN(name);
				}

				Attributes at = sr.getAttributes();
				NamingEnumeration<? extends Attribute> ane = at.getAll();
				while (ane.hasMore()) {
					Attribute attribute = ane.next();
					String attrType = attribute.getID();
					entry.put(attrType, getValues(attribute));
				}
				results.add(entry);
			}
		} catch (InvalidSearchFilterException isfe) {
			LOGGER.error("Search Filter Invalid: " + filter);
		} catch (NameNotFoundException nnfe) {
			LOGGER.error("Object Not Found: " + searchBase);
		} catch (NoPermissionException npe) {
			LOGGER.error("Search Failed: Permission Denied");
		} catch (CommunicationException ce) {
			LOGGER.error("Error Communicating with Server");
		} catch (NamingException nex) {
			LOGGER.error("Error: " + nex.getMessage());
		}

		return results;
	}
		
}
