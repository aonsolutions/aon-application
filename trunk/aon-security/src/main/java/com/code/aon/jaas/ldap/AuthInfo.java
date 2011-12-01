package com.code.aon.jaas.ldap;

import com.code.aon.jaas.auth.IAuthInfo;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
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
	public String getUserPassword(String domainId, String userId)
			throws AuthenticationLoginException {
		Domain domain = Domain.get(ldap, domainId);
		if ( domain != null ) {
			IUser user = domain.getStandaloneUser(userId);
			if ( user != null ) {
				return user.getPasswd();
			}
		}
		return null;
	}

	@Override
	public IRelation getUserRelation(String domainName, String context,
			String name) throws AuthenticationLoginException {
		String application = ldap.getApplicationId(context);
		DomainApplication da = DomainApplication.get(ldap, domainName, application);
		if ( da != null ) {
			return da.getUser(name);
		}
		return null;
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
