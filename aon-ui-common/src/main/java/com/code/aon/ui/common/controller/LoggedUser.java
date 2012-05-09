package com.code.aon.ui.common.controller;


import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.hibernate.Query;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.jaas.auth.AuthPrincipal;

/**
 * The Class LoggedUser.
 */
public class LoggedUser {

	/** The logged. */
	private boolean logged;
	
	/** The user name. */
	private String userName;
	
	/** The company name. */
	private String companyName;
	
	/** The principal. */
	private AuthPrincipal principal;
	
	/**
	 * Instantiates a new logged user.
	 */
	public LoggedUser() {
//		this.principal = BasicPrincipal.getAuthPrincipal();
		Principal p =  FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		this.principal = (AuthPrincipal) p;
		if ( principal != null ) {
			this.logged = true;
			initVariables(principal);
		}
	}

    private void initVariables( AuthPrincipal principal ) {
		this.userName = principal.getShortName();
		
		String sessionFactoryName = HibernateUtil.getSessionFactoryName("com.code.aon.config.User");
		String q = "SELECT name FROM User u  WHERE u.login = '" + principal.getShortName() + "'";
		if (principal.getDomainId() != null) {
			q = q + " AND u.domain = " + principal.getDomainId();
		}
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
		List<?> queryList = query.list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			this.userName = (String) iterator.next();
		}
		
		if (principal.getDomainId() != null) {
    		sessionFactoryName = HibernateUtil.getSessionFactoryName("com.code.aon.company.Company");
    		q = "SELECT name FROM Company c  WHERE c.domain = "  + principal.getDomainId();
    		query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
    		queryList = query.list();
    		iterator = queryList.iterator();
    		if (iterator.hasNext()) {
    			this.companyName = (String) iterator.next();
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
	 * Gets the principal.
	 *
	 * @return the principal
	 */
	public AuthPrincipal getPrincipal() {
		return principal;
	}
	
}
