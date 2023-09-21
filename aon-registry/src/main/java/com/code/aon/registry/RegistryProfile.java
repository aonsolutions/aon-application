package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.registry.enumeration.QuestionType;
import com.esferalia.aon.entity.master.RegistryProfileDB;

@Entity
@Table(name="rprofile")
public class RegistryProfile extends RegistryProfileDB implements IValueHolder {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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