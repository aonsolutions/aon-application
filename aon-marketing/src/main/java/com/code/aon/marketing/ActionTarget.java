package com.code.aon.marketing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;


/**
 * Transfer Object that represents the Action Target.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "mk_action_target")
public class ActionTarget implements ITransferObject, IRegistry {

	private static final long serialVersionUID = 707375832150721627L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="action", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_MK_ACTION_TARGET_MK_ACTION")
	@Index(name = "IDX_MK_ACTION_TARGET_MK_ACTION")
	private Action action;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="target", nullable = false, updatable = false )	
	@Index(name = "IDX_MK_ACTION_TARGET_TARGET")
	private Target target;

	@Column(nullable = false)
	private ActionTargetStatus status;	
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="survey_response" )	
	@Index(name = "IDX_MK_ACTION_TARGET_SURVEY_RESPONSE")
	private SurveyResponse surveyResponse;	
	
    /**
     * The empty constructor.
     */
    public ActionTarget() {
    	this.status = ActionTargetStatus.PENDING;
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public ActionTarget(Integer id) {
    	this();
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
	 * Gets the target.
	 * 
	 * @return the target
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target the new target
	 */
	public void setTarget(Target target) {
		this.target = target;
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
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public ActionTargetStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the new status
	 */
	public void setStatus(ActionTargetStatus status) {
		this.status = status;
	}

	/**
	 * Gets the survey response.
	 * 
	 * @return the survey response
	 */
	public SurveyResponse getSurveyResponse() {
		return surveyResponse;
	}

	/**
	 * Sets the survey response.
	 * 
	 * @param surveyResponse the new survey response
	 */
	public void setSurveyResponse(SurveyResponse surveyResponse) {
		this.surveyResponse = surveyResponse;
	}

	@Override
	@Transient
	public Registry getRegistry() {
		return this.target.getRegistry();
	}

	@Override
	public void setRegistry(Registry registry) {
		this.target.setRegistry(registry);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ActionTarget o = (ActionTarget) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.action, o.action)
				.append(this.status, o.status)				
				.append(this.surveyResponse, o.surveyResponse)
				.append(this.target, o.target)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(action)
			.append(id)	
			.append(status)			
			.append(surveyResponse)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}