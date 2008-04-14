package com.code.aon.jaas.ldap;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.jaas.auth.IAuthInfo;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;

public class AuthInfo implements IAuthInfo, ILdapConstants, ILdapSecurityConstants {

	private SecurityLdap ldap;
	
	public AuthInfo(SecurityLdap ldap) {
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
		String application = ldap.getApplicationId(context);
		Entry profile = ldap.getProfile(domainName, application, name);
		if ( profile != null ) {
			relation = new Relation(name);
			Object value = profile.get( MEMBER );
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
			byte[] password = user.getAsByteArray(USER_PASSWORD);
			int offset = ArrayUtils.indexOf( password, (byte) '}' ) + 1;
			return new String( password, offset, password.length-offset );
		}
		return null;
	}

	@Override
	public IRelation getUserRelation(String domainName, String context,
			String name) throws AuthenticationLoginException {
		String application = ldap.getApplicationId(context);
		return ldap.getUserRelation(domainName, application, name);
	}
	
	@Override
	public boolean hasUser(String domainName, String context, String name)
			throws AuthenticationLoginException {
    	if ( ! ldap.hasDomain( domainName ) ) {
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );
    	}
    	String application = ldap.getApplicationId(context);
		return ldap.hasUser(domainName, application, name);
	}
	
}
