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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the user.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "question_value")
public class QuestionValue implements ITransferObject {

	private static final long serialVersionUID = -7135601793952520234L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Column(name = "value_text", length = 64)
    private String text;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "value_date")
    private Date date;
	
	@Column(name = "value_number")
	private Double number;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="question", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_QUESTION_VALUE_QUESTION")
	private Question question;

    /**
     * The empty constructor.
     */
    public QuestionValue() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public QuestionValue(Integer id) {
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
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 * 
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
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
	 * Gets the number.
	 * 
	 * @return the number
	 */
	public Double getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the new number
	 */
	public void setNumber(Double number) {
		this.number = number;
	}
	
	/**
	 * Checks if is boolean.
	 * 
	 * @return true, if is boolean
	 */
	@Transient
	public boolean isBoolean() {
		return this.number != 0;
	}

	/**
	 * Sets the boolean.
	 * 
	 * @param b the new boolean
	 */
	public void setBoolean(boolean b) {
		this.number = b ? 1.0 : 0;
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
	 * @param application the new question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}
    
}