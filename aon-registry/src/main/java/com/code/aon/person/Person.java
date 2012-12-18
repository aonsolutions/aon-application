package com.code.aon.person;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.IRegistry;
import com.esferalia.aon.entity.master.PersonDB;

@Entity
@Table(name="person")
@Heritable
public class Person extends PersonDB implements IRegistry {

	private static final long serialVersionUID = 1L;
	
	public Person() {
		setGender(Gender.UNKNOWN);
		setMaritalStatus(MaritalStatus.UNKNOWN);
	}

	@Transient
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (!StringUtils.isEmpty(getFirstSurname())) {
			sb.append(getFirstSurname());
		}
		if (!StringUtils.isEmpty(getSecondSurname())) {
			sb.append(" ").append(getSecondSurname());
		}
		if (!StringUtils.isEmpty(getName())) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(getName());
		}
		return sb.toString();
	}

	@Transient
	public void setFullName(String value) {
		// Necesario para que no falle en los lookup
	}

	@Transient
	public Integer getAge() {
		if(getBirthDate()!=null){
			return (int) (CommonUtil.getDaysBetweenDates(getBirthDate(), new Date(), true) / 365);
		}
		return null;
	}
	

}