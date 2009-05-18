package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.marketing.enumeration.Operator;


/**
 * Transfer Object that represents the Survey Workflow.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_workflow")
public class SurveyWorkflow extends ValueHolder {

	private static final long serialVersionUID = -6533787102207004333L;

	private Operator operator;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="questionValue", updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_QUESTION_VALUE")
	@Index(name = "IDX_SURVERY_WORKFLOW_QUESTION_VALUE")
	private QuestionValue questionValue;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="surveyQuestion", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_SURVERY_QUESTION")
	@Index(name = "IDX_SURVERY_WORKFLOW_SURVERY_QUESTION")
	private SurveyQuestion surveyQuestion;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="nextSurveyQuestion", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_NEXT_SURVERY_QUESTION")
	@Index(name = "IDX_SURVERY_WORKFLOW_NEXT_SURVERY_QUESTION")
	private SurveyQuestion nextSurveyQuestion;
	
	/**
	 * Gets the question value.
	 * 
	 * @return the question value
	 */
	public QuestionValue getQuestionValue() {
		return questionValue;
	}

	/**
	 * Sets the question value.
	 * 
	 * @param questionValue the new question value
	 */
	public void setQuestionValue(QuestionValue questionValue) {
		this.questionValue = questionValue;
	}

	/**
	 * Gets the survey question.
	 * 
	 * @return the survey question
	 */
	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}

	/**
	 * Sets the survey question.
	 * 
	 * @param surveyQuestion the new survey question
	 */
	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

	/**
	 * Gets the next survey question.
	 * 
	 * @return the next survey question
	 */
	public SurveyQuestion getNextSurveyQuestion() {
		return nextSurveyQuestion;
	}

	/**
	 * Sets the next survey question.
	 * 
	 * @param nextSurveyQuestion the new next survey question
	 */
	public void setNextSurveyQuestion(SurveyQuestion nextSurveyQuestion) {
		this.nextSurveyQuestion = nextSurveyQuestion;
	}
	
	/**
	 * Gets the question.
	 * 
	 * @return the question
	 */
	@Transient
	public Question getQuestion() {
		return (surveyQuestion != null) ? surveyQuestion.getQuestion() : null;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SurveyWorkflow o = (SurveyWorkflow) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(getDate(), o.getDate())
				.append(getNumber(), o.getNumber())				
				.append(getText(), o.getText())			
				.append(this.nextSurveyQuestion, o.nextSurveyQuestion)
				.append(this.operator, o.operator)				
				.append(this.questionValue, o.questionValue)
				.append(this.surveyQuestion, o.surveyQuestion)
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
			.append(getText())		
			.append(nextSurveyQuestion)
			.append(operator)
			.append(questionValue)
			.append(surveyQuestion)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}