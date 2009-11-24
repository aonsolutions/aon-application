package com.code.aon.marketing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the Survey Workflow.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey_workflow")
public class SurveyWorkflow implements ITransferObject {

	private static final long serialVersionUID = -6533787102207004333L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="questionValue", updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_QUESTION_VALUE")
	private QuestionValue questionValue;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="surveyQuestion", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_SURVERY_QUESTION")
	private SurveyQuestion surveyQuestion;

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="nextSurveyQuestion", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SURVERY_WORKFLOW_NEXT_SURVERY_QUESTION")
	private SurveyQuestion nextSurveyQuestion;
	
    /**
     * The empty constructor.
     */
    public SurveyWorkflow() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public SurveyWorkflow(Integer id) {
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
	
}