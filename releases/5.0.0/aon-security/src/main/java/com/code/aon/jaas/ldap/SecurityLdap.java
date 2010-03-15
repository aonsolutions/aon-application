package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;

public class SecurityLdap extends BasicLdap implements ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(SecurityLdap.class);
	
	private static final String LOGIN_ERROR_PREFFIX = "aon_login_error_";
	
	public static final String NOT_SUPPORTED = "Not supported!";
	
	public SecurityLdap( Properties properties ) {
		super( properties );
	}
	
	@SuppressWarnings("unchecked")
	public IRelation getProfile( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		Object value = entry.get( MEMBER_ATTRIBUTE );
		if ( value instanceof String ) {
			Name member = NameResolver.getName( (String) value );
			relation.addRelation( NameResolver.getFirstValue(member) );
		} else {
			for( String profile : (List<String>) value ) {
				Name dn = NameResolver.getName( profile );
				relation.addRelation( NameResolver.getFirstValue(dn) );
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
		Name dn = Domain.getDN(domainName);
		return exists(dn, DOMAIN);
	}

	public boolean hasUser( String domainId, String applicationId, String userId ) throws AuthenticationLoginException {
		Object[] arguments = new Object[] { userId, applicationId, domainId };
		// Validación de que el status del Dominio es correcto
		Domain domain = Domain.get(this, domainId);
    	if ( domain.getStatus() > 10 ) {
    		throw new AuthenticationLoginException( LOGIN_ERROR_PREFFIX + domain.getStatus(), arguments );
    	}
		// Validación de que el Usuario esta activado
    	Entry userEntry = Domain.getUser(this, domainId, userId);
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
    	Entry domainUserEntry = domainApplication.getDomainApplicationUser(userId);
    	if ( domainUserEntry == null ) {
    		return false;
    	}
    	if ( domainUserEntry.containsKey(STATUS_ATTRIBUTE) ) {
	    	int status = domainUserEntry.getAsInteger(STATUS_ATTRIBUTE);
	    	if ( status > 10 ) {
	    		throw new AuthenticationLoginException( LOGIN_ERROR_PREFFIX + status, arguments );
	    	}
	    }		
		return true;
	}
	
	public void deleteAttribute( Name dn, String attribute, String ... moreAttributes ) {
		try {
			getLdapSession().removeAttributes(dn, attribute, moreAttributes);
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
	}
	
	public Entry getApplicationProfile( String domainName, String application, String profileName ) {
		Name dn = NameResolver.getApplicationProfileDN(application, profileName);
		return get( dn, PROFILE );
	}

	public Entry getProfile( String domainName, String application, String profileName ) {
		Entry profile = null;
		Name dn = NameResolver.getApplicationProfileDN(application, profileName);
		if ( exists(dn, PROFILE) ) {
			profile = getApplicationProfile(domainName, application, profileName);
		} else {
			dn = NameResolver.getDomainApplicationProfileDN(domainName, application, profileName);
			if ( exists(dn, DOMAIN_APPLICATION_PROFILE) ) {
				profile = getDomainApplicationProfile(domainName, application, profileName);
			}
		}
		return profile;
	}

	private Entry getDomainApplicationProfile( String domainName, String application, String profileName ) {
		Name dn = NameResolver.getDomainApplicationProfileDN(domainName, application, profileName);
		return get( dn, DOMAIN_APPLICATION_PROFILE );
	}	
		
	public Application getApplication4Ctx( String context ) {
		String applicationId = getApplicationId(context);
		Application application = Application.get(this, applicationId);
		if ( application != null ) {
			application.setContext(context);
		}
		return application;
	}
	
	public List<String> getUserApplications(String domainId, String userId) {
		List<String> applications = new ArrayList<String>();
		try {
			LdapSession session = getLdapSession();
			String objectClass = NameResolver.getObjectClass(DOMAIN_APPLICATION_USER);
			String cn = NameResolver.getCommonName(userId);
			Name dn = DomainApplication.getParentDN(domainId);
			List<Entry> list = session.search( dn, NameResolver.getAndExpression(objectClass, cn), Scope.SUBTREE_SCOPE, COMMON_NAME_ATTRIBUTE);
			for( Entry entry : list ) {
				String application = NameResolver.getValue(entry.getDN(), 2);
				applications.add( application );
			}
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return applications;
	}	

	public void updateUser( String algorithm, String domainId, IUser user, String oldUserId ) {
		try {
			Entry userEntry = Domain.getUser(this, domainId, user.getId());
			LdapSession session = getLdapSession();
			session.updateAttribute( userEntry, DESCRIPTION_ATTRIBUTE, user.getDescription());
			String password = user.getPasswd();
			if ( algorithm != null ) {
				password = "{" + algorithm + "}" + password;
			}
			session.updateAttribute( userEntry, USER_PASSWORD_ATTRIBUTE, password);
			if ( (oldUserId != null) && (! user.getId().equals(oldUserId)) ) {
				Name newDN = NameResolver.getUserDN(domainId, user.getId());
				session.rename(userEntry.getDN(), newDN);
			}
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
	}
	
	private String getOrganizationName( String domainId ) {
		String organizationName = null;
		Name dn = Domain.getDN(domainId);
		Entry entry = get( dn, DOMAIN );
		if ( (entry != null) && entry.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
			organizationName = entry.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
		}
		return organizationName;		
	}

	public void addUser( String algorithm, String domainId, IUser user ) {
		try {
			String organizationName = getOrganizationName(domainId);
			LdapSession session = getLdapSession();
			Name dn = NameResolver.getUserDN( domainId, user.getId());
			Entry entry = new Entry(dn);
			entry.addObjectClass(AMAVIS_ACCOUNT);
			entry.addObjectClass(USER);
			entry.addObjectClass(INET_ORG_PERSON);
			entry.addObjectClass(ORGANIZATIONAL_PERSON);
			entry.addObjectClass(PERSON);
			entry.addObjectClass(POSIX_ACCOUNT);
			entry.addObjectClass(TOP);
			entry.put( ACTIVE_ATTRIBUTE, LdapSession.FALSE_VALUE );
			String cn = StringUtils.trim(user.getName());
			String sn = StringUtils.trim(user.getName());
			int pos = cn.indexOf(" ");
			if ( pos != -1 ) {
				cn = cn.substring(0, pos);
				sn = StringUtils.substring(sn, pos+1);
			}
			entry.put( COMMON_NAME_ATTRIBUTE, cn );
			entry.put( GROUP_ID_NUMBER_ATTRIBUTE, 100 );
			entry.put( HOME_DIRECTORY_ATTRIBUTE, "/home/DOMAINS/" + domainId + "/USERS/" + user.getId() );
			entry.put( SURNAME_ATTRIBUTE, sn );
			entry.put( USER_ID_ATTRIBUTE, user.getId() );
			entry.put( USER_ID_NUMBER_ATTRIBUTE, 10 );
			entry.put( DESCRIPTION_ATTRIBUTE, user.getDescription() );
			String password = user.getPasswd();
			if ( algorithm != null ) {
				password = "{" + algorithm + "}" + password;
			}
			entry.put( USER_PASSWORD_ATTRIBUTE, password );
			if ( organizationName != null ) {
				entry.put(ORGANIZATION_NAME_ATTRIBUTE, organizationName);
			}
			session.add(entry);
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
	}
	
}
