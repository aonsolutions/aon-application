package com.code.aon.ui.common.controller;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
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
	
	/**
	 * Instantiates a new logged user.
	 */
	public LoggedUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
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
    		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName("com.code.aon.config.User")); 
    		if ( principal.getUserId() != null ) {
    			Query query = session.createQuery("SELECT name FROM User u WHERE u.id = ?");
    			query.setInteger(0, principal.getUserId());
    			this.userName = (String) query.uniqueResult();
    		} else {
        		String q = "SELECT name FROM User u  WHERE u.login = '" + principal.getShortName() + "'";
        		if (principal.getDomainId() != null) {
        			q = q + " AND u.domain = " + principal.getDomainId();
        		}
    			this.userName = (String) session.createQuery(q).uniqueResult();
    		}
    		if ( StringUtils.isEmpty(this.userName) ) {
        		this.userName = principal.getShortName();	
    		}
    	}
       	Entry domain = getAonDomain( principal );
       	if ( (domain != null) && domain.containsKey(ORGANIZATION_NAME_ATTRIBUTE) ) {
           	if (domain.containsKey(PARENT_DOMAIN_ATTRIBUTE) ) {
           		this.companyName = domain.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
        	}    	        		
           	if ( StringUtils.isBlank(companyName) ) {
           		this.companyName = domain.getAsString(ORGANIZATION_NAME_ATTRIBUTE);
           	}
       	} else {
    		if (principal.getDomainId() != null) {
	    		String sessionFactoryName = HibernateUtil.getSessionFactoryName("com.code.aon.company.Company");
	    		String q = "SELECT name FROM Company c  WHERE c.domain = ?";
	    		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
    			this.companyName = (String) query.setInteger(0, principal.getDomainId()).uniqueResult();
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
	
}
