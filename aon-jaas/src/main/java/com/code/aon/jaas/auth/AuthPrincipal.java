package com.code.aon.jaas.auth;

import static com.code.aon.jaas.auth.IConstants.CONTEXT_SEPARATOR;
import static com.code.aon.jaas.auth.IConstants.IDENTITY_SEPARATOR;

import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;

/**
 * Esta clase implementa el interfaz <code>Principal</code> y representa un usuario.
 * <p>
 * Principals como <code>AuthPrincipal</code> se asocian con un <code>Subject</code> 
 * para añadir al <code>Subject</code> con una identidad adicional.
 * 
 * @version 1.4, 01/11/00
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02-jun-2004
 * @since 1.0
 */
public class AuthPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;

    /** Principal shortName */
    private String shortName;
    /** Principal domain. ej: code.es */
    private String domain;
    /** Principal context */
    private String context;
    private String databaseName;
    private Integer domainId;
    private Integer userId;
    private Integer applicationId;
    private Integer userDomainId;    
    private String initAction;

    /**
     * Constructs an <code>AuthPrincipal</code>
     * <p>
     * @param name nombre del usuario.
	 */
	public AuthPrincipal(String shortName) {
        if (shortName == null) {
            throw new NullPointerException("illegal null input");
        }
        this.shortName = shortName;
	}

    /**
     * Returns user name: shortName@domain/context
     * <p>
     * @return user name
     * @see java.security.Principal#getName()
     */
    public String getName() {
    	return shortName + IDENTITY_SEPARATOR + domain + CONTEXT_SEPARATOR + context;
    }

	/**
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

    /**
	 * @return Returns the context.
	 */
	public String getContext() {
		return context;
	}

	/**
     * Returns a string representation of the object. 
     * <p>
     * @return string representation of the object.
     * @see java.security.Principal#toString()
     */
    public String toString() {
        return getName();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Returns true 
     * if given object is equal tp this <code>AuthPrincipal</code> and both have 
     * the same user.
     * <p>
     * @param o the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument; 
     * 		<code>false</code> otherwise.
     * @see java.security.Principal#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Principal)) {
            return false;
        }
        String anotherName = ((Principal) o).getName();
        return StringUtils.equals(getName(), anotherName);
    }

    public int hashCode() {
        return getName().hashCode();
    }

	public Integer getDomainId() {
		return domainId;
	}
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getUserDomainId() {
		return userDomainId;
	}

	public void setUserDomainId(Integer userDomainId) {
		this.userDomainId = userDomainId;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public String getInitAction() {
		return initAction;
	}
	
	public void setInitAction(String initAction) {
		this.initAction = initAction;
	}
	
}