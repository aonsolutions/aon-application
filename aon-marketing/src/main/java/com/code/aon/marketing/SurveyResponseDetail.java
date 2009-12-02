package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the Survey response detail.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_response_detail")
public class SurveyResponseDetail extends ValueQuestionHolder {

	private static final long serialVersionUID = -6982817430708927980L;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="surveyResponse", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE")
	@Index(name = "IDX_SURVEY_RESPONSE_DETAIL_SURVEY_RESPONSE")
	private SurveyResponse surveyResponse;
	
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
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SurveyResponseDetail o = (SurveyResponseDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(getDate(), o.getDate())
				.append(getNumber(), o.getNumber())				
				.append(getQuestion(), o.getQuestion())
				.append(getText(), o.getText())				
				.append(surveyResponse, o.surveyResponse)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getDate())
			.append(getNumber())
			.append(getId())	
			.append(getQuestion())			
			.append(getText())		
			.append(surveyResponse)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
