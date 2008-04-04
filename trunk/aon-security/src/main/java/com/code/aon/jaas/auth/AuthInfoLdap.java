package com.code.aon.jaas.auth;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.ldap.ILdapConstants;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;

public class AuthInfoLdap implements IAuthInfo, ILdapConstants {

	private SecurityLdap ldap;
	
	public AuthInfoLdap(SecurityLdap ldap) {
		this.ldap = ldap;
	}
	
	@Override
	public IAccessPolicy getAccessPolicy(String domainName)
			throws AuthenticationLoginException {
    	if ( ! ldap.hasDomain( domainName ) ) {
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );
    	}
		return ldap.getAccessPolicy(domainName);
	}

	@Override
	public IRelation getProfileRelation(String domainName, String context,
			String name) throws AuthenticationLoginException {
		Relation relation = null;
		String application = ldap.getApplication(context);
		Entry profile = ldap.getProfile(domainName, application, name);
		if ( profile != null ) {
			relation = new Relation(name);
			Object value = profile.get( MEMBER_ATTRIBUTE );
			if ( value instanceof String ) {
				relation.addRelation( ((DistinguishedName) value).getLevelValue(0) );
			} else {
				for( String role : (List<String>) value ) {
					DistinguishedName dn = new DistinguishedName( role );
					relation.addRelation( dn.getLevelValue(0) );
				}
			}
		}
		return relation;
	}

	@Override
	public String getUserPassword(String domainName, String name)
			throws AuthenticationLoginException {
		Entry user = ldap.getUser(domainName, name);
		if ( user != null ) {
			byte[] password = (byte[]) user.get(USER_PASSWORD_ATTRIBUTE);
			int offset = ArrayUtils.indexOf( password, (byte) '}' ) + 1;
			return new String( password, offset, password.length-offset );
		}
		return null;
	}

	@Override
	public IRelation getUserRelation(String domainName, String context,
			String name) throws AuthenticationLoginException {
		Relation relation = null;
		String application = ldap.getApplication(context);
		Entry user = ldap.getDomainApplicationUser(domainName, application, name);
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
	
	@Override
	public boolean hasUser(String domainName, String context, String name)
			throws AuthenticationLoginException {
    	if ( ! ldap.hasDomain( domainName ) ) {
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );
    	}
    	String application = ldap.getApplication(context);
		return ldap.hasUser(domainName, application, name);
	}
	
}
