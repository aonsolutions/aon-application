package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.IValueHolder;
import com.code.aon.commercial.Question;
import com.code.aon.commercial.enumeration.QuestionType;
import com.esferalia.aon.entity.master.SurveyWorkflowDB;

@Entity
@Table(name="survey_workflow")
public class SurveyWorkflow extends SurveyWorkflowDB implements IValueHolder{

	private static final long serialVersionUID = 1L;
	
	@Transient
	public Question getQuestion() {
		return (getSurveyQuestion() != null) ? getSurveyQuestion().getQuestion() : null;
	}	
	
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