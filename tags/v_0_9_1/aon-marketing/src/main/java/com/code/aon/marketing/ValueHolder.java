package com.code.aon.marketing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.marketing.enumeration.QuestionType;


/**
 * Transfer Object that represents a Value Holder.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@MappedSuperclass
public abstract class ValueHolder implements ITransferObject {

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Column(name = "value_text", length = 64)
    private String text;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "value_date")
    private Date date;
	
	@Column(name = "value_number", precision = 15, scale = 3)
	private Double number;

    /**
     * The empty constructor.
     */
    public ValueHolder() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public ValueHolder(Integer id) {
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
		return (this.number != null) && (this.number != 0);
	}

	/**
	 * Sets the boolean.
	 * 
	 * @param b the new boolean
	 */
	public void setBoolean(boolean b) {
		this.number = b ? 1.0 : 0;
	}

	@Transient
	public boolean isNotFilled() {
		return (number == null) && (date == null) && StringUtils.isEmpty(text);
	}
	
	/**
	 * Gets the value.
	 * 
	 * @param type the type
	 * 
	 * @return the value
	 */
	public Object getValue( QuestionType type ) {
		switch ( type ) {
			case BOOLEAN:
				return isBoolean();
			case DATE:
				return getDate();
			case NUMBER:
				return getNumber();
			case TEXT:
			case INFO:
				return getText();
		}
		return null;
	}
	
	public void copyValues( ValueHolder vh ) {
		vh.setDate( getDate() );
		vh.setNumber( getNumber() );
		vh.setText( getText() );
	}
	
}