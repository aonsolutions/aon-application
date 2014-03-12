package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.ReservationRequestGuestDB;

@Entity
@Table(name="reservation_request_guest")
public class ReservationRequestGuest extends ReservationRequestGuestDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public String getFullName() {
    	String fullName = StringUtils.isEmpty(getName()) ? "" : getName() + " ";
    	fullName += StringUtils.isEmpty(getSurname()) ? "" : getSurname() + " ";
    	return fullName;
	}

    @Transient
	public String getFullAddress() {
    	String fullAddress = StringUtils.isEmpty(getAddress()) ? "" : getAddress() + " ";
    	fullAddress += StringUtils.isEmpty(getZip()) ? "" : getZip() + " - ";
    	fullAddress += StringUtils.isEmpty(getCity()) ? "" : getCity() + " ";
    	fullAddress += StringUtils.isEmpty(getProvince()) ? "" : "(" + getProvince() + ") ";
    	fullAddress += StringUtils.isEmpty(getCountry()) ? "" : getCountry();
    	return fullAddress;
	}
	
}