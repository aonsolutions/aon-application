package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.registry.IValueHolder;
import com.code.aon.registry.Question;
import com.code.aon.registry.enumeration.QuestionType;
import com.esferalia.aon.entity.master.SurveyWorkflowDB;

@Entity
@Table(name="survey_workflow")
public class SurveyWorkflow extends SurveyWorkflowDB implements IValueHolder{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public Question getQuestion() {
		return (getSurveyQuestion() != null) ? getSurveyQuestion().getQuestion() : null;
	}	
	
	@Transient
	public Boolean getBooleanValue() {
		if ( getNumber() != null ) {
			return (getNumber() != 0);
		}
		return null;
	}
	
	public void setBooleanValue(Boolean b) {
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