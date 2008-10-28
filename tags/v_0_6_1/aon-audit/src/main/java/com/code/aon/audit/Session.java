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
 * Transfer Object that represents the session.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "SESSION", schema = "AUDIT")
@SequenceGenerator(name="SESSION_GENERATOR", sequenceName="SEQ_SESSION",allocationSize=1)
public class Session implements ITransferObject {

	private static final long serialVersionUID = -1655581325873540066L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "SESSION_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "SESSION_ID", nullable = false, length = 32)
	@Index(name="IDX_SESSION_SESSION_ID")
    private String sessionId;

	@Column(name = "REMOTE_ADDRESS", nullable = false, length = 15)
    private String remoteAddress;

	@Column(name = "REMOTE_HOST", nullable = false, length = 64)
    private String remoteHost;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="APPLICATION_ID", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SESSION_APPLICATION")
	private Application application;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="USER_ID", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SESSION_USER")
	private User user;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
    /**
     * The empty constructor.
     */
    public Session() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Session(Integer id) {
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
	 * Gets the application.
	 * 
	 * @return the application
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 * 
	 * @param application the new application
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start.
	 * 
	 * @param start the new start
	 */
	public void setStartDate(Date start) {
		this.startDate = start;
	}

	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end.
	 * 
	 * @param end the new end
	 */
	public void setEndDate(Date end) {
		this.endDate = end;
	}

	/**
	 * Gets the remote address.
	 * 
	 * @return the remote address
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Sets the remote address.
	 * 
	 * @param remoteAddress the new remote address
	 */
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	/**
	 * Gets the remote host.
	 * 
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote host.
	 * 
	 * @param remoteHost the new remote host
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}	
	
}