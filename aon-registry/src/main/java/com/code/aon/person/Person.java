package com.code.aon.person;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
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

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
			return (int) (CommonUtil.getDaysBetweenDates(getBirthDate(), new Date(), false) / 365);
		}
		return null;
	}
	
	@Transient
	public boolean isValidSSNumber(){
		try {
			if( StringUtils.isEmpty(getSocialSecurityNumber()) || !getValidSSNumber(getSocialSecurityNumber()).isEmpty()){
				return false;
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	private String getValidSSNumber(String ssNumber) throws IllegalArgumentException {
		Integer iDC = null;
		Integer iDCTemp = null;
		String sNumSegSocialTemp = null; 
		String sTempNumOriginal = null;
		if (ssNumber.length() != 12) {
	        throw new IllegalArgumentException("La longitud no es correcta.");
	    }
	    if (ssNumber.matches("[^0-9]")) {
	        throw new IllegalArgumentException("El valor debe ser numerico.");
	    }
	    iDCTemp = Integer.parseInt( ssNumber.substring(ssNumber.length()-2, ssNumber.length()) );
	    sTempNumOriginal = ssNumber.substring(0, ssNumber.length()-2);
	    if (ssNumber.charAt(2)=='0') {
	    	sNumSegSocialTemp = ssNumber.substring(0, 2) + ssNumber.substring(3, 10);
	    } else {
	    	sNumSegSocialTemp = ssNumber.substring(0, 10);
	    }	    
	    iDC = (int)(Long.parseLong(sNumSegSocialTemp) % 97);
	    if (iDC == iDCTemp) {
	        return "";
	    } else {
	    	return sTempNumOriginal + iDC.toString();
	    }
	}
	

}