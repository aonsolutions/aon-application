package com.code.aon.marketing;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.marketing.enumeration.QuestionType;


/**
 * Transfer Object that represents the question.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "question")
public class Question implements ITransferObject {

	private static final long serialVersionUID = -5694421598656533776L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Column(name = "question_text", nullable = false, length = 255)
    private String text;
	
	@Column(nullable = false)
	private QuestionType type;
	
	@Column(length = 1024)
    private String argument;	
	
	@Column(nullable = false)
	private boolean active;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "question")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<QuestionValue> values = new LinkedList<QuestionValue>();	
	
    /**
     * The empty constructor.
     */
    public Question() {
    	this.active = true;
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Question(Integer id) {
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public QuestionType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the new type
	 */
	public void setType(QuestionType type) {
		this.type = type;
	}

	/**
	 * Gets the argument.
	 * 
	 * @return the argument
	 */
	public String getArgument() {
		return argument;
	}

	/**
	 * Sets the argument.
	 * 
	 * @param argument the new argument
	 */
	public void setArgument(String argument) {
		this.argument = argument;
	}

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 * 
	 * @param active the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the values.
	 * 
	 * @return the values
	 */
	public List<QuestionValue> getValues() {
		return this.values;
	}
	
	/**
	 * Sets the values.
	 * 
	 * @param users the values
	 */
	public void setValues( List<QuestionValue> values ) {
		this.values = values;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Question o = (Question) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.argument, o.argument)				
				.append(this.text, o.text)
				.append(this.type, o.type)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(argument)
			.append(id)	
			.append(text)			
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}