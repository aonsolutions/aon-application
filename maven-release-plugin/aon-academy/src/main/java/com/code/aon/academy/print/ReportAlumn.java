package com.code.aon.academy.print;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.registry.RegistryNote;

public class ReportAlumn implements ITransferObject {

	private Customer alumn;
	
	private String phone;
	
	private String cellular;
	
	private String courseCode;
	
	private Date birthDate;

	private RegistryNote observation;
	
	public Customer getAlumn() {
		return alumn;
	}

	public void setAlumn(Customer alumn) {
		this.alumn = alumn;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public RegistryNote getObservation() {
		return observation;
	}

	public void setObservation(RegistryNote observation) {
		this.observation = observation;
	}
	
	
}