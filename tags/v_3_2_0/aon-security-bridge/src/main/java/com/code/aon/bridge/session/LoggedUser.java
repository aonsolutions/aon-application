package com.code.aon.bridge.session;

import java.util.AbstractMap;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

public class LoggedUser implements ILdapConstants, IAonObjectClasses {

	private boolean logged;
	
	private Entry aonUser;
	
	private static final FakeMap USER_IN_ROLE = new FakeMap();
	
	public LoggedUser() {
		AuthPrincipal principal = Utils.getAuthPrincipal();
		if ( (principal != null) && (!IConstants.UNAUTHENTICATED_IDENTITY.equals(principal.getName())) ) {
			this.logged = true;
			this.aonUser = getAonUser( principal );
		}
	}

	private Entry getAonUser( AuthPrincipal principal ) {
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(principal.getDomain(), principal.getShortName());
		return ldap.get(dn, USER);
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
    
	public FakeMap getUserInRole() {
		return USER_IN_ROLE;
	}
    
	@SuppressWarnings("unchecked")
	private static class FakeMap extends AbstractMap<String,Boolean> {
		
		@Override
		public Set entrySet() {
			return null;
		}
		
		@Override
		public Boolean get(Object key) {
			if ( key != null ) {
				String value = key.toString();
				String[] roles = StringUtils.split(value, ", " );
				FacesContext ctx = FacesContext.getCurrentInstance();
				for( String role : roles ) {
					if ( ctx.getExternalContext().isUserInRole(role) ) {
						return Boolean.TRUE;
					}
				}				
			}
			return Boolean.FALSE;
		}
		
	}
	
}
