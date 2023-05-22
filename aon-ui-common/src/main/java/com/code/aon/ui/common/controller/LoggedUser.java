package com.code.aon.ui.common.controller;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

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
	
	private boolean skipConcurrentCheck;
	
	/**
	 * Instantiates a new logged user.
	 */
	public LoggedUser() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		if ( principal != null ) {
			this.logged = true;
			initVariables(principal);
			if (! isAdminUser(principal) ) {
				updateLastAccess(principal);	
				this.skipConcurrentCheck = calculateSkipConcurrentCheck(principal);
			} else {
				this.skipConcurrentCheck = true;
			}
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
    	}
    	return result;
    }
    
    private Timestamp getUserLastAccess() {
    	Timestamp ts = null;
    	AuthPrincipal principal = AonUtil.getAuthPrincipal();
		try(CloseableAONContext ctx = AONContext.getAONContext(principal.getDomain(), principal.getDomainId())) {
			ts = ctx.getDslContext()
					.select(USER.LASTACCESS)
					.from(USER)
					.where(USER.ID.eq(principal.getUserId()))
					.fetchOne(0, Timestamp.class);
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		}
		return ts;
    }

	private void updateLastAccess( AuthPrincipal principal ) {
		CloseableAONContext ctx = AONContext.getAONContext(principal.getDomain(), principal.getDomainId());
		try {
			// Truncate timestamp due to Mysql round issue.
			// http://bugs.mysql.com/bug.php?id=68760
			Timestamp t = getSessionCreatedTimestamp();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t.getTime());
			c.set(Calendar.MILLISECOND, 0);
			t.setTime(c.getTimeInMillis());
			// -------------------------------------------
			
			ctx.getDslContext().update(USER)
			.set(USER.LASTACCESS, t )
			.where(USER.ID.eq(principal.getUserId()))
			.execute();	
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.close();	
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
	
	private boolean isAdminUser( AuthPrincipal principal ) {
		if ( StringUtils.endsWith(principal.getContext(), "*") ) {
			return true;
		}
		Integer type = AdminUtil.getDomainType(principal.getUserDomainId());
		return (type != null) && (type == DomainType.ADMIN.ordinal());

	}
	
	private boolean isAllowDomainConcurrent( String domain ) {
		boolean allow = false;
		Integer domainId = DomainManager.getCurrentDomain();
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, domainId)){
			String value = ctx.getDslContext() 
					.select(APP_PARAM.VALUE)
					.from(APP_PARAM)
					.where(APP_PARAM.DOMAIN.eq(domainId)
						.and(APP_PARAM.NAME.eq(AppParam.AON_ALLOW_CONCURRENT_DOMAIN.getValue())))
					.fetchOne(0, String.class);		
			allow = Boolean.valueOf(value);
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		}				    	
		return allow;
	}	

	private boolean isAllowUserConcurrent( AuthPrincipal principal ) {
		boolean allow = false;
		try(CloseableAONContext ctx = AONContext.getAONContext(principal.getDomain(), principal.getDomainId())) {
			allow = ctx.getDslContext() 
				.select(USER.ALLOWCONCURRENT)
				.from(USER)
				.where(USER.ID.eq(principal.getUserId())).fetchOne(0, Boolean.class);	
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} 			    	
		return allow;
	}	
	
	private boolean calculateSkipConcurrentCheck( AuthPrincipal principal ) {
		if ( isAllowDomainConcurrent(principal.getDomain()) || isAllowUserConcurrent(principal)  ) {
			return true;
		}
		return false;
	}

	public boolean isConcurrentSession() {
		if (! skipConcurrentCheck ) {
			Timestamp sessionCreated = getSessionCreatedTimestamp();
			if ( sessionCreated != null ) {
				Timestamp userLastAccess = getUserLastAccess();
				return (userLastAccess != null) && (userLastAccess.compareTo(sessionCreated) > 0);
			}			
		}
		return false;
	}	
	
}
