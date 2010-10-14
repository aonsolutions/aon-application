package com.code.aon.marketing;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;


/**
 * Transfer Object that represents the Survey response.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_response")
public class SurveyResponse implements ITransferObject {

	private static final long serialVersionUID = -8359279749317851837L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
    private Date creationDate;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "response_date", nullable = false)
    private Date date;	
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="survey", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_RESPONSE_SURVEY")
	@Index(name = "IDX_SURVERY_RESPONSE_SURVEY")
	private Survey survey;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="target", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_RESPONSE_TARGET")
	@Index(name = "IDX_SURVERY_RESPONSE_TARGET")
	private Target target;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="user", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_RESPONSE_USER")
	@Index(name = "IDX_SURVERY_RESPONSE_USER")
	private User user;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="campaign_action" )	
	@ForeignKey(name = "FK_MK_ACTION_TARGET_MK_ACTION")
	@Index(name = "IDX_MK_ACTION_TARGET_MK_ACTION")
	private MarketingAction action;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "surveyResponse")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<SurveyResponseDetail> details = new LinkedList<SurveyResponseDetail>();	
	
    /**
     * The empty constructor.
     */
    public SurveyResponse() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public SurveyResponse(Integer id) {
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
	 * Gets the survey.
	 * 
	 * @return the survey
	 */
	public Survey getSurvey() {
		return survey;
	}

	/**
	 * Sets the survey.
	 * 
	 * @param survey the new survey
	 */
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	/**
	 * Gets the creation date.
	 * 
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date.
	 * 
	 * @param creationDate the new creation date
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 * 
	 * @param date the new date
	 */
	public void setDate(Date date) {
		this.date = date;
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
	 * @param user the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public MarketingAction getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action the new action
	 */
	public void setAction(MarketingAction action) {
		this.action = action;
	}
	
	/**
	 * Gets the details.
	 * 
	 * @return the details
	 */
	public List<SurveyResponseDetail> getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 * 
	 * @param details the new details
	 */
	public void setDetails(List<SurveyResponseDetail> details) {
		this.details = details;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SurveyResponse o = (SurveyResponse) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.action, o.action)
				.append(this.creationDate, o.creationDate)				
				.append(this.date, o.date)
				.append(this.survey, o.survey)
				.append(this.target, o.target)				
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(action)
			.append(creationDate)
			.append(date)							
			.append(id)
			.append(survey)			
			.append(target)
			.append(user)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}