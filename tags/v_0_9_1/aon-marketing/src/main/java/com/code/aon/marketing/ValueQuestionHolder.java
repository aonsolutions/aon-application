package com.code.aon.marketing;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;


/**
 * Transfer Object that represents a Value Holder.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@MappedSuperclass
public abstract class ValueQuestionHolder extends ValueHolder {

	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="question", nullable = false, updatable = false )	
	private Question question;

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
	 * @param application the new question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}
	
}