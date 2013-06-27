package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.registry.enumeration.QuestionType;
import com.esferalia.aon.entity.master.QuestionValueDB;

@Entity
@Table(name="question_value")
public class QuestionValue extends QuestionValueDB implements IValueHolder {

	private static final long serialVersionUID = 1L;
	
	@Transient
	private Boolean getBooleanValue() {
		if ( getNumber() != null ) {
			return (getNumber() != 0);
		}
		return null;
	}
	
	@Transient
	public boolean isBoolean() {
		return (getNumber() != null) && (getNumber() != 0);
	}
	public void setBoolean(boolean b) {
		setNumber(b ? 1.0 : 0);
	}

	@Transient
	public boolean isNotFilled() {
		return (getNumber() == null) && (getDate() == null) && StringUtils.isEmpty(getText());
	}
	
	public Object getValue( QuestionType type ) {
		switch ( type ) {
			case BOOLEAN:
				return getBooleanValue();
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
	
	public void copyValues( IValueHolder vh ) {
		vh.setDate( getDate() );
		vh.setNumber( getNumber() );
		vh.setText( getText() );
	}
	
}