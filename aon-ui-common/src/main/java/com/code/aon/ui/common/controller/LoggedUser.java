package com.code.aon.ui.common.controller;

import java.util.AbstractMap;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class LoggedUser.
 */
public class LoggedUser implements ILdapConstants, IAonObjectClasses {

	/** The logged. */
	private boolean logged;
	
	/** The user name. */
	private String userName;
	
	/** The company name. */
	private String companyName;
	
	/** The principal. */
	private AuthPrincipal principal;
	
	/** The Constant USER_IN_ROLE. */
	private static final FakeMap USER_IN_ROLE = new FakeMap();
	
	/**
	 * Instantiates a new logged user.
	 */
	public LoggedUser() {
		this.principal = BasicPrincipal.getAuthPrincipal();
		if ( principal != null ) {
			this.logged = true;
			initVariables(principal);
		}
	}

	/**
	 * Gets the aon user.
	 *
	 * @param principal the principal
	 * @return the aon user
	 */
	private Entry getAonUser( AuthPrincipal principal ) {
		if (! AonUtil.isSkipLdap() ) {
			BasicLdap ldap = new BasicLdap();
			Name dn = NameResolver.getUserDN(principal.getDomain(), principal.getShortName());
			if ( ldap.exists(dn, USER) ) {
				return ldap.get(dn, USER, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE, ORGANIZATION_NAME_ATTRIBUTE );	
			}
		}
		return null;
	}		

	/**
	 * Gets the aon domain.
	 *
	 * @param principal the principal
	 * @return the aon domain
	 */
	private Entry getAonDomain( AuthPrincipal principal ) {
		if (! AonUtil.isSkipLdap() ) {
			BasicLdap ldap = new BasicLdap();
			Name dn = NameResolver.getDomainDN(principal.getDomain());
			if ( ldap.exists(dn, DOMAIN) ) {
				return ldap.get(dn, DOMAIN, PARENT_DOMAIN_ATTRIBUTE, ORGANIZATION_NAME_ATTRIBUTE );
			}			
		}
		return null;
	}		
	
    /**
     * Inits the variables.
     *
     * @param principal the principal
     */
    private void initVariables( AuthPrincipal principal ) {
    	Entry user = getAonUser( principal );
    	if ( user != null ) {
        	this.userName = user.getAsString(COMMON_NAME_ATTRIBUTE);
        	if (user.containsKey(SURNAME_ATTRIBUTE) ) {
        		this.userName += " " + user.getAsString(SURNAME_ATTRIBUTE);
        	}
           	if (user.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
           		companyName = user.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
           	}    	
    	} else {
    		this.userName = principal.getShortName();
    	}
       	Entry domain = getAonDomain( principal );
       	if ( (domain != null) && domain.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
           	if (domain.containsKey(PARENT_DOMAIN_ATTRIBUTE) ) {
           		companyName = domain.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
        	}    	        		
           	if ( StringUtils.isBlank(companyName) ) {
           		companyName = domain.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
           	}
       	}
    }

    /**
     * Checks if is logged.
     *
     * @return true, if is logged
     */
    public boolean isLogged(){
    	return logged;
    }    
        
    /**
     * Gets the logged user name.
     *
     * @return the logged user name
     */
    public String getLoggedUserName() {
        return userName;
    }    

    /**
     * Sets the logged user name.
     *
     * @param userName the new logged user name
     */
    public void setLoggedUserName( String userName ) {
        this.userName = userName;
    }    
    
    /**
     * Gets the company name.
     *
     * @return the company name
     */
    public String getCompanyName(){
    	return companyName;
    }
    
	/**
	 * Sets the company name.
	 *
	 * @param companyName the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Gets the user in role.
	 *
	 * @return the user in role
	 */
	public FakeMap getUserInRole() {
		return USER_IN_ROLE;
	}

	/**
	 * Gets the principal.
	 *
	 * @return the principal
	 */
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	/**
	 * The Class FakeMap.
	 */
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
