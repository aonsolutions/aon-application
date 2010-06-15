package com.code.aon.marketing;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the survey.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "survey")
public class Survey implements ITransferObject {

	private static final long serialVersionUID = 8032426876935464774L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
    private Date creationDate;	
	
	@Column(nullable = false, length = 64)
    private String description;
	
	@Column(nullable = false)
	private boolean active;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "survey")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<SurveyQuestion> questions = new LinkedList<SurveyQuestion>();	
	
    /**
     * The empty constructor.
     */
    public Survey() {
    	setCreationDate( new Date() );
    	setActive( true );
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Survey(Integer id) {
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * Gets the questions.
	 * 
	 * @return the questions
	 */
	public List<SurveyQuestion> getQuestions() {
		return this.questions;
	}
	
	/**
	 * Sets the questions.
	 * 
	 * @param users the questions
	 */
	public void setQuestions( List<SurveyQuestion> questions ) {
		this.questions = questions;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Survey o = (Survey) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.creationDate, o.creationDate)				
				.append(this.description, o.description)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(creationDate)
			.append(description)			
			.append(id)				
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}