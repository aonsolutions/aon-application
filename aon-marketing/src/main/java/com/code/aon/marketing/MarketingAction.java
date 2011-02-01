package com.code.aon.marketing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.marketing.enumeration.ActionMediaType;


/**
 * Transfer Object that represents the Action.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "mk_action")
public class MarketingAction implements ITransferObject {

	private static final long serialVersionUID = 4581317654783219721L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="campaign", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_MK_ACTION_MK_CAMPAIGN")
	@Index(name = "IDX_MK_ACTION_MK_CAMPAIGN")
	private Campaign campaign;
	
	@Column( name = "media_type", nullable = false)
	private ActionMediaType mediaType;	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
    private Date startDate;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date" )
    private Date endDate;	

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="survey" )	
	@ForeignKey(name = "FK_MK_ACTION_SURVEY")
	@Index(name = "IDX_MK_ACTION_SURVEY")
	private Survey survey;
	
    /**
     * The empty constructor.
     */
    public MarketingAction() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public MarketingAction(Integer id) {
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
	 * Gets the campaign.
	 * 
	 * @return the campaign
	 */
	public Campaign getCampaign() {
		return campaign;
	}

	/**
	 * Sets the campaign.
	 * 
	 * @param campaign the new campaign
	 */
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	/**
	 * Gets the media type.
	 * 
	 * @return the media type
	 */
	public ActionMediaType getMediaType() {
		return mediaType;
	}

	/**
	 * Sets the media type.
	 * 
	 * @param mediaType the new media type
	 */
	public void setMediaType(ActionMediaType mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the new start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the new end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final MarketingAction o = (MarketingAction) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.campaign, o.campaign)
				.append(this.endDate, o.endDate)				
				.append(this.mediaType, o.mediaType)
				.append(this.startDate, o.startDate)				
				.append(this.survey, o.survey)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(campaign)
			.append(endDate)
			.append(id)	
			.append(mediaType)			
			.append(startDate)
			.append(survey)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}