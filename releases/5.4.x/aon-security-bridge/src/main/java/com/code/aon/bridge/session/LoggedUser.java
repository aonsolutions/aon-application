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

	public static final String LOGGED_USER = "loggedUser";
	
	private boolean logged;
	
	private String userName;
	
	private String companyName;
	
	private AuthPrincipal principal;
	
	private static final FakeMap USER_IN_ROLE = new FakeMap();
	
	public LoggedUser() {
		this.principal = Utils.getAuthPrincipal();
		if ( (principal != null) && (!IConstants.UNAUTHENTICATED_IDENTITY.equals(principal.getName())) ) {
			this.logged = true;
			initVariables(principal);
		}
	}

	private Entry getAonUser( AuthPrincipal principal ) {
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getUserDN(principal.getDomain(), principal.getShortName());
		return ldap.get(dn, USER, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE, ORGANIZATION_NAME_ATTRIBUTE );
	}		

	private Entry getAonDomain( AuthPrincipal principal ) {
		BasicLdap ldap = new BasicLdap();
		Name dn = NameResolver.getDomainDN(principal.getDomain());
		return ldap.get(dn, DOMAIN, PARENT_DOMAIN_ATTRIBUTE, ORGANIZATION_NAME_ATTRIBUTE );
	}		
	
    private void initVariables( AuthPrincipal principal ) {
    	Entry user = getAonUser( principal );
    	if ( user != null ) {
        	this.userName = user.getAsString(COMMON_NAME_ATTRIBUTE);
        	if (user.containsKey(SURNAME_ATTRIBUTE) ) {
        		this.userName += " " + user.getAsString(SURNAME_ATTRIBUTE);
        	}
        	Entry domain = getAonDomain( principal );
        	if ( domain != null ) {
            	if (domain.containsKey(PARENT_DOMAIN_ATTRIBUTE) && domain.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
            		companyName = domain.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
            	}    	        		
        	}
        	if ( StringUtils.isBlank(companyName) ) {
            	if (user.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
            		companyName = user.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
            	}    	
        	}
    	}
    }

    public boolean isLogged(){
    	return logged;
    }    
        
    public String getLoggedUserName() {
        return userName;
    }    
    
    public String getCompanyName(){
    	return companyName;
    }
    
	public FakeMap getUserInRole() {
		return USER_IN_ROLE;
	}

	public AuthPrincipal getPrincipal() {
		return principal;
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
