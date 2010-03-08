package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

public interface ITemporalDisabilityConfirmation extends Serializable{

	ITemporalDisability getTemporalDisability();
	void setTemporalDisability();
	
	Integer getNumber();
	void setNumber();
	
	Date getDate();
	void getDate(Date date);
	
	String getMedicalLicenseNumber();
	void setMedicalLicenseNumber(String medicalLicenseNumber);
	
	String getMedicalAreaId();
	void setMedicalAreaId(String medicalAreaId);
	
	boolean isProcessed();
	void setProcessed(boolean processed);
	
}
