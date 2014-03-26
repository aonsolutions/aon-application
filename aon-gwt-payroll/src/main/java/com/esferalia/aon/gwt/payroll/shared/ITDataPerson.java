package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ITDataPerson implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1519604556575978934L;
	
	public static enum Type implements HasDescription{
		COMMON_DISEASE("Enfermedad Com\u00FAn"),
		OCCUPATIONAL_DISEASE("Enfermedad Profesional"),
		MATERNITY("Maternidad"),
		PATERNITY("Paternidad"),
		PREGNANCY_RISK("Riesgo Durante Embarazo"),
		BREASTFEEDING_RISK("Lactancia Materna"),
		NON_OCCUPATIONAL_DISEASE("Enfermedad No Profesional");
		
		private String description;
		
		private Type(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return this.description;
		};
	}
	

	private int contractId;
	private Type type;	
	
	private Date leaveStartDate;
	private Date leaveEndDate;	
	
	public ITDataPerson() {
		
	}	

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public Date getLeaveStartDate() {
		return leaveStartDate;
	}

	public void setLeaveStartDate(Date start_date) {
		this.leaveStartDate = start_date;
	}

	public Date getLeaveEndDate() {
		return leaveEndDate;
	}

	public void setLeaveEndDate(Date end_date) {
		this.leaveEndDate = end_date;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}
