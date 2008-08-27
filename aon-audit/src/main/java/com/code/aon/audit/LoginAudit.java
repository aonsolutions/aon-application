package com.code.aon.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the login audit.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "LOGIN_AUDIT", schema = "AUDIT")
@SequenceGenerator(name="LOGIN_AUDIT_GENERATOR", sequenceName="SEQ_LOGIN_AUDIT",allocationSize=1)
public class LoginAudit implements ITransferObject {

	private static final long serialVersionUID = -1655581325873540066L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "LOGIN_AUDIT_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "SESSION_ID", nullable = false, length = 32)
	@Index(name="IDX_LOGIN_AUDIT_SESSION_ID")
    private String sessionId;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="USER", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_LOGIN_AUDIT_USER")
	private User user;

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date start;

	@Temporal(TemporalType.DATE)
	private Date end;
	
    /**
     * The empty constructor.
     */
    public LoginAudit() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public LoginAudit(Integer id) {
        this.id = id;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
	public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the session id.
     * 
     * @return the session id
     */
	public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session id.
     * 
     * @param name the session id
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param domain the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Sets the start.
	 * 
	 * @param start the new start
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Sets the end.
	 * 
	 * @param end the new end
	 */
	public void setEnd(Date end) {
		this.end = end;
	}	

}