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

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the action.
 * 
 * @author Consulting & Development. Aimar Tellitu - 28-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "ACTION_EXECUTION", schema = "AUDIT")
@SequenceGenerator(name="ACTION_EXECUTION_GENERATOR", sequenceName="SEQ_ACTION_EXECUTION",allocationSize=1)
public class ActionExecution implements ITransferObject {

	private static final long serialVersionUID = -4428829681920654096L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "ACTION_EXECUTION_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="ACTION_ID", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_ACTION_EXECUTION_ACTION")
    private Action action;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="SESSION_ID", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_ACTION_EXECUTION_SESSION")
	private Session session;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date executionDate;
	
    /**
     * The empty constructor.
     */
    public ActionExecution() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public ActionExecution(Integer id) {
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
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action the new action
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Sets the session.
	 * 
	 * @param session the new session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Gets the execution date.
	 * 
	 * @return the execution date
	 */
	public Date getExecutionDate() {
		return executionDate;
	}

	/**
	 * Sets the execution date.
	 * 
	 * @param executionDate the new execution date
	 */
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
    
}