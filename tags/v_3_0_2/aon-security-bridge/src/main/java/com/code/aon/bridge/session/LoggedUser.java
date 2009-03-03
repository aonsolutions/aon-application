package com.code.aon.bridge.session;

import javax.faces.event.AbortProcessingException;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class LoggedUser implements ILdapConstants, IAonObjectClasses {

	private boolean logged;
	
	private Entry aonUser;
	
	public LoggedUser() {
		AuthPrincipal principal = Utils.getAuthPrincipal();
		if ( (principal != null) && (!IConstants.UNAUTHENTICATED_IDENTITY.equals(principal.getName())) ) {
			this.logged = true;
			this.aonUser = getAonUser( principal );
		}
	}

	private Entry getAonUser( AuthPrincipal principal ) {
		Entry entry = null;
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN(principal.getDomain(), principal.getShortName());
			String filter = LdapSession.getObjectClass(USER);
			entry = ldap.getLdapSession().get(dn.toString(), filter);
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting aonUser for " + principal + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return entry;
	}		
    
    public boolean isLogged(){
    	return logged;
    }    
    
    public String getCompanyName(){
    	if (aonUser.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
    		return aonUser.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
    	}    	
    	return null;
    }

    public String getLoggedUserName() {
    	String userName = aonUser.getAsString(COMMON_NAME_ATTRIBUTE);
    	if (aonUser.containsKey(SURNAME_ATTRIBUTE) ) {
    		userName += " " + aonUser.getAsString(SURNAME_ATTRIBUTE);
    	}
        return userName;
    }    
	
}
