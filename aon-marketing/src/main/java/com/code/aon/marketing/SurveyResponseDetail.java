package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


/**
 * Transfer Object that represents the Survey response detail.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_response_detail")
public class SurveyResponseDetail extends ValueHolder {

	private static final long serialVersionUID = -6982817430708927980L;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="surveyResponse", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE")
	private SurveyResponse surveyResponse;
	
    /**
     * The empty constructor.
     */
    public SurveyResponseDetail() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public SurveyResponseDetail(Integer id) {
    	super(id);
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
    
	
}