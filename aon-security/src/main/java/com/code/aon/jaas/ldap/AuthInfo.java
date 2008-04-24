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
		Domain domain = Domain.get(ldap, domainName);
    	if ( domain == null ) {
    		throw new AuthenticationLoginException( "aon_login_err_2", domainName );
    	}
		return domain.getAccessPolicy();
	}

	@Override
	public IRelation getProfileRelation(String domainName, String context,
			String name) throws AuthenticationLoginException {
		IRelation relation = null;
		String application = ldap.getApplicationId(context);
		Entry profile = ldap.getProfile(domainName, application, name);
		if ( profile != null ) {
			relation = ldap.getProfile(profile);
		}
		return relation;
	}

	@Override
	public String getUserPassword(String domainName, String name)
			throws AuthenticationLoginException {
		Entry user = ldap.getUser(domainName, name);
		if ( user != null ) {
			return ldap.getUser(user).getPasswd();
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
