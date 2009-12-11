package com.code.aon.marketing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the survey question.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_question")
public class SurveyQuestion implements ITransferObject {

	private static final long serialVersionUID = -8359279749317851837L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	private Integer position;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="survey", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_QUESTION_SURVEY")
	@Index(name = "IDX_SURVERY_QUESTION_SURVEY")
	private Survey survey;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="question", nullable = false )	
	@Index(name = "IDX_SURVERY_QUESTION_QUESTION")
	private Question question;
	
    /**
     * The empty constructor.
     */
    public SurveyQuestion() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public SurveyQuestion(Integer id) {
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
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Sets the position.
	 * 
	 * @param position the new position
	 */
	public void setPosition(Integer position) {
		this.position = position;
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
	 * Gets the question.
	 * 
	 * @return the question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * Sets the question.
	 * 
	 * @param question the new question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SurveyQuestion o = (SurveyQuestion) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.position, o.position)
				.append(this.question, o.question)				
				.append(this.survey, o.survey)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(position)
			.append(question)
			.append(survey)							
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}