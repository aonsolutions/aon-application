package com.code.aon.ui.common.controller;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;

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
     * Inits the variables.
     *
     * @param principal the principal
     */
    private void initVariables( AuthPrincipal principal ) {
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
    		Query query = session.createQuery(q).setMaxResults(1);
			this.userName = (String) query.uniqueResult();
		}
		if ( StringUtils.isEmpty(this.userName) ) {
    		this.userName = principal.getShortName();	
		}

		if (principal.getDomainId() != null) {
    		String sessionFactoryName = HibernateUtil.getSessionFactoryName("com.code.aon.company.Company");
    		String q = "SELECT name FROM Company c  WHERE c.domain = ?";
    		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q).setMaxResults(1);
			this.companyName = (String) query.setInteger(0, principal.getDomainId()).uniqueResult();
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
