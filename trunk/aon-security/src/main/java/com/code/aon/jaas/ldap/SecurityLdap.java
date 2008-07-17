package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.DataSourceMetaData;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.client.ast.core.Role;
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.Scope;

public class SecurityLdap extends BasicLdap implements ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( SecurityLdap.class.getName() );
	
	private static final String LOGIN_ERROR_PREFFIX = "aon_login_error_";
	
	public SecurityLdap( Properties properties ) {
		super( properties );
	}
	
	private static String getAndExpression( String expression1, String expression2 ) {
		return "(&" + expression1 + expression2 + ")";
	}
	
	public IRelation getProfile( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		Object value = entry.get( MEMBER_ATTRIBUTE );
		if ( value instanceof String ) {
			DistinguishedName member = new DistinguishedName( (String) value );
			relation.addRelation( member.getLevelValue(0) );
		} else {
			for( String profile : (List<String>) value ) {
				DistinguishedName dn = new DistinguishedName( profile );
				relation.addRelation( dn.getLevelValue(0) );
			}
		}
		return relation;
	}
	
	public String getApplicationId( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		int pos = application.lastIndexOf(".");
		if ( pos != -1 ) {
			application = application.substring(0, pos);
		}
		return application;
	}
	
	public boolean hasDomain( String domainName ) {
		DistinguishedName dn = Domain.getDN(domainName);
		return exists(dn, Domain.OBJECT_CLASS);
	}

	public boolean hasUser( String domainId, String applicationId, String user ) throws AuthenticationLoginException {
		Object[] arguments = new Object[] { user, applicationId, domainId };
		// Validación de que el status del Dominio es correcto
		Domain domain = Domain.get(this, domainId);
    	if ( domain.getStatus() > 10 ) {
    		throw new AuthenticationLoginException( LOGIN_ERROR_PREFFIX + domain.getStatus(), arguments );
    	}
		// Validación de que el Usuario esta activado
    	Entry userEntry = getUser(domainId, user);
    	if ( userEntry == null ) {
    		return false;
    	}
    	boolean active = userEntry.getAsBoolean(ACTIVE_ATTRIBUTE);
    	if (! active ) {
    		throw new AuthenticationLoginException( "aon_login_user_inactive", arguments );
    	}
		// Validación de que el status de la Aplicación del Dominio es correcto
    	DomainApplication domainApplication = DomainApplication.get(this, domainId, applicationId);
    	if ( domainApplication == null ) {
    		return false;
    	}
    	if ( domainApplication.getStatus() > 10 ) {
    		throw new AuthenticationLoginException( LOGIN_ERROR_PREFFIX + domainApplication.getStatus(), arguments );
    	}
		// Validación de que el status del Usuario de la Aplicación es correcto
    	Entry domainUserEntry = getDomainApplicationUser(domainId, applicationId, user);
    	if ( domainUserEntry == null ) {
    		return false;
    	}
    	int status = domainUserEntry.getAsInteger(STATUS_ATTRIBUTE);
    	if ( status > 10 ) {
    		throw new AuthenticationLoginException( LOGIN_ERROR_PREFFIX + status, arguments );
    	}		
		return true;
	}
	
	public Entry getUser( String domainName, String user ) {
		Entry entry = null;
		try {
			String objectClass = LdapSession.getObjectClass(USER);
			DistinguishedName dn = AonDN.getUserDN(domainName, user);
			entry = getLdapSession().get( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
		return entry;
	}

	public Entry getDomainApplicationUser( String domainName, String application, String user ) {
		Entry entry = null;
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_USER);
			DistinguishedName dn = AonDN.getDomainApplicationUserDN(domainName, application, user);
			entry = getLdapSession().get( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
		return entry;
	}

	public void updateRelation( DistinguishedName dn, IRelation relation ) {
		try {
			LdapSession session = getLdapSession();
			String appId = dn.getLevelValue(2);
			List<Object> members = new LinkedList<Object>();
			for( String role : relation.relations() ) {
				DistinguishedName member = session.getFullDN( AonDN.getApplicationProfileDN(appId, role) );
				members.add( member.toString() );
			}
			session.replaceAttribute(dn, MEMBER_ATTRIBUTE, members);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
	}

	public void deleteAttribute( DistinguishedName dn, String attribute, String ... moreAttributes ) {
		try {
			getLdapSession().removeAttributes(dn, attribute, moreAttributes);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
	}
	
	public Entry getApplicationProfile( String domainName, String application, String profileName ) {
		Entry entry = null;
		try {
			String objectClass = LdapSession.getObjectClass(PROFILE);
			DistinguishedName dn = AonDN.getApplicationProfileDN(application, profileName);
			entry = getLdapSession().get( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
		return entry;
	}

	public Entry getDomainApplicationProfile( String domainName, String application, String profileName ) {
		Entry entry = null;
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_PROFILE);
			DistinguishedName dn = AonDN.getDomainApplicationProfileDN(domainName, application, profileName);
			entry = getLdapSession().get( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
		return entry;
	}

	public Entry getProfile( String domainName, String application, String profileName ) {
		Entry profile = null;
		DistinguishedName dn = AonDN.getApplicationProfileDN(application, profileName);
		if ( exists(dn, PROFILE) ) {
			profile = getApplicationProfile(domainName, application, profileName);
		} else {
			dn = AonDN.getDomainApplicationProfileDN(domainName, application, profileName);
			if ( exists(dn, DOMAIN_APPLICATION_PROFILE) ) {
				profile = getDomainApplicationProfile(domainName, application, profileName);
			}
		}
		return profile;
	}

	public IRelation getRelation( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		for( Object member : entry.get(MEMBER_ATTRIBUTE) ) {
			DistinguishedName dn = new DistinguishedName( (String) member );
			relation.addRelation( dn.getLevelValue(0) );
		}
		return relation;
	}

	public Entry getDomainApplicationProfile( LdapSession session, IRelation relation, DistinguishedName dn ) {
		Entry entry = new Entry(dn.toString());
		String appId = dn.getLevelValue(2);
		String[] objectClasses = new String[] {TOP, "groupOfNames", DOMAIN_APPLICATION_PROFILE};
		entry.addObjectClasses( objectClasses );
		for( String role : relation.relations() ) {
			DistinguishedName member = session.getFullDN( AonDN.getApplicationProfileDN(appId, role) );
			entry.put( MEMBER_ATTRIBUTE, member.toString() );
		}
		return entry;
	}
	
	public IRole getRole( Entry entry ) {
		Role role = new Role();
		role.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		return role;
	}
	
	public User getUser( Entry entry ) {
		User user = new User();
		user.setId(entry.getAsString(USER_ID_ATTRIBUTE));
		user.setName(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		byte[] password = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);		
		int offset = ArrayUtils.indexOf( password, (byte) '}' ) + 1;
		user.setPasswd( new String(password, offset, password.length-offset) );
		if ( entry.containsKey(DESCRIPTION_ATTRIBUTE) ) {
			user.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));			
		}
		return user;
	}
	
	public IDataSourceMetaData getDataSourceMetaData( Entry entry ) {
		DataSourceMetaData dataSource = new DataSourceMetaData();
		dataSource.setUsername( entry.getAsString("uid") );
		byte[] password = entry.getAsByteArray("userPassword");
		dataSource.setPassword( new String(password) );
		dataSource.setConnectionURL( entry.getAsString("labeledURI") );
		dataSource.setDriverClass( entry.getAsString("driverClassName") );
		return dataSource;
	}
	
	public Application getApplication4Ctx( String context ) {
		String applicationId = getApplicationId(context);
		Application application = Application.get(this, applicationId);
		if ( application != null ) {
			application.setContext(context);
		}
		return application;
	}
	
	public IRelation getUserRelation(String domainName, String application, String name) {
		Relation relation = null;
		Entry user = getDomainApplicationUser(domainName, application, name);
		if ( user != null ) {
			relation = new Relation(name);
			Object value = user.get( MEMBER_ATTRIBUTE );
			if ( value instanceof String ) {
				DistinguishedName member = new DistinguishedName( (String) value );
				relation.addRelation( member.getLevelValue(0) );
			} else {
				for( String profile : (List<String>) value ) {
					DistinguishedName dn = new DistinguishedName( profile );
					relation.addRelation( dn.getLevelValue(0) );
				}
			}
		}
		return relation;
	}	
	
	public List<String> getUserApplications(String domainId, String userId) {
		List<String> applications = new ArrayList<String>();
		try {
			LdapSession session = getLdapSession();
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_USER);
			String cn = LdapSession.getCommonName(userId);
			DistinguishedName dn = DomainApplication.getParentDN(domainId);
			List<Entry> list = session.search( dn.toString(), getAndExpression(objectClass, cn), Scope.SUBTREE_SCOPE, COMMON_NAME_ATTRIBUTE);
			for( Entry entry : list ) {
				String application = entry.getDN().getLevelValue( 2 );
				applications.add(application);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
		return applications;
	}	

	public void updateUser( String algorithm, String domainId, IUser user, String oldUserId ) {
		try {
			Entry userEntry = getUser(domainId, user.getId());
			LdapSession session = getLdapSession();
			session.updateAttribute( userEntry, DESCRIPTION_ATTRIBUTE, user.getDescription());
			String password = user.getPasswd();
			if ( algorithm != null ) {
				password = "{" + algorithm + "}" + password;
			}
			session.updateAttribute( userEntry, USER_PASSWORD_ATTRIBUTE, password);
			if ( (oldUserId != null) && (! user.getId().equals(oldUserId)) ) {
				DistinguishedName newDN = AonDN.getUserDN(domainId, user.getId());
				session.rename(userEntry.getDN().toString(), newDN.toString());
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession();
		}
	}
	
}
