package com.code.aon.ui.common.controller;

import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;

/**
 * The Class LoggedUser.
 */
public class LoggedUser implements Serializable {

	private static final String COMPANY_CLASS = "com.code.aon.company.Company";

	private static final String USER_CLASS = "com.code.aon.config.User";

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LoggedUser.class);
	
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
			updateLastAccess(principal);
		}
	}
		
    /**
     * Inits the variables.
     *
     * @param principal the principal
     */
    private void initVariables( AuthPrincipal principal ) {
    	String sessionFactoryName = HibernateUtil.getSessionFactoryName(USER_CLASS);
		Session session = HibernateUtil.getSession(sessionFactoryName); 
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
		HibernateUtil.closeSession(sessionFactoryName, false);
		if ( StringUtils.isEmpty(this.userName) ) {
    		this.userName = principal.getShortName();	
		}

		if (principal.getDomainId() != null) {
    		sessionFactoryName = HibernateUtil.getSessionFactoryName(COMPANY_CLASS);
    		String q = "SELECT name FROM Company c  WHERE c.domain = ?";
    		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q).setMaxResults(1);
			this.companyName = (String) query.setInteger(0, principal.getDomainId()).uniqueResult();
			HibernateUtil.closeSession(sessionFactoryName, false);
		}
    }
    
    private Timestamp getSessionCreatedTimestamp() {
    	Timestamp result = null;
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	if ( session != null ) {
    		result = new Timestamp(session.getCreationTime());
    	} else {
    		result = new Timestamp(new Date().getTime());
    	}
    	return result;
    }

	private void updateLastAccess( AuthPrincipal principal ) {
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), DomainManager.getCurrentDomain());
		try {
			ctx.getDslContext().update(USER)
			.set(USER.LASTACCESS, getSessionCreatedTimestamp() )
			.where(USER.ID.eq(principal.getUserId()))
			.execute();	
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();	
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
